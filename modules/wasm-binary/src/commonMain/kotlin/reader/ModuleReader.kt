package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_EXPRESSIONS
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_PASSIVE_OR_DECLARATIVE
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_TABLE_INDEX
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.ElementKind
import org.wasmium.wasm.binary.tree.ElementKind.FUNCTION_REF
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.IndexName
import org.wasmium.wasm.binary.tree.LinkingKind
import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.tree.NameKind
import org.wasmium.wasm.binary.tree.NameKind.FIELD
import org.wasmium.wasm.binary.tree.NameKind.LABEL
import org.wasmium.wasm.binary.tree.NameKind.LOCAL
import org.wasmium.wasm.binary.tree.NameKind.MODULE
import org.wasmium.wasm.binary.tree.Opcode.*
import org.wasmium.wasm.binary.tree.RelocationKind.EVENT_INDEX_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.FUNCTION_INDEX_I32
import org.wasmium.wasm.binary.tree.RelocationKind.FUNCTION_INDEX_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.FUNCTION_OFFSET_I32
import org.wasmium.wasm.binary.tree.RelocationKind.FUNCTION_OFFSET_I64
import org.wasmium.wasm.binary.tree.RelocationKind.GLOBAL_INDEX_I32
import org.wasmium.wasm.binary.tree.RelocationKind.GLOBAL_INDEX_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_I32
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_I64
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_LEB64
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_LOCREL_I32
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_SLEB
import org.wasmium.wasm.binary.tree.RelocationKind.MEMORY_ADDRESS_SLEB64
import org.wasmium.wasm.binary.tree.RelocationKind.SECTION_OFFSET_I32
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_I32
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_I64
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_REL_SLEB64
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_SLEB
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_INDEX_SLEB64
import org.wasmium.wasm.binary.tree.RelocationKind.TABLE_NUMBER_LEB
import org.wasmium.wasm.binary.tree.RelocationKind.TYPE_INDEX_LEB
import org.wasmium.wasm.binary.tree.SectionKind.CODE
import org.wasmium.wasm.binary.tree.SectionKind.CUSTOM
import org.wasmium.wasm.binary.tree.SectionKind.DATA
import org.wasmium.wasm.binary.tree.SectionKind.DATA_COUNT
import org.wasmium.wasm.binary.tree.SectionKind.ELEMENT
import org.wasmium.wasm.binary.tree.SectionKind.EXPORT
import org.wasmium.wasm.binary.tree.SectionKind.FUNCTION
import org.wasmium.wasm.binary.tree.SectionKind.GLOBAL
import org.wasmium.wasm.binary.tree.SectionKind.IMPORT
import org.wasmium.wasm.binary.tree.SectionKind.MEMORY
import org.wasmium.wasm.binary.tree.SectionKind.START
import org.wasmium.wasm.binary.tree.SectionKind.TABLE
import org.wasmium.wasm.binary.tree.SectionKind.TAG
import org.wasmium.wasm.binary.tree.SectionKind.TYPE
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.instructions.TryCatchArgument
import org.wasmium.wasm.binary.tree.instructions.TryCatchArgument.TryCatchKind
import org.wasmium.wasm.binary.tree.sections.RelocationType
import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor

@OptIn(ExperimentalUnsignedTypes::class)
public class ModuleReader(options: ReaderOptions) {
    private val context = ReaderContext(options)

    public fun readModule(source: WasmBinaryReader, visitor: ModuleVisitor): ReaderResult {
        // minimum allowed module size
        val minSize = 8u
        if (!source.request(minSize)) {
            throw ParserException("Expecting module size of at least: $minSize")
        }

        checkMagicNumber(source.readUInt32())

        val version = source.readUInt32()
        checkVersion(version)

        visitor.visitHeader(version)

        var numberOfSections = 0u

        while (!source.exhausted()) {
            if (numberOfSections > WasmBinary.MAX_SECTIONS) {
                throw ParserException("Sections size of $numberOfSections exceed the maximum of ${WasmBinary.MAX_SECTIONS}")
            }

            // current section
            val section = source.readSectionKind()
            if (section != CUSTOM) {
                // not consider CUSTOM section for ordering
                if (context.lastSection != null) {
                    if (section.ordinal < context.lastSection!!.ordinal) {
                        throw ParserException("Invalid section order of ${context.lastSection!!.name} followed by ${section.name}")
                    }
                }

                context.lastSection = section
            }

            val payloadSize = source.readVarUInt32()
            if (payloadSize > WasmBinary.MAX_SECTION_LENGTH) {
                throw ParserException("Section size of $payloadSize exceed the maximum of ${WasmBinary.MAX_SECTION_LENGTH}")
            }

            if (!source.request(payloadSize)) {
                throw ParserException("Section payload greater then input.")
            }

            val startPosition = source.position

            if (context.options.skipSections.contains(section)) {
                source.skip(payloadSize)
            } else {
                when (section) {
                    CUSTOM -> readCustomSection(source, payloadSize, visitor)
                    TYPE -> readTypeSection(source, visitor)
                    IMPORT -> readImportSection(source, visitor)
                    FUNCTION -> readFunctionSection(source, visitor)
                    TABLE -> readTableSection(source, visitor)
                    MEMORY -> readMemorySection(source, visitor)
                    TAG -> readTagSection(source, visitor)
                    GLOBAL -> readGlobalSection(source, visitor)
                    EXPORT -> readExportSection(source, visitor)
                    START -> readStartSection(source, visitor)
                    ELEMENT -> readElementSection(source, visitor)
                    CODE -> readCodeSection(source, visitor)
                    DATA -> readDataSection(source, visitor)
                    DATA_COUNT -> readDataCountSection(source, visitor)
                }
            }

            if (payloadSize != source.position - startPosition) {
                throw ParserException("Invalid size of section id $section, expected $payloadSize, actual ${source.position - startPosition}")
            }

            if (context.nameOfSectionConsumed && section != CUSTOM) {
                throw ParserException("${section.name} section can not occur after Name section")
            }

            numberOfSections++
        }

        visitor.visitEnd()

        if (source.position > WasmBinary.MAX_MODULE_SIZE) {
            throw ParserException("Module size of ${source.position} is too large, maximum allowed is ${WasmBinary.MAX_MODULE_SIZE}")
        }

        return ReaderResult.Success(context.messages)
    }

    private fun checkVersion(version: UInt) {
        if (version != WasmBinary.Meta.VERSION_1 && version != WasmBinary.Meta.VERSION_2) {
            throw ParserException("Unsupported version number: $version")
        }
    }

