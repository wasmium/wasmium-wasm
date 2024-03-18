package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.tree.LinkingKind
import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.tree.NameKind
import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.CustomSectionVisitor
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor
import org.wasmium.wasm.binary.visitors.NameSectionVisitor
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor

public class CustomSectionReader(
    private val context: WasmBinaryContext,
) {
    public fun readCustomSection(source: WasmSource, payloadSize: UInt, visitor: ModuleVisitor) {
        val startPosition = source.position

        val sectionName = source.readInlineString()
        val sectionPayloadSize = payloadSize - (source.position - startPosition)

        when {
            sectionName == WasmBinary.SECTION_NAME_NAME -> {
                context.nameSectionConsumed = true
                if (context.options.isDebugNamesEnabled) {
                    readNamesSection(source, source.position, sectionPayloadSize, visitor)
                } else {
                    source.skip(payloadSize)
                }
            }

            sectionName.startsWith(WasmBinary.SECTION_NAME_RELOCATION) -> {
                readRelocationSection(source, visitor)
            }

            sectionName == WasmBinary.SECTION_NAME_LINKING -> {
                readLinkingSection(source, visitor)
            }

            sectionName == WasmBinary.SECTION_NAME_EXCEPTION -> {
                if (context.options.features.isExceptionHandlingEnabled) {
                    readExceptionSection(source, visitor)
                } else {
                    source.skip(payloadSize)
                }
            }

            sectionName == WasmBinary.SECTION_NAME_SOURCE_MAPPING_URL -> {
                readSourceMapSection(source, visitor)
            }

            else -> {
                readUnknownSection(source, visitor, sectionName, startPosition, sectionPayloadSize)
            }
        }
    }

    private fun readUnknownSection(source: WasmSource, visitor: ModuleVisitor, sectionName: String, startPosition: UInt, sectionPayloadSize: UInt) {
        val payload = ByteArray(sectionPayloadSize.toInt())
        source.readTo(payload, 0u, sectionPayloadSize)

        val customSectionVisitor: CustomSectionVisitor = visitor.visitCustomSection()
        customSectionVisitor.visitSection(sectionName, payload)
        customSectionVisitor.visitEnd()
    }

    private fun readSourceMapSection(source: WasmSource, visitor: ModuleVisitor) {
        val sourceMapURL = source.readInlineString()
        // TODO
    }

    private fun readNamesSection(source: WasmSource, startIndex: UInt, sectionPayloadSize: UInt, visitor: ModuleVisitor) {
        val nameSectionVisitor: NameSectionVisitor = visitor.visitNameSection()

        var lastNameKind: NameKind? = null

        while (source.position < startIndex + sectionPayloadSize) {
            val nameKind = source.readNameKind()

            if ((lastNameKind != null) && nameKind.nameKindId <= lastNameKind.nameKindId) {
                context.messages.add("warning: Name subsection is out of order")
            }

            val subsectionSize = source.readVarUInt32()
            if (!source.require(subsectionSize)) {
                throw ParserException("Name subsection greater then input")
            }

            val startSubSectionIndex = source.position

            when (nameKind) {
                NameKind.MODULE -> {
                    val moduleName = source.readInlineString()

                    nameSectionVisitor.visitModuleName(moduleName)
                }

                NameKind.TABLE,
                NameKind.MEMORY,
                NameKind.GLOBAL,
                NameKind.ELEMENT,
                NameKind.DATA,
                NameKind.TAG,
                NameKind.FUNCTION -> {
                    val numberFunctionNames = source.readVarUInt32()
                    var lastFunctionIndex: UInt? = null

                    for (function in 0u until numberFunctionNames) {
                        val functionIndex = source.readVarUInt32()
                        if (functionIndex > context.numberTotalFunctions) {
                            context.messages.add("warning: Function index out of bounds in name section, function subsection at index %$functionIndex")
                        }

                        if ((lastFunctionIndex != null) && functionIndex < lastFunctionIndex) {
                            context.messages.add("warning: Function index out of order in name section, function subsection at index %$functionIndex")
                        }

                        val functionName = source.readInlineString()
                        when (nameKind) {
                            NameKind.FUNCTION -> nameSectionVisitor.visitFunctionName(functionIndex, functionName)
                            NameKind.GLOBAL -> nameSectionVisitor.visitGlobalName(functionIndex, functionName)
                            NameKind.TAG -> nameSectionVisitor.visitTagName(functionIndex, functionName)
                            NameKind.TABLE -> nameSectionVisitor.visitTableName(functionIndex, functionName)
                            NameKind.MEMORY -> nameSectionVisitor.visitMemoryName(functionIndex, functionName)
                            NameKind.ELEMENT -> nameSectionVisitor.visitElementName(functionIndex, functionName)
                            NameKind.DATA -> nameSectionVisitor.visitDataName(functionIndex, functionName)
                            else -> throw ParserException("Unsupported name section: $nameKind")
                        }

                        lastFunctionIndex = functionIndex
                    }
                }

                NameKind.LABEL,
                NameKind.LOCAL -> {
                    val numberFunctions = source.readVarUInt32()
                    var lastFunctionIndex: UInt? = null

                    for (function in 0u until numberFunctions) {
                        val functionIndex = source.readVarUInt32()
                        if (functionIndex > context.numberTotalFunctions) {
                            context.messages.add("warning: Function index out of bounds in name section, local subsection at index %$functionIndex")
                        }

                        if ((lastFunctionIndex != null) && (functionIndex <= lastFunctionIndex)) {
                            context.messages.add("warning: Function index out of order in name section at index %$functionIndex")
                        }

                        val numberLocals = source.readVarUInt32()
                        var lastLocalIndex: UInt? = null

                        for (local in 0u until numberLocals) {
                            val nameLocalIndex = source.readVarUInt32()

                            // TODO check if local index is valid

                            if ((lastLocalIndex != null) && (nameLocalIndex <= lastLocalIndex)) {
                                context.messages.add("warning: Local function index out of order in name section, local subsection at index %$nameLocalIndex")
                            }

                            val localName = source.readInlineString()
                            if (localName.isEmpty()) {
                                context.messages.add("warning: Empty local name at index %$nameLocalIndex in function %$functionIndex")
                            }

                            when (nameKind) {
                                NameKind.LOCAL -> nameSectionVisitor.visitLocalName(functionIndex, nameLocalIndex, localName)
                                NameKind.LABEL -> nameSectionVisitor.visitLabelName(functionIndex, nameLocalIndex, localName)
                                else -> throw ParserException("Unsupported name section: $nameKind")
                            }

                            lastLocalIndex = nameLocalIndex
                        }

                        lastFunctionIndex = functionIndex
                    }
                }

                NameKind.TYPE -> {
                    val numberNameTypes = source.readVarUInt32()
                    var lastNameTypeIndex: UInt? = null

                    for (index in 0u until numberNameTypes) {
                        val nameTypeIndex = source.readVarUInt32()

                        // TODO check if name type is valid

                        if ((lastNameTypeIndex != null) && (nameTypeIndex <= lastNameTypeIndex)) {
                            context.messages.add("warning: Type index out of order in name section, type subsection at index %$nameTypeIndex")
                        }

                        val nameType = source.readInlineString()
                        // TODO
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

    private fun readLinkingSection(source: WasmSource, visitor: ModuleVisitor) {
        val linkingSectionVisitor: LinkingSectionVisitor = visitor.visitLinkingSection()

        while (!source.exhausted()) {
            val linkingKind: LinkingKind = source.readLinkingKind()

            val subsectionSize = source.readVarUInt32()
            if (!source.require(subsectionSize)) {
                throw ParserException("Linking subsection greater then input")
            }

            val startIndex: UInt = source.position

            when (linkingKind) {
                LinkingKind.SYMBOL_TABLE -> {
                    val symbolCount = source.readVarUInt32()

                    for (symbolIndex in 0u until symbolCount) {
                        val symbolType = source.readLinkingSymbolType()
                        val flags = source.readUInt32()

                        linkingSectionVisitor.visitSymbol(symbolIndex, symbolType, flags)

                        when (symbolType) {
                            LinkingSymbolType.FUNCTION, LinkingSymbolType.GLOBAL -> {
                                val index = source.readIndex()
                                var name: String? = null

                                if ((flags and WasmBinary.LINKING_SYMBOL_FLAG_UNDEFINED) == 0u) {
                                    name = source.readInlineString()
                                }

                                if (symbolType == LinkingSymbolType.FUNCTION) {
                                    linkingSectionVisitor.visitFunctionSymbol(symbolIndex, flags, name!!, index)
                                } else {
                                    linkingSectionVisitor.visitGlobalSymbol(symbolIndex, flags, name!!, index)
                                }
                            }

                            LinkingSymbolType.DATA -> {
                                var segment = 0u
                                var offset = 0u
                                var size = 0u

                                val name = source.readInlineString()

                                if ((flags and WasmBinary.LINKING_SYMBOL_FLAG_UNDEFINED) == 0u) {
                                    segment = source.readVarUInt32()
                                    offset = source.readVarUInt32()
                                    size = source.readVarUInt32()
                                }
                                linkingSectionVisitor.visitDataSymbol(symbolIndex, flags, name, segment, offset, size)
                            }

                            LinkingSymbolType.SECTION -> {
                                val index = source.readIndex()

                                linkingSectionVisitor.visitSectionSymbol(symbolIndex, flags, index)
                            }
                        }
                    }
                }

                LinkingKind.SEGMENT_INFO -> {
                    val segmentCount = source.readVarUInt32()

                    for (index in 0u until segmentCount) {
                        val name = source.readInlineString()
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

    private fun readRelocationSection(source: WasmSource, visitor: ModuleVisitor) {
        val sectionKind: SectionKind = source.readSectionKind()

        var sectionName: String? = null
        if (sectionKind == SectionKind.CUSTOM) {
            sectionName = source.readInlineString()
        }

        val numberRelocations = source.readVarUInt32()

        val relocationVisitor: RelocationSectionVisitor = visitor.visitRelocationSection()
        relocationVisitor.visitSection(sectionKind, sectionName!!)

        for (relocationIndex in 0u until numberRelocations) {
            val relocationKind = source.readRelocationKind()

            when (relocationKind) {
                RelocationKind.FUNC_INDEX_LEB, RelocationKind.TABLE_INDEX_SLEB, RelocationKind.TABLE_INDEX_I32, RelocationKind.TYPE_INDEX_LEB, RelocationKind.GLOBAL_INDEX_LEB -> {
                    val offset = source.readIndex()
                    val index = source.readIndex()

                    relocationVisitor.visitRelocation(relocationKind, offset, index)
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

    private fun readExceptionSection(source: WasmSource, visitor: ModuleVisitor) {
        context.numberExceptions = source.readVarUInt32()

        if (context.numberExceptions > WasmBinary.MAX_EXCEPTIONS) {
            throw ParserException("Number of exceptions ${context.numberExceptions} exceed the maximum of ${WasmBinary.MAX_EXCEPTIONS}")
        }

        val exceptionVisitor = visitor.visitExceptionSection()
        for (index in 0u until context.numberExceptions) {
            val exceptionIndex = context.numberExceptionImports + index

            val exceptionType = readExceptionType(source)

            exceptionVisitor.visitExceptionType(exceptionIndex, exceptionType)
        }

        exceptionVisitor.visitEnd()
    }

    private fun readExceptionType(source: WasmSource): Array<WasmType> {
        val numberExceptionTypes = source.readVarUInt32()

        if (numberExceptionTypes > WasmBinary.MAX_EXCEPTION_TYPES) {
            throw ParserException("Number of exceptions types $numberExceptionTypes exceed the maximum of ${WasmBinary.MAX_EXCEPTIONS}")
        }

        val exceptionTypes = Array(numberExceptionTypes.toInt()) { WasmType.NONE }
        for (exceptionIndex in 0u until numberExceptionTypes) {
            val exceptionType = source.readType()

            if (!exceptionType.isValueType()) {
                throw ParserException("Invalid exception type: %#$exceptionType")
            }

            exceptionTypes[exceptionIndex.toInt()] = exceptionType
        }

        return exceptionTypes
    }
}
