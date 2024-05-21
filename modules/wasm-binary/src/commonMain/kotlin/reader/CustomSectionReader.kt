package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.IndexName
import org.wasmium.wasm.binary.tree.LinkingKind
import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.tree.NameKind
import org.wasmium.wasm.binary.tree.NameKind.DATA
import org.wasmium.wasm.binary.tree.NameKind.ELEMENT
import org.wasmium.wasm.binary.tree.NameKind.FIELD
import org.wasmium.wasm.binary.tree.NameKind.FUNCTION
import org.wasmium.wasm.binary.tree.NameKind.GLOBAL
import org.wasmium.wasm.binary.tree.NameKind.LABEL
import org.wasmium.wasm.binary.tree.NameKind.LOCAL
import org.wasmium.wasm.binary.tree.NameKind.MEMORY
import org.wasmium.wasm.binary.tree.NameKind.MODULE
import org.wasmium.wasm.binary.tree.NameKind.TABLE
import org.wasmium.wasm.binary.tree.NameKind.TAG
import org.wasmium.wasm.binary.tree.NameKind.TYPE
import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class CustomSectionReader(
    private val context: ReaderContext,
) {
    public fun readCustomSection(source: WasmBinaryReader, payloadSize: UInt, visitor: ModuleVisitor) {
        val startPosition = source.position

        val sectionName = source.readString()
        val sectionPayloadSize = payloadSize - (source.position - startPosition)

        when {
            sectionName == SectionName.NAME.sectionName -> {
                context.nameOfSectionConsumed = true
                if (context.options.debugNames) {
                    readNamesSection(source, source.position, sectionPayloadSize, visitor)
                } else {
                    readUnknownSection(source, visitor, sectionName, startPosition, sectionPayloadSize)
                }
            }

            sectionName.startsWith(SectionName.RELOCATION.sectionName) -> {
                readRelocationSection(source, visitor)
            }

            sectionName == SectionName.LINKING.sectionName -> {
                readLinkingSection(source, visitor)
            }

            sectionName == SectionName.SOURCE_MAPPING_URL.sectionName -> {
                readSourceMapSection(source, visitor)
            }

            else -> {
                readUnknownSection(source, visitor, sectionName, startPosition, sectionPayloadSize)
            }
        }
    }

    private fun readUnknownSection(source: WasmBinaryReader, visitor: ModuleVisitor, customSectionName: String, startPosition: UInt, sectionPayloadSize: UInt) {
        val payload = ByteArray(sectionPayloadSize.toInt())
        source.readTo(payload, 0u, sectionPayloadSize)

        val unknownSectionVisitor = visitor.visitUnknownSection(customSectionName, payload)
        unknownSectionVisitor.visitEnd()
    }

    private fun readSourceMapSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val sourceMapURL = source.readString()

        visitor.visitSourceMapSection(sourceMapURL)
    }

    private fun readNamesSection(source: WasmBinaryReader, startIndex: UInt, sectionPayloadSize: UInt, visitor: ModuleVisitor) {
        val nameSectionVisitor = visitor.visitNameSection()

        var lastNameKind: NameKind? = null

        while (source.position < startIndex + sectionPayloadSize) {
            val nameKind = source.readNameKind()

            if ((lastNameKind != null) && nameKind.nameKindId <= lastNameKind.nameKindId) {
                context.messages.add("warning: Name subsection is out of order")
            }

            val subsectionSize = source.readVarUInt32()
            if (!source.request(subsectionSize)) {
                throw ParserException("Name subsection greater then input")
            }

            val startSubSectionIndex = source.position

            when (nameKind) {
                MODULE -> {
                    val moduleName = source.readString()

                    nameSectionVisitor.visitModuleName(moduleName)
                }

                TYPE,
                TABLE,
                MEMORY,
                GLOBAL,
                ELEMENT,
                DATA,
                TAG,
                FUNCTION -> {
                    val numberOfFunctionNames = source.readVarUInt32()
                    var lastIndex: UInt? = null

                    val names = mutableListOf<IndexName>()
                    for (function in 0u until numberOfFunctionNames) {
                        val index = source.readIndex()

                        if ((lastIndex != null) && index < lastIndex) {
                            context.messages.add("warning: Index out of order in name section, $nameKind section at index %$index")
                        }

                        val functionName = source.readString()
                        names.add(IndexName(index, functionName))

                        lastIndex = index
                    }

                    when (nameKind) {
                        FUNCTION -> nameSectionVisitor.visitFunctionNames(names)
                        GLOBAL -> nameSectionVisitor.visitGlobalNames(names)
                        TAG -> nameSectionVisitor.visitTagNames(names)
                        TABLE -> nameSectionVisitor.visitTableNames(names)
                        MEMORY -> nameSectionVisitor.visitMemoryNames(names)
                        ELEMENT -> nameSectionVisitor.visitElementNames(names)
                        DATA -> nameSectionVisitor.visitDataNames(names)
                        TYPE -> nameSectionVisitor.visitTypeNames(names)
                        else -> throw ParserException("Unsupported name section: $nameKind")
                    }
                }

                FIELD,
                LABEL,
                LOCAL -> {
                    val numberOfLocalNames = source.readVarUInt32()
                    var lastFunctionIndex: UInt? = null

                    for (function in 0u until numberOfLocalNames) {
                        val functionIndex = source.readIndex()
                        if (functionIndex > context.numberOfTotalFunctions) {
                            context.messages.add("warning: Function index out of bounds in name section, local subsection at index %$functionIndex")
                        }

                        if ((lastFunctionIndex != null) && (functionIndex <= lastFunctionIndex)) {
                            context.messages.add("warning: Function index out of order in name section at index %$functionIndex")
                        }

                        val numberLocals = source.readVarUInt32()
                        var lastLocalIndex: UInt? = null

                        val names = mutableListOf<IndexName>()
                        for (local in 0u until numberLocals) {
                            val localNameIndex = source.readVarUInt32()

                            // TODO check if local index is valid

                            if ((lastLocalIndex != null) && (localNameIndex <= lastLocalIndex)) {
                                context.messages.add("warning: Local function index out of order in name section, local subsection at index %$localNameIndex")
                            }

                            val localName = source.readString()
                            if (localName.isEmpty()) {
                                context.messages.add("warning: Empty local name at index %$localNameIndex in function %$functionIndex")
                            }

                            names.add(IndexName(localNameIndex, localName))
                            lastLocalIndex = localNameIndex
                        }

                        when (nameKind) {
                            LOCAL -> nameSectionVisitor.visitLocalNames(functionIndex, names)
                            LABEL -> nameSectionVisitor.visitLabelNames(functionIndex, names)
                            FIELD -> nameSectionVisitor.visitFieldNames(functionIndex, names)
                            else -> throw ParserException("Unsupported name section: $nameKind")
                        }

                        lastFunctionIndex = functionIndex
                    }
                }

                TYPE -> {
                    val numberOfTypeNames = source.readVarUInt32()
                    var lastNameTypeIndex: UInt? = null

                    for (index in 0u until numberOfTypeNames) {
                        val nameTypeIndex = source.readVarUInt32()

                        // TODO check if name type is valid

                        if ((lastNameTypeIndex != null) && (nameTypeIndex <= lastNameTypeIndex)) {
                            context.messages.add("warning: Type index out of order in name section, type subsection at index %$nameTypeIndex")
                        }

                        val nameType = source.readString()
                        TODO()
                        // nameSectionVisitor.visitTypeName(nameTypeIndex, nameType)

                        lastNameTypeIndex = nameTypeIndex
                    }
                }

                else -> context.messages.add("warning: Unknown name subsection with id $nameKind")
            }

            if (subsectionSize != source.position - startSubSectionIndex) {
                throw ParserException("Wrong names subsection size with id $nameKind")
            }

            lastNameKind = nameKind
        }

        if (sectionPayloadSize != source.position - startIndex) {
            throw ParserException("Wrong names section size")
        }

        nameSectionVisitor.visitEnd()
    }

    private fun readLinkingSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val linkingSectionVisitor = visitor.visitLinkingSection()

        while (!source.exhausted()) {
            val linkingKind: LinkingKind = source.readLinkingKind()

            val subsectionSize = source.readVarUInt32()
            if (!source.request(subsectionSize)) {
                throw ParserException("Linking subsection greater then input")
            }

            val startIndex: UInt = source.position

            when (linkingKind) {
                LinkingKind.SYMBOL_TABLE -> {
                    val symbolCount = source.readVarUInt32()

                    for (symbolIndex in 0u until symbolCount) {
                        val symbolType = source.readLinkingSymbolType()
                        val flags = source.readUInt32()

                        linkingSectionVisitor.visitSymbol(symbolType, flags)

                        when (symbolType) {
                            LinkingSymbolType.FUNCTION, LinkingSymbolType.GLOBAL -> {
                                val index = source.readIndex()
                                var name: String? = null

                                if ((flags and WasmBinary.LINKING_SYMBOL_FLAG_UNDEFINED) == 0u) {
                                    name = source.readString()
                                }

                                if (symbolType == LinkingSymbolType.FUNCTION) {
                                    linkingSectionVisitor.visitFunctionSymbol(flags, name!!, index)
                                } else {
                                    linkingSectionVisitor.visitGlobalSymbol(flags, name!!, index)
                                }
                            }

                            LinkingSymbolType.DATA -> {
                                var segment = 0u
                                var offset = 0u
                                var size = 0u

                                val name = source.readString()

                                if ((flags and WasmBinary.LINKING_SYMBOL_FLAG_UNDEFINED) == 0u) {
                                    segment = source.readVarUInt32()
                                    offset = source.readVarUInt32()
                                    size = source.readVarUInt32()
                                }
                                linkingSectionVisitor.visitDataSymbol(flags, name, segment, offset, size)
                            }

                            LinkingSymbolType.SECTION -> {
                                val index = source.readIndex()

                                linkingSectionVisitor.visitSectionSymbol(flags, index)
                            }
                        }
                    }
                }

                LinkingKind.SEGMENT_INFO -> {
                    val segmentCount = source.readVarUInt32()

                    for (index in 0u until segmentCount) {
                        val name = source.readString()
                        val alignment = source.readVarUInt32()
                        val flags = source.readUInt32()

                        linkingSectionVisitor.visitSegment(name, alignment, flags)
                    }
                }

                else -> throw ParserException("Unsupported linking section: $linkingKind")
            }
            if (subsectionSize != source.position - startIndex) {
                throw ParserException("Invalid size of subsection id: $linkingKind")
            }
        }

        linkingSectionVisitor.visitEnd()
    }

    private fun readRelocationSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val sectionKind: SectionKind = source.readSectionKind()

        var sectionName: String? = null
        if (sectionKind == SectionKind.CUSTOM) {
            sectionName = source.readString()
        }

        val numberRelocations = source.readVarUInt32()

        val relocationVisitor = visitor.visitRelocationSection()
        relocationVisitor.visitSection(sectionKind, sectionName!!)

        for (relocationIndex in 0u until numberRelocations) {
            val relocationKind = source.readRelocationKind()

            when (relocationKind) {
                RelocationKind.FUNC_INDEX_LEB, RelocationKind.TABLE_INDEX_SLEB, RelocationKind.TABLE_INDEX_I32, RelocationKind.TYPE_INDEX_LEB, RelocationKind.GLOBAL_INDEX_LEB -> {
                    val offset = source.readIndex()
                    val index = source.readIndex()

                    relocationVisitor.visitRelocation(relocationKind, offset, index, null)
                }

                RelocationKind.MEMORY_ADDRESS_LEB, RelocationKind.MEMORY_ADDRESS_SLEB, RelocationKind.MEMORY_ADDRESS_I32, RelocationKind.FUNCTION_OFFSET_I32, RelocationKind.SECTION_OFFSET_I32 -> {
                    val offset = source.readIndex()
                    val index = source.readIndex()
                    val addend: Int = source.readVarInt32()

                    relocationVisitor.visitRelocation(relocationKind, offset, index, addend)
                }
            }
        }

        relocationVisitor.visitEnd()
    }
}