    private fun checkMagicNumber(magic: UInt) {
        if (magic != WasmBinary.Meta.MAGIC_NUMBER) {
            throw ParserException("Module does not start with: $magic")
        }
    }

    private fun readTypeSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfTypes = source.readVarUInt32()

        val typeVisitor = visitor.visitTypeSection()
        (0u until context.numberOfTypes).forEach { _ ->
            val functionType = source.readFunctionType()

            context.functionTypes.add(functionType)

            typeVisitor.visitType(functionType)
        }

        typeVisitor.visitEnd()
    }

    private fun readTagSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        if (context.options.features.isExceptionHandlingEnabled) {
            throw ParserException("Tag section is not enabled")
        }

        val numberOfTagTypes = source.readVarUInt32()

        val tagSectionVisitor = visitor.visitTagSection()
        (0u until numberOfTagTypes).forEach { _ ->
            val tagType = source.readTagType()

            tagSectionVisitor.visitTag(tagType)
        }
        tagSectionVisitor.visitEnd()
    }

    private fun readTableSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfTables = source.readVarUInt32()

        val tableVisitor = visitor.visitTableSection()
        (0u until context.numberOfTables).forEach { _ ->
            val tableType = source.readTableType()

            tableVisitor.visitTable(tableType)
        }

        tableVisitor.visitEnd()
    }

    private fun readStartSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val functionIndex = source.readIndex()

        val startSection = visitor.visitStartSection(functionIndex)
        startSection.visitEnd()
    }

    private fun readMemorySection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfMemories = source.readVarUInt32()

        if (context.numberOfMemories > 0u) {
            val memoryVisitor = visitor.visitMemorySection()
            (0u until context.numberOfMemories).forEach { _ ->
                val memoryType = source.readMemoryType()

                memoryVisitor.visitMemory(memoryType)
            }

            memoryVisitor.visitEnd()
        }
    }

    private fun readImportSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfImports = source.readVarUInt32()

        val importVisitor = visitor.visitImportSection()
        (0u until context.numberOfImports).forEach { _ ->
            val moduleName = source.readString()
            val fieldName = source.readString()

            when (source.readExternalKind()) {
                ExternalKind.FUNCTION -> {
                    val typeIndex = source.readIndex()

                    val functionType = context.functionTypes.getOrElse(typeIndex.toInt()) {
                        throw ParserException("Invalid type index at $typeIndex")
                    }

                    importVisitor.visitFunction(moduleName, fieldName, functionType)

                    context.numberOfFunctionImports++
                }

                ExternalKind.TABLE -> {
                    val tableType = source.readTableType()

                    importVisitor.visitTable(moduleName, fieldName, tableType)

                    context.numberOfTableImports++
                }

                ExternalKind.MEMORY -> {
                    val memoryType = source.readMemoryType()
                    importVisitor.visitMemory(moduleName, fieldName, memoryType)

                    context.numberOfMemoryImports++
                }

                ExternalKind.GLOBAL -> {
                    val globalType = source.readGlobalType()

                    importVisitor.visitGlobal(moduleName, fieldName, globalType)

                    context.numberOfGlobalImports++
                }

                ExternalKind.TAG -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid import exception kind: exceptions not enabled.")
                    }

                    val tagType = source.readTagType()
                    importVisitor.visitTag(moduleName, fieldName, tagType)

                    context.numberOfTagImports++
                }
            }
        }

        importVisitor.visitEnd()
    }

    private fun readGlobalVariable(source: WasmBinaryReader, globalVisitor: GlobalSectionVisitor) {
        val globalType = source.readGlobalType()
        val expressionVisitor = globalVisitor.visitGlobalVariable(globalType)

        readExpression(source, expressionVisitor)
        expressionVisitor.visitEnd()
    }

    private fun readGlobalSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfGlobals = source.readVarUInt32()

        val globalVisitor = visitor.visitGlobalSection()
        (0u until context.numberOfGlobals).forEach { _ ->
            readGlobalVariable(source, globalVisitor)
        }

        globalVisitor.visitEnd()
    }

    private fun readFunctionSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfFunctions = source.readVarUInt32()

        val functionVisitor = visitor.visitFunctionSection()
        (0u until context.numberOfFunctions).forEach { _ ->
            val typeIndex = source.readIndex()

            val functionType = context.functionTypes.getOrElse(typeIndex.toInt()) {
                throw IllegalStateException("Function type not found")
            }

            functionVisitor.visitFunction(functionType)
        }

        functionVisitor.visitEnd()
    }

    private fun readExportSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfExports = source.readVarUInt32()

        val exportVisitor = visitor.visitExportSection()
        (0u until context.numberOfExports).forEach { _ ->
            val name = source.readString()

            val externalKind = source.readExternalKind()
            when (externalKind) {
                ExternalKind.FUNCTION -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid export exception kind: exceptions not enabled.")
                    }
                }

                ExternalKind.TAG -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid import exception kind: exceptions not enabled.")
                    }
                }

                else -> {
                    // empty
                }
            }

            val itemIndex = source.readIndex()
            exportVisitor.visitExport(name, externalKind, itemIndex)
        }

        exportVisitor.visitEnd()
    }

    private fun readElementSegment(source: WasmBinaryReader, elementVisitor: ElementSectionVisitor) {
        val startIndex = source.position

        val elementSegmentVisitor = elementVisitor.visitElementSegment()

        val elementType = source.readVarUInt32()
        if (elementType >= 0x08u) {
            throw ParserException("Invalid element type $elementType")
        }

        if ((elementType and ELEMENT_PASSIVE_OR_DECLARATIVE.toUInt()) != 0u) {
            elementSegmentVisitor.visitNonActiveMode((elementType and ELEMENT_TABLE_INDEX.toUInt()) != 0u)
        } else {
            val tableIndex = if ((elementType and ELEMENT_TABLE_INDEX.toUInt()) != 0u) source.readIndex() else 0u

            val expressionVisitor = elementSegmentVisitor.visitActiveMode(tableIndex)
            readExpression(source, expressionVisitor)
            expressionVisitor.visitEnd()
        }

        val implicitFuncRef = (elementType and 0b011u) == 0u
        if ((elementType and ELEMENT_EXPRESSIONS.toUInt()) != 0u) {
            if (!implicitFuncRef) {
                val type = source.readWasmRefType()
                elementSegmentVisitor.visitType(type)
            }

            val initLength = source.readVarUInt32()
            (0u until initLength).forEach { _ ->
                val expressionVisitor = elementSegmentVisitor.visitExpression()
                readExpression(source, expressionVisitor)
                expressionVisitor.visitEnd()
            }
        } else {
            if (!implicitFuncRef) {
                val elementKindId = source.readVarUInt1()
                val elementKind = ElementKind.fromElementKind(elementKindId) ?: throw ParserException("Unrecognised element kind $elementKindId")

                val type = when (elementKind) {
                    FUNCTION_REF -> WasmType.FUNC_REF
                }
                elementSegmentVisitor.visitType(type)
            }

            val numberOfFunctionIndexes = source.readVarUInt32()
            val elementIndices = mutableListOf<UInt>()
            (0u until numberOfFunctionIndexes).forEach { _ ->
                elementIndices.add(source.readIndex())
            }
            elementSegmentVisitor.visitElementIndices(elementIndices)

            if (numberOfFunctionIndexes + (source.position - startIndex) > WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH) {
                throw ParserException("Element segment size of ${numberOfFunctionIndexes + (source.position - startIndex)} exceed the maximum of ${WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH}")
            }
        }

        elementSegmentVisitor.visitEnd()
    }

    private fun readElementSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val numberOfElementSegments = source.readVarUInt32()

        if (numberOfElementSegments != 0u && context.numberOfTotalTables <= 0u) {
            throw ParserException("Element section without table section.")
        }

        val elementVisitor = visitor.visitElementSection()
        (0u until numberOfElementSegments).forEach { _ ->
            readElementSegment(source, elementVisitor)
        }
        elementVisitor.visitEnd()
    }

    private fun readDataSegment(source: WasmBinaryReader, dataVisitor: DataSectionVisitor) {
        val startIndex = source.position

        val dataSegmentVisitor = dataVisitor.visitDataSegment()

        val dataType = source.readVarUInt32()
        if (dataType and WasmBinary.DATA_PASSIVE.toUInt() == 0u) {
            val memoryIndex = if ((dataType and WasmBinary.DATA_EXPLICIT.toUInt()) == 0u) 0u else source.readVarUInt32()

            val expressionVisitor = dataSegmentVisitor.visitActive(memoryIndex)
            readExpression(source, expressionVisitor)
            expressionVisitor.visitEnd()
        }

        val dataSize = source.readVarUInt32()
        if (dataSize + (source.position - startIndex) > WasmBinary.MAX_DATA_SEGMENT_LENGTH) {
            throw ParserException("Data segment size of $dataSize${source.position - startIndex} exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENT_LENGTH}")
        }
        val data = ByteArray(dataSize.toInt())
        source.readTo(data, 0u, dataSize)
        dataSegmentVisitor.visitData(data)

        dataSegmentVisitor.visitEnd()
    }

    private fun readDataSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val dataSegmentCount = source.readVarUInt32()

        val dataVisitor = visitor.visitDataSection()
        (0u until dataSegmentCount).forEach { _ ->
            readDataSegment(source, dataVisitor)
        }

        dataVisitor.visitEnd()
    }

    private fun readDataCountSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val dataCount = source.readVarUInt32()

        val dataCountSectionVisitor = visitor.visitDataCountSection(dataCount)
        dataCountSectionVisitor.visitEnd()
    }

    private fun readCustomSection(source: WasmBinaryReader, payloadSize: UInt, visitor: ModuleVisitor) {
        val startPosition = source.position

        val sectionName = source.readString()
        val sectionPayloadSize = payloadSize - (source.position - startPosition)

        when {
            sectionName == SectionName.NAME.sectionName -> {
                context.nameOfSectionConsumed = true
                if (context.options.debugNames) {
                    readNamesSection(source, source.position, sectionPayloadSize, visitor)
                } else {
                    readUnknownSection(source, visitor, sectionName, sectionPayloadSize)
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

            sectionName == SectionName.EXTERNAL_DEBUG_INFO.sectionName -> {
                readExternalDebugSection(source, visitor)
            }

            else -> {
                readUnknownSection(source, visitor, sectionName, sectionPayloadSize)
            }
        }
    }

    private fun readUnknownSection(source: WasmBinaryReader, visitor: ModuleVisitor, customSectionName: String, sectionPayloadSize: UInt) {
        val payload = ByteArray(sectionPayloadSize.toInt())
        source.readTo(payload, 0u, sectionPayloadSize)

        val unknownSectionVisitor = visitor.visitUnknownSection(customSectionName, payload)
        unknownSectionVisitor.visitEnd()
    }

    private fun readSourceMapSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val sourceMapUrl = source.readString()

        visitor.visitSourceMapSection(sourceMapUrl)
    }

    private fun readExternalDebugSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val debugInfoUrl = source.readString()

        visitor.visitExternalDebugSection(debugInfoUrl)
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

                NameKind.TYPE,
                NameKind.TABLE,
                NameKind.MEMORY,
                NameKind.GLOBAL,
                NameKind.ELEMENT,
                NameKind.DATA,
                NameKind.TAG,
                NameKind.FUNCTION -> {
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
                        NameKind.FUNCTION -> nameSectionVisitor.visitFunctionNames(names)
                        NameKind.GLOBAL -> nameSectionVisitor.visitGlobalNames(names)
                        NameKind.TAG -> nameSectionVisitor.visitTagNames(names)
                        NameKind.TABLE -> nameSectionVisitor.visitTableNames(names)
                        NameKind.MEMORY -> nameSectionVisitor.visitMemoryNames(names)
                        NameKind.ELEMENT -> nameSectionVisitor.visitElementNames(names)
                        NameKind.DATA -> nameSectionVisitor.visitDataNames(names)
                        NameKind.TYPE -> nameSectionVisitor.visitTypeNames(names)
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

            val startIndex = source.position

            when (linkingKind) {
                LinkingKind.SYMBOL_TABLE -> {
                    val symbolCount = source.readVarUInt32()

                    (0u until symbolCount).forEach { _ ->
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

                    (0u until segmentCount).forEach { _ ->
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
        if (!context.options.features.isLinkingEnabled) {
            throw ParserException("Invalid relocation section: linking not enabled.")
        }

        val sectionKind = source.readSectionKind()

        if (context.lastSection != null && context.lastSection!! != sectionKind) {
            throw ParserException("Custom relocation section must be sequenced after the section $sectionKind and is after ${context.lastSection}")
        }

        var sectionName: String? = null
        if (sectionKind == CUSTOM) {
            sectionName = source.readString()
        }

        val numberOfRelocations = source.readVarUInt32()

        val relocationVisitor = visitor.visitRelocationSection()
        val relocationTypes = mutableListOf<RelocationType>()
        (0u until numberOfRelocations).forEach { _ ->
            when (val relocationKind = source.readRelocationKind()) {
                FUNCTION_INDEX_LEB,
                TABLE_INDEX_SLEB,
                TABLE_INDEX_I32,
                TYPE_INDEX_LEB,
                GLOBAL_INDEX_LEB,
                EVENT_INDEX_LEB,
                GLOBAL_INDEX_I32,
                TABLE_INDEX_SLEB64,
                TABLE_INDEX_I64,
                TABLE_NUMBER_LEB,
                TABLE_INDEX_REL_SLEB64,
                FUNCTION_INDEX_I32 -> {
                    val offset = source.readIndex()
                    val index = source.readIndex()

                    relocationTypes.add(RelocationType(relocationKind, offset, index, addend = null))
                }

                MEMORY_ADDRESS_LEB,
                MEMORY_ADDRESS_LEB64,
                MEMORY_ADDRESS_SLEB,
                MEMORY_ADDRESS_SLEB64,
                MEMORY_ADDRESS_I32,
                MEMORY_ADDRESS_I64,
                MEMORY_ADDRESS_LOCREL_I32,
                FUNCTION_OFFSET_I32,
                FUNCTION_OFFSET_I64,
                SECTION_OFFSET_I32 -> {
                    val offset = source.readIndex()
                    val index = source.readIndex()
                    val addend = source.readVarInt32()

                    relocationTypes.add(RelocationType(relocationKind, offset, index, addend))
                }
            }
        }

        relocationVisitor.visitRelocation(sectionKind, sectionName, relocationTypes)

        relocationVisitor.visitEnd()
    }

    private fun readCodeSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val numberOfCodes = source.readVarUInt32()

        val codeVisitor = visitor.visitCodeSection()

        (0u until numberOfCodes).forEach { _ ->
            val bodySize = source.readVarUInt32()
            if (bodySize == 0u) {
                throw ParserException("Empty function size")
            }

            if (bodySize > WasmBinary.MAX_FUNCTION_SIZE) {
                throw ParserException("Function body size $bodySize exceed the maximum of ${WasmBinary.MAX_FUNCTION_SIZE}")
            }

            val startAvailable = source.position

            val locals = mutableListOf<LocalVariable>()
            val numberOfLocals = source.readVarUInt32()

            (0u until numberOfLocals).forEach { _ ->
                val numberOfLocalTypes = source.readVarUInt32()

                val localType = source.readType()
                if (!localType.isValueType()) {
                    throw ParserException("Invalid local type: %#$localType")
                }

                locals.add(LocalVariable(numberOfLocalTypes, localType))
            }

            val expressionVisitor = codeVisitor.visitCode(locals)
            readExpression(source, expressionVisitor)
            expressionVisitor.visitEnd()

            if (bodySize != source.position - startAvailable) {
                throw ParserException("Binary offset at function exit not at expected location.")
            }
        }

        codeVisitor.visitEnd()
    }

    private fun readExpression(source: WasmBinaryReader, expressionVisitor: ExpressionVisitor) {
        var blockDepth = 1u

        while (true) {
            when (val opcode = source.readOpcode()) {
                UNREACHABLE -> {
                    expressionVisitor.visitUnreachableInstruction()
                }

                NOP -> {
                    expressionVisitor.visitNopInstruction()
                }

                END -> {
                    expressionVisitor.visitEndInstruction()

                    blockDepth--
                    if (blockDepth <= 0u) {
                        return
                    }
                }

                BLOCK -> {
                    val blockType = source.readBlockType()

                    expressionVisitor.visitBlockInstruction(blockType)

                    ++blockDepth
                }

                LOOP -> {
                    val blockType = source.readBlockType()

                    expressionVisitor.visitLoopInstruction(blockType)

                    ++blockDepth
                }

                IF -> {
                    val blockType = source.readBlockType()

                    expressionVisitor.visitIfInstruction(blockType)

                    ++blockDepth
                }

                ELSE -> {
                    expressionVisitor.visitElseInstruction()
                }

                TRY -> {
                    val blockType = source.readBlockType()

                    expressionVisitor.visitTryInstruction(blockType)
                }

                CATCH -> {
                    expressionVisitor.visitCatchInstruction()
                }

                THROW -> {
                    val exceptionIndex = source.readIndex()

                    expressionVisitor.visitThrowInstruction(exceptionIndex)
                }

                RETHROW -> {
                    expressionVisitor.visitRethrowInstruction()
                }

                THROW_REF -> {
                    expressionVisitor.visitThrowRefInstruction()
                }

                BR -> {
                    val depth = source.readIndex()

                    expressionVisitor.visitBrInstruction(depth)
                }

                BR_IF -> {
                    val depth = source.readIndex()

                    expressionVisitor.visitBrIfInstruction(depth)
                }

                BR_TABLE -> {
                    val numberTargets = source.readVarUInt32()
                    val targets = mutableListOf<UInt>()

                    (0u until numberTargets).forEach { _ ->
                        val depth = source.readIndex()

                        targets.add(depth)
                    }

                    val defaultTarget = source.readIndex()

                    expressionVisitor.visitBrTableInstruction(targets, defaultTarget)
                }

                RETURN -> {
                    expressionVisitor.visitReturnInstruction()
                }

                CALL -> {
                    val functionIndex = source.readIndex()

                    expressionVisitor.visitCallInstruction(functionIndex)
                }

                CALL_INDIRECT -> {
                    val typeIndex = source.readIndex()
                    val reserved = source.readVarUInt32()

                    expressionVisitor.visitCallIndirectInstruction(typeIndex, reserved)
                }

                DROP -> {
                    expressionVisitor.visitDropInstruction()
                }

                SELECT -> {
                    expressionVisitor.visitSelectInstruction()
                }

                GET_GLOBAL -> {
                    val globalIndex = source.readIndex()

                    expressionVisitor.visitGetGlobalInstruction(globalIndex)
                }

                SET_LOCAL -> {
                    val localIndex = source.readIndex()

                    expressionVisitor.visitSetLocalInstruction(localIndex)
                }

                TEE_LOCAL -> {
                    val localIndex = source.readIndex()

                    expressionVisitor.visitTeeLocalInstruction(localIndex)
                }

                GET_LOCAL -> {
                    val localIndex = source.readIndex()

                    expressionVisitor.visitGetLocalInstruction(localIndex)
                }

                SET_GLOBAL -> {
                    val globalIndex = source.readIndex()

                    expressionVisitor.visitSetGlobalInstruction(globalIndex)
                }

                I32_LOAD,
                I64_LOAD,
                F32_LOAD,
                F64_LOAD,
                I32_LOAD8_S,
                I32_LOAD8_U,
                I32_LOAD16_S,
                I32_LOAD16_U,
                I64_LOAD8_S,
                I64_LOAD8_U,
                I64_LOAD16_S,
                I64_LOAD16_U,
                I64_LOAD32_S,
                I64_LOAD32_U -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitLoadInstruction(opcode, alignment, offset)
                }

                I32_STORE8,
                I32_STORE16,
                I64_STORE8,
                I64_STORE16,
                I64_STORE32,
                I32_STORE,
                I64_STORE,
                F32_STORE,
                F64_STORE -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitStoreInstruction(opcode, alignment, offset)
                }

                MEMORY_SIZE -> {
                    val reserved = source.readVarUInt1()

                    expressionVisitor.visitMemorySizeInstruction(reserved)
                }

                MEMORY_GROW -> {
                    val reserved = source.readVarUInt1()

                    expressionVisitor.visitMemoryGrowInstruction(reserved)
                }

                I32_CONST -> {
                    val value = source.readVarInt32()

                    expressionVisitor.visitConstInt32Instruction(value)
                }

                I64_CONST -> {
                    val value = source.readVarInt64()

                    expressionVisitor.visitConstInt64Instruction(value)
                }

                F32_CONST -> {
                    val value = source.readFloat32()

                    expressionVisitor.visitConstFloat32Instruction(value)
                }

                F64_CONST -> {
                    val value = source.readFloat64()

                    expressionVisitor.visitConstFloat64Instruction(value)
                }

                I32_EQZ,
                I64_EQZ -> {
                    expressionVisitor.visitEqualZeroInstruction(opcode)
                }

                F64_LT,
                I32_LT_S,
                I32_LT_U,
                F32_LT,
                I64_LT_S,
                I64_LT_U -> {
                    expressionVisitor.visitLessThanInstruction(opcode)
                }

                I32_EQ,
                I64_EQ,
                F32_EQ,
                F64_EQ -> {
                    expressionVisitor.visitEqualInstruction(opcode)
                }

                I32_NE,
                I64_NE,
                F32_NE,
                F64_NE -> {
                    expressionVisitor.visitNotEqualInstruction(opcode)
                }

                I32_LE_S,
                I32_LE_U,
                I64_LE_S,
                I64_LE_U,
                F32_LE,
                F64_LE -> {
                    expressionVisitor.visitLessEqualInstruction(opcode)
                }

                I32_GT_S,
                I32_GT_U,
                I64_GT_S,
                I64_GT_U,
                F32_GT,
                F64_GT -> {
                    expressionVisitor.visitGreaterThanInstruction(opcode)
                }

                I32_GE_S,
                I32_GE_U,
                I64_GE_S,
                I64_GE_U,
                F32_GE,
                F64_GE -> {
                    expressionVisitor.visitGreaterEqualInstruction(opcode)
                }

                I32_CLZ,
                I64_CLZ -> {
                    expressionVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                I32_CTZ,
                I64_CTZ -> {
                    expressionVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                I32_POPCNT,
                I64_POPCNT -> {
                    expressionVisitor.visitPopulationCountInstruction(opcode)
                }

                I32_SUB,
                I64_SUB,
                F32_SUB,
                F64_SUB -> {
                    expressionVisitor.visitSubtractInstruction(opcode)
                }

                F32_DIV,
                I32_DIV_S,
                I32_DIV_U,
                F64_DIV,
                I64_DIV_S,
                I64_DIV_U -> {
                    expressionVisitor.visitDivideInstruction(opcode)
                }

                I32_REM_S,
                I32_REM_U,
                I64_REM_S,
                I64_REM_U -> {
                    expressionVisitor.visitRemainderInstruction(opcode)
                }

                I32_AND,
                I64_AND -> {
                    expressionVisitor.visitAndInstruction(opcode)
                }

                I32_OR,
                I64_OR -> {
                    expressionVisitor.visitOrInstruction(opcode)
                }

                I32_XOR,
                I64_XOR -> {
                    expressionVisitor.visitXorInstruction(opcode)
                }

                I32_SHR_S,
                I32_SHR_U,
                I64_SHR_U,
                I64_SHR_S -> {
                    expressionVisitor.visitShiftRightInstruction(opcode)
                }

                I32_SHL,
                I64_SHL -> {
                    expressionVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_ROTL,
                I64_ROTL -> {
                    expressionVisitor.visitRotateLeftInstruction(opcode)
                }

                I32_ROTR,
                I64_ROTR -> {
                    expressionVisitor.visitRotateRightInstruction(opcode)
                }

                F32_ABS,
                F64_ABS -> {
                    expressionVisitor.visitAbsoluteInstruction(opcode)
                }

                F32_NEG,
                F64_NEG -> {
                    expressionVisitor.visitNegativeInstruction(opcode)
                }

                F32_CEIL,
                F64_CEIL -> {
                    expressionVisitor.visitCeilingInstruction(opcode)
                }

                F32_FLOOR,
                F64_FLOOR -> {
                    expressionVisitor.visitFloorInstruction(opcode)
                }

                F32_NEAREST,
                F64_NEAREST -> {
                    expressionVisitor.visitNearestInstruction(opcode)
                }

                F32_SQRT,
                F64_SQRT -> {
                    expressionVisitor.visitSqrtInstruction(opcode)
                }

                I32_ADD,
                I64_ADD,
                F32_ADD,
                F64_ADD -> {
                    expressionVisitor.visitAddInstruction(opcode)
                }

                I32_MUL,
                I64_MUL,
                F32_MUL,
                F64_MUL -> {
                    expressionVisitor.visitMultiplyInstruction(opcode)
                }

                F32_MIN,
                F64_MIN -> {
                    expressionVisitor.visitMinInstruction(opcode)
                }

                F32_MAX,
                F64_MAX -> {
                    expressionVisitor.visitMaxInstruction(opcode)
                }

                F32_COPYSIGN,
                F64_COPYSIGN -> {
                    expressionVisitor.visitCopySignInstruction(opcode)
                }

                I32_WRAP_I64 -> {
                    expressionVisitor.visitWrapInstruction(opcode)
                }

                F32_TRUNC,
                F64_TRUNC,
                I32_TRUNC_S_F32,
                I32_TRUNC_U_F32,
                I32_TRUNC_S_F64,
                I32_TRUNC_U_F64,
                I64_TRUNC_S_F32,
                I64_TRUNC_U_F32,
                I64_TRUNC_S_F64,
                I64_TRUNC_U_F64,
                I32_TRUNC_S_SAT_F32,
                I32_TRUNC_U_SAT_F32,
                I32_TRUNC_S_SAT_F64,
                I32_TRUNC_U_SAT_F64,
                I64_TRUNC_S_SAT_F32,
                I64_TRUNC_U_SAT_F32,
                I64_TRUNC_S_SAT_F64,
                I64_TRUNC_U_SAT_F64 -> {
                    expressionVisitor.visitTruncateInstruction(opcode)
                }

                F32_CONVERT_S_I32,
                F32_CONVERT_U_I32,
                F32_CONVERT_S_I64,
                F32_CONVERT_U_I64,
                F64_CONVERT_S_I32,
                F64_CONVERT_U_I32,
                F64_CONVERT_S_I64,
                F64_CONVERT_U_I64 -> {
                    expressionVisitor.visitConvertInstruction(opcode)
                }

                F32_DEMOTE_F64 -> {
                    expressionVisitor.visitDemoteInstruction(opcode)
                }

                F64_PROMOTE_F32 -> {
                    expressionVisitor.visitPromoteInstruction(opcode)
                }

                I32_REINTERPRET_F32,
                I64_REINTERPRET_F64,
                F32_REINTERPRET_I32,
                F64_REINTERPRET_I64 -> {
                    expressionVisitor.visitReinterpretInstruction(opcode)
                }

                I32_EXTEND8_S,
                I32_EXTEND16_S,
                I64_EXTEND8_S,
                I64_EXTEND16_S,
                I64_EXTEND32_S,
                I64_EXTEND_S_I32,
                I64_EXTEND_U_I32 -> {
                    expressionVisitor.visitExtendInstruction(opcode)
                }

                MEMORY_ATOMIC_NOTIFY -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicWakeInstruction(opcode, alignment, offset)
                }

                MEMORY_ATOMIC_WAIT32,
                MEMORY_ATOMIC_WAIT64 -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicWaitInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_LOAD,
                I64_ATOMIC_LOAD,
                I32_ATOMIC_LOAD8_U,
                I32_ATOMIC_LOAD16_U,
                I64_ATOMIC_LOAD8_U,
                I64_ATOMIC_LOAD16_U,
                I64_ATOMIC_LOAD32_U -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicLoadInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_STORE,
                I64_ATOMIC_STORE,
                I32_ATOMIC_STORE8,
                I32_ATOMIC_STORE16,
                I64_ATOMIC_STORE8,
                I64_ATOMIC_STORE16,
                I64_ATOMIC_STORE32 -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicStoreInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_ADD,
                I64_ATOMIC_RMW_ADD,
                I32_ATOMIC_RMW8_U_ADD,
                I32_ATOMIC_RMW16_U_ADD,
                I64_ATOMIC_RMW8_U_ADD,
                I64_ATOMIC_RMW16_U_ADD,
                I64_ATOMIC_RMW32_U_ADD -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwAddInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_SUB,
                I64_ATOMIC_RMW_SUB,
                I32_ATOMIC_RMW8_U_SUB,
                I32_ATOMIC_RMW16_U_SUB,
                I64_ATOMIC_RMW8_U_SUB,
                I64_ATOMIC_RMW16_U_SUB,
                I64_ATOMIC_RMW32_U_SUB -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_AND,
                I64_ATOMIC_RMW_AND,
                I32_ATOMIC_RMW8_U_AND,
                I32_ATOMIC_RMW16_U_AND,
                I64_ATOMIC_RMW8_U_AND,
                I64_ATOMIC_RMW16_U_AND,
                I64_ATOMIC_RMW32_U_AND -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwAndInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_OR,
                I64_ATOMIC_RMW_OR,
                I32_ATOMIC_RMW8_U_OR,
                I32_ATOMIC_RMW16_U_OR,
                I64_ATOMIC_RMW8_U_OR,
                I64_ATOMIC_RMW16_U_OR,
                I64_ATOMIC_RMW32_U_OR -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwOrInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_XOR,
                I64_ATOMIC_RMW_XOR,
                I32_ATOMIC_RMW8_U_XOR,
                I32_ATOMIC_RMW16_U_XOR,
                I64_ATOMIC_RMW8_U_XOR,
                I64_ATOMIC_RMW16_U_XOR,
                I64_ATOMIC_RMW32_U_XOR -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwXorInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_XCHG,
                I64_ATOMIC_RMW_XCHG,
                I32_ATOMIC_RMW8_U_XCHG,
                I32_ATOMIC_RMW16_U_XCHG,
                I64_ATOMIC_RMW8_U_XCHG,
                I64_ATOMIC_RMW16_U_XCHG,
                I64_ATOMIC_RMW32_U_XCHG -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_CMPXCHG,
                I64_ATOMIC_RMW_CMPXCHG,
                I32_ATOMIC_RMW8_U_CMPXCHG,
                I32_ATOMIC_RMW16_U_CMPXCHG,
                I64_ATOMIC_RMW8_U_CMPXCHG,
                I64_ATOMIC_RMW16_U_CMPXCHG,
                I64_ATOMIC_RMW32_U_CMPXCHG -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
                }

                V128_CONST -> {
                    val value: V128Value = source.readV128()

                    expressionVisitor.visitSimdConstInstruction(value)
                }

                V128_LOAD -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitSimdLoadInstruction(opcode, alignment, offset)
                }

                V128_STORE -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitSimdStoreInstruction(opcode, alignment, offset)
                }

                I8X16_SPLAT,
                I16X8_SPLAT,
                I32X4_SPLAT,
                I64X2_SPLAT,
                F32X4_SPLAT,
                F64X2_SPLAT -> {
                    val value = source.readVarUInt32()

                    expressionVisitor.visitSimdSplatInstruction(opcode, value)
                }

                I8X16_EXTRACT_LANE_S,
                I8X16_EXTRACT_LANE_U,
                I16X8_EXTRACT_LANE_S,
                I16X8_EXTRACT_LANE_U,
                I32X4_EXTRACT_LANE,
                I64X2_EXTRACT_LANE,
                F32X4_EXTRACT_LANE,
                F64X2_EXTRACT_LANE -> {
                    val index = source.readIndex()

                    expressionVisitor.visitSimdExtractLaneInstruction(opcode, index)
                }

                I8X16_REPLACE_LANE,
                I16X8_REPLACE_LANE,
                I32X4_REPLACE_LANE,
                I64X2_REPLACE_LANE,
                F32X4_REPLACE_LANE,
                F64X2_REPLACE_LANE -> {
                    val index = source.readIndex()

                    expressionVisitor.visitSimdReplaceLaneInstruction(opcode, index)
                }

                V8X16_SHUFFLE -> {
                    val lanesIndex = UIntArray(16) { 0u }

                    for (index in 0u until 16u) {
                        lanesIndex[index.toInt()] = source.readVarUInt32()
                    }

                    expressionVisitor.visitSimdShuffleInstruction(opcode, V128Value(lanesIndex))
                }

                I8X16_ADD_SATURATE_S,
                I8X16_ADD_SATURATE_U,
                I16X8_ADD_SATURATE_S,
                I16X8_ADD_SATURATE_U -> {
                    expressionVisitor.visitSimdAddSaturateInstruction(opcode)
                }

                I8X16_SUB_SATURATE_S,
                I8X16_SUB_SATURATE_U,
                I16X8_SUB_SATURATE_S,
                I16X8_SUB_SATURATE_U -> {
                    expressionVisitor.visitSimdSubtractSaturateInstruction(opcode)
                }

                I8X16_SHL,
                I16X8_SHL,
                I32X4_SHL,
                I64X2_SHL,
                I8X16_SHL_S,
                I8X16_SHL_U,
                I16X8_SHL_S,
                I16X8_SHL_U,
                I32X4_SHL_S,
                I32X4_SHL_U,
                I64X2_SHL_S,
                I64X2_SHL_U -> {
                    expressionVisitor.visitSimdShiftLeftInstruction(opcode)
                }

                V128_AND -> {
                    expressionVisitor.visitSimdAndInstruction(opcode)
                }

                V128_OR -> {
                    expressionVisitor.visitSimdOrInstruction(opcode)
                }

                V128_XOR -> {
                    expressionVisitor.visitSimdXorInstruction(opcode)
                }

                V128_NOT -> {
                    expressionVisitor.visitSimdNotInstruction(opcode)
                }

                V128_BITSELECT -> {
                    expressionVisitor.visitSimdBitSelectInstruction(opcode)
                }

                I8X16_ANY_TRUE,
                I16X8_ANY_TRUE,
                I32X4_ANY_TRUE,
                I64X2_ANY_TRUE,
                I8X16_ALL_TRUE,
                I16X8_ALL_TRUE,
                I32X4_ALL_TRUE,
                I64X2_ALL_TRUE -> {
                    expressionVisitor.visitSimdAllTrueInstruction(opcode)
                }

                I8X16_EQ,
                I16X8_EQ,
                I32X4_EQ,
                F32X4_EQ,
                F64X2_EQ -> {
                    expressionVisitor.visitSimdEqualInstruction(opcode)
                }

                I8X16_NE,
                I16X8_NE,
                I32X4_NE,
                F32X4_NE,
                F64X2_NE -> {
                    expressionVisitor.visitSimdNotEqualInstruction(opcode)
                }

                I8X16_LT_S,
                I8X16_LT_U,
                I16X8_LT_S,
                I16X8_LT_U,
                I32X4_LT_S,
                I32X4_LT_U,
                F32X4_LT,
                F64X2_LT -> {
                    expressionVisitor.visitSimdLessThanInstruction(opcode)
                }

                I8X16_LE_S,
                I8X16_LE_U,
                I16X8_LE_S,
                I16X8_LE_U,
                I32X4_LE_S,
                I32X4_LE_U,
                F32X4_LE,
                F64X2_LE -> {
                    expressionVisitor.visitSimdLessEqualInstruction(opcode)
                }

                I8X16_GT_S,
                I8X16_GT_U,
                I16X8_GT_S,
                I16X8_GT_U,
                I32X4_GT_S,
                I32X4_GT_U,
                F32X4_GT,
                F64X2_GT -> {
                    expressionVisitor.visitSimdGreaterThanInstruction(opcode)
                }

                I8X16_GE_S,
                I8X16_GE_U,
                I16X8_GE_S,
                I16X8_GE_U,
                I32X4_GE_S,
                I32X4_GE_U,
                F32X4_GE,
                F64X2_GE -> {
                    expressionVisitor.visitSimdGreaterEqualInstruction(opcode)
                }

                I8X16_NEG,
                I16X8_NEG,
                I32X4_NEG,
                I64X2_NEG,
                F32X4_NEG,
                F64X2_NEG -> {
                    expressionVisitor.visitSimdNegativeInstruction(opcode)
                }

                F32X4_ABS,
                F64X2_ABS -> {
                    expressionVisitor.visitSimdAbsInstruction(opcode)
                }

                F32X4_MIN,
                F64X2_MIN -> {
                    expressionVisitor.visitSimdMinInstruction(opcode)
                }

                F32X4_MAX,
                F64X2_MAX -> {
                    expressionVisitor.visitSimdMaxInstruction(opcode)
                }

                I8X16_ADD,
                I16X8_ADD,
                I32X4_ADD,
                I64X2_ADD,
                F32X4_ADD,
                F64X2_ADD -> {
                    expressionVisitor.visitSimdAddInstruction(opcode)
                }

                I8X16_SUB,
                I16X8_SUB,
                I32X4_SUB,
                I64X2_SUB,
                F32X4_SUB,
                F64X2_SUB -> {
                    expressionVisitor.visitSimdSubtractInstruction(opcode)
                }

                F32X4_DIV,
                F64X2_DIV -> {
                    expressionVisitor.visitSimdDivideInstruction(opcode)
                }

                I8X16_MUL,
                I16X8_MUL,
                I32X4_MUL,
                F32X4_MUL,
                F64X2_MUL -> {
                    expressionVisitor.visitSimdMultiplyInstruction(opcode)
                }

                F32X4_SQRT,
                F64X2_SQRT -> {
                    expressionVisitor.visitSimdSqrtInstruction(opcode)
                }

                F32X4_CONVERT_S_I32X4,
                F32X4_CONVERT_U_I32X4,
                F64X2_CONVERT_S_I64X2,
                F64X2_CONVERT_U_I64X2 -> {
                    expressionVisitor.visitSimdConvertInstruction(opcode)
                }

                I32X4_TRUNC_S_F32X4_SAT,
                I32X4_TRUNC_U_F32X4_SAT,
                I64X2_TRUNC_S_F64X2_SAT,
                I64X2_TRUNC_U_F64X2_SAT -> {
                    expressionVisitor.visitSimdTruncateInstruction(opcode)
                }

                MEMORY_FILL -> {
                    val memoryIndex = source.readVarUInt32()

                    expressionVisitor.visitMemoryFillInstruction(memoryIndex)
                }

                MEMORY_COPY -> {
                    val targetIndex = source.readVarUInt32()
                    val sourceIndex = source.readVarUInt32()

                    expressionVisitor.visitMemoryCopyInstruction(targetIndex, sourceIndex)
                }

                MEMORY_INIT -> {
                    val segmentIndex = source.readVarUInt32()
                    val memoryIndex = source.readVarUInt32()

                    expressionVisitor.visitMemoryInitInstruction(memoryIndex, segmentIndex)
                }

                TABLE_INIT -> {
                    val segmentIndex = source.readVarUInt32()
                    val tableIndex = source.readVarUInt32()

                    expressionVisitor.visitTableInitInstruction(segmentIndex, tableIndex)
                }

                DATA_DROP -> {
                    val segmentIndex = source.readVarUInt32()

                    expressionVisitor.visitDataDropInstruction(segmentIndex)
                }

                ELEMENT_DROP -> {
                    val segmentIndex = source.readVarUInt32()

                    expressionVisitor.visitElementDropInstruction(segmentIndex)
                }

                TABLE_SIZE -> {
                    val tableIndex = source.readVarUInt32()

                    expressionVisitor.visitTableSizeInstruction(tableIndex)
                }

                TABLE_GROW -> {
                    val tableIndex = source.readVarUInt32()
                    val value = source.readVarUInt32()
                    val delta = source.readVarUInt32()

                    expressionVisitor.visitTableGrowInstruction(tableIndex, value, delta)
                }

                TABLE_FILL -> {
                    val tableIndex = source.readVarUInt32()

                    expressionVisitor.visitTableFillInstruction(tableIndex)
                }

                TABLE_COPY -> {
                    val targetTableIndex = source.readVarUInt32()
                    val sourceTableIndex = source.readVarUInt32()

                    expressionVisitor.visitTableCopyInstruction(targetTableIndex, sourceTableIndex)
                }

                CALL_REF -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid call_ref code: reference types not enabled.")
                    }

                    TODO()
                }

                RETURN_CALL_REF -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid return_call_ref code: reference types not enabled.")
                    }

                    TODO()
                }

                REF_AS_NON_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid ref_as_non_null code: reference types not enabled.")
                    }

                    TODO()
                }

                BR_ON_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid br_on_null code: reference types not enabled.")
                    }

                    TODO()
                }

                BR_ON_NON_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid br_on_non_null code: reference types not enabled.")
                    }

                    TODO()
                }

                RETURN_CALL -> {
                    if (!context.options.features.isTailCallsEnabled) {
                        throw ParserException("Invalid return_call code: tail call not enabled.")
                    }

                    TODO()
                }

                RETURN_CALL_INDIRECT -> {
                    if (!context.options.features.isTailCallsEnabled) {
                        throw ParserException("Invalid return_call_indirect code: tail call not enabled.")
                    }

                    TODO()
                }

                DELEGATE -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid delegate code: exception handling not enabled.")
                    }

                    TODO()
                }

                CATCH_ALL -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch_call code: exception handling not enabled.")
                    }

                    TODO()
                }

                SELECT_T -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid select_t code: reference types not enabled.")
                    }

                    TODO()
                }

                TRY_TABLE -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid try_table code: exception handling not enabled.")
                    }

                    val blockType = source.readBlockType()
                    val numberOfCatches = source.readVarUInt32()

                    val handlers = mutableListOf<TryCatchArgument>()
                    (0u until numberOfCatches).forEach { _ ->
                        when (source.readUInt8()) {
                            TryCatchKind.CATCH.kindId -> {
                                val tagIndex = source.readVarUInt32()
                                val labelIndex = source.readVarUInt32()

                                handlers.add(TryCatchArgument(TryCatchKind.CATCH, tagIndex, labelIndex))
                            }

                            TryCatchKind.CATCH_REF.kindId -> {
                                val tagIndex = source.readVarUInt32()
                                val labelIndex = source.readVarUInt32()

                                handlers.add(TryCatchArgument(TryCatchKind.CATCH, tagIndex, labelIndex))
                            }

                            TryCatchKind.CATCH_ALL.kindId -> {
                                val labelIndex = source.readVarUInt32()

                                handlers.add(TryCatchArgument(TryCatchKind.CATCH, tag = null, label = labelIndex))
                            }

                            TryCatchKind.CATCH_ALL_REF.kindId -> {
                                val labelIndex = source.readVarUInt32()

                                handlers.add(TryCatchArgument(TryCatchKind.CATCH, tag = null, label = labelIndex))
                            }

                            else -> {
                                throw ParserException("Invalid try_table handler opcode: $opcode")
                            }
                        }
                    }

                    expressionVisitor.visitTryTableInstruction(blockType, handlers)
                }

                GET_TABLE -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid table.get code: reference types not enabled.")
                    }

                    TODO()
                }

                SET_TABLE -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid table.set code: reference types not enabled.")
                    }

                    TODO()
                }

                REF_NULL -> {
                    val type = source.readType()

                    expressionVisitor.visitReferenceNullInstruction(type)
                }

                REF_IS_NULL -> {
                    expressionVisitor.visitReferenceIsNullInstruction()
                }

                REF_FUNC -> {
                    val functionIndex = source.readIndex()

                    expressionVisitor.visitReferenceFunctionInstruction(functionIndex)
                }

                REF_EQ -> {
                    expressionVisitor.visitReferenceEqualInstruction()
                }

                ATOMIC_FENCE -> {
                    val reserved = source.readVarUInt32()

                    expressionVisitor.visitAtomicFenceInstruction(reserved)
                }
            }
        }
    }
}
