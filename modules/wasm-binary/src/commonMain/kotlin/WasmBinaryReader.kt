@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary

import org.wasmium.wasm.binary.tree.*
import org.wasmium.wasm.binary.tree.Opcode.*
import org.wasmium.wasm.binary.tree.SectionKind.*
import org.wasmium.wasm.binary.visitors.*
import kotlin.text.Typography.section

public class WasmBinaryReader(
    protected val options: ReaderOptions,
    protected val source: WasmSource,
    protected val visitor: ModuleVisitor,
) {
    /** Total number of imported functions. */
    protected var numberFunctionImports: UInt = 0u

    /** Total number of imported tables. */
    protected var numberTableImports: UInt = 0u

    /** Total number of imported memories. */
    protected var numberMemoryImports: UInt = 0u

    /** Total number of imported globals. */
    protected var numberGlobalImports: UInt = 0u

    /** Total number of imported exceptions. */
    protected var numberExceptionImports: UInt = 0u

    /** Total number of signatures. */
    protected var numberSignatures: UInt = 0u

    /** Total number of tables. */
    protected var numberTables: UInt = 0u

    /** Total number of memories. */
    protected var numberMemories: UInt = 0u

    /** Total number of globals. */
    protected var numberGlobals: UInt = 0u

    /** Total number of imports. */
    protected var numberImports: UInt = 0u

    /** Total number of functions. */
    protected var numberFunctions: UInt = 0u

    /** Total number of exports. */
    protected var numberExports: UInt = 0u

    /** Total number of elements. */
    protected var numberElementSegments: UInt = 0u

    /** Total number of exceptions. */
    protected var numberExceptions: UInt = 0u

    protected var seenNameSection: Boolean = false

    protected var previousSection: SectionKind = NONE

    public fun read() {
        // minimum allowed module size
        val minSize = 8u
        if (!source.require(minSize)) {
            throw ParserException("Expecting module size of at least: $minSize")
        }

        // read magic
        val magic: UInt = source.readUInt32()
        if (magic != WasmBinary.MAGIC_NUMBER) {
            throw ParserException("Module does not start with: $magic")
        }

        // read version
        val version: UInt = source.readUInt32()
        if ((version <= 0u) || (version > WasmVersion.V1.version)) {
            throw ParserException("Unsupported version number: $version")
        }

        visitor.visit(version)
        readSections(visitor)
        visitor.visitEnd()

        // maximum allowed module size
        if (source.position > WasmBinary.MAX_MODULE_SIZE) {
            throw ParserException("Module size of ${source.position} is too large, maximum allowed is ${WasmBinary.MAX_MODULE_SIZE}")
        }
    }

    protected fun readSections(visitor: ModuleVisitor) {
        // current section
        var section: SectionKind
        // total read sections
        var numberOfSections = 0u
        while (!source.exhausted()) {
            numberOfSections++

            if (numberOfSections > WasmBinary.MAX_SECTIONS) {
                throw ParserException("Sections size of $numberOfSections exceed the maximum of ${WasmBinary.MAX_SECTIONS}")
            }

            section = source.readSectionKind()

            if (section != CUSTOM) {
                // not consider CUSTOM section for ordering
                if (section.sectionKindId < previousSection.sectionKindId) {
                    throw ParserException("Invalid section order of ${previousSection.name} followed by ${section.name}")
                }

                previousSection = section
            }

            val payloadSize: UInt = source.readVarUInt32()
            if (payloadSize > WasmBinary.MAX_SECTION_LENGTH) {
                throw ParserException("Section size of $payloadSize exceed the maximum of ${WasmBinary.MAX_SECTION_LENGTH}")
            }

            if (!source.require(payloadSize)) {
                throw ParserException("Section payload greater then input.")
            }

            val startPosition = source.position

            when (section) {
                CUSTOM -> readCustomSection(visitor, payloadSize)
                TYPE -> readTypeSection(visitor)
                IMPORT -> readImportSection(visitor)
                FUNCTION -> readFunctionSection(visitor)
                TABLE -> readTableSection(visitor)
                MEMORY -> readMemorySection(visitor)
                GLOBAL -> readGlobalSection(visitor)
                EXPORT -> readExportSection(visitor)
                START -> readStartSection(visitor)
                ELEMENT -> readElementSection(visitor)
                CODE -> {
                    if (options.isSkipCodeSection) {
                        source.skip(payloadSize)
                    } else {
                        readCodeSection(visitor)
                    }
                }
                DATA -> readDataSection(visitor)
                else -> throw ParserException("Invalid section id: $section")
            }

            if (payloadSize != source.position - startPosition) {
                throw ParserException("Invalid size of section id: $section, expected: $payloadSize, actual: ${source.position - startPosition}")
            }

            if (seenNameSection && section != CUSTOM) {
                throw ParserException("${section.name} section can not occur after Name section")
            }
        }
    }

    protected fun readCustomSection(visitor: ModuleVisitor, payloadSize: UInt) {
        val startIndex = source.position

        val sectionName: String = source.readString()

        val sectionPayloadSize = payloadSize - (source.position - startIndex)

        when {
            sectionName == WasmBinary.SECTION_NAME_NAME -> {
                seenNameSection = true
                if (options.isDebugNamesEnabled) {
                    readNamesSection(visitor, startIndex, sectionPayloadSize)
                } else {
                    source.skip(payloadSize)
                }
            }

            sectionName.startsWith(WasmBinary.SECTION_NAME_RELOCATION) -> {
                readRelocationSection(visitor)
            }

            sectionName == WasmBinary.SECTION_NAME_LINKING -> {
                readLinkingSection(visitor)
            }

            sectionName == WasmBinary.SECTION_NAME_EXCEPTION -> {
                if (options.features.isExceptionHandlingEnabled) {
                    readExceptionSection(visitor)
                } else {
                    source.skip(payloadSize)
                }
            }

            sectionName == WasmBinary.SECTION_NAME_SOURCE_MAPPING_URL -> {
                readSourceMapSection(visitor)
            }

            else -> {
                readUnknownSection(visitor, sectionName, startIndex, sectionPayloadSize)
            }
        }
    }

    protected fun readNamesSection(visitor: ModuleVisitor, startIndex: UInt, sectionPayloadSize: UInt) {
        val nameSectionVisitor: NameSectionVisitor = visitor.visitNameSection()

        var previousNameKind = NameKind.NONE

        while (source.position < startIndex + sectionPayloadSize) {
            val nameKind: NameKind = source.readNameKind()

            if (nameKind.nameKindId < previousNameKind.nameKindId) {
                throw ParserException("Name kind sub subsection out of order")
            }

            val subsectionSize: UInt = source.readVarUInt32()
            if (!source.require(subsectionSize)) {
                throw ParserException("Name subsection greater then input")
            }

            val startIndex: UInt = source.position

            when (nameKind) {
                NameKind.MODULE -> {
                    val moduleName: String = source.readString()

                    nameSectionVisitor.visitModuleName(moduleName)
                }

                NameKind.TABLE,
                NameKind.MEMORY,
                NameKind.GLOBAL,
                NameKind.ELEMENT,
                NameKind.DATA,
                NameKind.TAG,
                NameKind.FUNCTION -> {
                    val numberFunctionNames: UInt = source.readVarUInt32()
                    var previousFunctionIndex: UInt? = null

                    for (function in 0u until numberFunctionNames) {
                        val functionIndex: UInt = source.readVarUInt32()

                        if (functionIndex > getNumberTotalFunctions()) {
                            throw ParserException("Invalid function index: %$functionIndex")
                        }

                        previousFunctionIndex?.let {
                            if (functionIndex < it) {
                                throw ParserException("Function index out of order:$functionIndex")
                            }
                        }

                        val functionName: String = source.readString()

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

                        previousFunctionIndex = functionIndex
                    }
                }

                NameKind.LABEL,
                NameKind.LOCAL -> {
                    val numberFunctions: UInt = source.readVarUInt32()
                    var previousFunctionIndex: UInt? = null

                    for (function in 0u until numberFunctions) {
                        val functionIndex: UInt = source.readVarUInt32()

                        if (functionIndex > getNumberTotalFunctions()) {
                            throw ParserException("Invalid function index: %$functionIndex")
                        }

                        previousFunctionIndex?.let {
                            if (functionIndex < it) {
                                throw ParserException("Locals function index out of order:$functionIndex")
                            }
                        }

                        val numberLocals: UInt = source.readVarUInt32()
                        var previousLocalIndex: UInt? = null
                        var local: UInt = 0u
                        while (local < numberLocals) {
                            val nameLocalIndex: UInt = source.readVarUInt32()

                            previousLocalIndex?.let {
                                if (nameLocalIndex < it) {
                                    throw ParserException("Locals function index out of order:$functionIndex")
                                }
                            }

                            val localName: String = source.readString()

                            when (nameKind) {
                                NameKind.LOCAL -> nameSectionVisitor.visitLocalName(functionIndex, nameLocalIndex, localName)
                                NameKind.LABEL -> nameSectionVisitor.visitLabelName(functionIndex, nameLocalIndex, localName)
                                else -> throw ParserException("Unsupported name section: $nameKind")
                            }

                            previousLocalIndex = nameLocalIndex
                            local++
                        }

                        previousFunctionIndex = functionIndex
                    }
                }

                else -> throw ParserException("Unsupported name section: $nameKind")
            }

            if (subsectionSize != source.position - startIndex) {
                throw ParserException("Invalid size of subsection id: $nameKind")
            }
        }

        nameSectionVisitor.visitEnd()
    }

    protected fun readLinkingSection(visitor: ModuleVisitor) {
        val linkingSectionVisitor: LinkingSectionVisitor = visitor.visitLinkingSection()

        while (!source.exhausted()) {
            val linkingKind: LinkingKind = source.readLinkingKind()

            val subsectionSize: UInt = source.readVarUInt32()
            if (!source.require(subsectionSize)) {
                throw ParserException("Linking subsection greater then input")
            }

            val startIndex: UInt = source.position

            when (linkingKind) {
                LinkingKind.SYMBOL_TABLE -> {
                    val symbolCount: UInt = source.readVarUInt32()

                    for (symbolIndex in 0u until symbolCount) {
                        val symbolType: LinkingSymbolType = LinkingSymbolType.fromLinkingSymbolTypeId(source.readVarInt7())
                        val flags: UInt = source.readUInt32()

                        linkingSectionVisitor.visitSymbol(symbolIndex, symbolType, flags)

                        when (symbolType) {
                            LinkingSymbolType.FUNCTION, LinkingSymbolType.GLOBAL -> {
                                val index: UInt = source.readIndex()
                                var name: String? = null

                                if ((flags and WasmBinary.LINKING_SYMBOL_FLAG_UNDEFINED) == 0u) {
                                    name = source.readString()
                                }

                                if (symbolType == LinkingSymbolType.FUNCTION) {
                                    linkingSectionVisitor.visitFunctionSymbol(symbolIndex, flags, name!!, index)
                                } else {
                                    linkingSectionVisitor.visitGlobalSymbol(symbolIndex, flags, name!!, index)
                                }
                            }

                            LinkingSymbolType.DATA -> {
                                var segment: UInt = 0u
                                var offset: UInt = 0u
                                var size: UInt = 0u

                                val name: String = source.readString()

                                if ((flags and WasmBinary.LINKING_SYMBOL_FLAG_UNDEFINED) == 0u) {
                                    segment = source.readVarUInt32()
                                    offset = source.readVarUInt32()
                                    size = source.readVarUInt32()
                                }
                                linkingSectionVisitor.visitDataSymbol(symbolIndex, flags, name, segment, offset, size)
                            }

                            LinkingSymbolType.SECTION -> {
                                val index: UInt = source.readIndex()

                                linkingSectionVisitor.visitSectionSymbol(symbolIndex, flags, index)
                            }

                            else -> throw IllegalArgumentException()
                        }
                    }
                }

                LinkingKind.SEGMENT_INFO -> {
                    val segmentCount: UInt = source.readVarUInt32()

                    for (index in 0u until segmentCount) {
                        val name: String = source.readString()
                        val alignment: UInt = source.readVarUInt32()
                        val flags: UInt = source.readUInt32()

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

    protected fun readSourceMapSection(visitor: ModuleVisitor) {
        val sourceMapURL = source.readString()
    }

    protected fun readUnknownSection(visitor: ModuleVisitor, sectionName: String, startIndex: UInt, sectionPayloadSize: UInt) {
        val payload = ByteArray(sectionPayloadSize.toInt())
        source.readTo(payload, 0u, sectionPayloadSize)

        val customSectionVisitor: CustomSectionVisitor = visitor.visitCustomSection()
        customSectionVisitor.visitSection(sectionName, payload)
        customSectionVisitor.visitEnd()
    }

    protected fun readRelocationSection(visitor: ModuleVisitor) {
        val sectionKind: SectionKind = source.readSectionKind()

        var sectionName: String? = null
        if (sectionKind == CUSTOM) {
            sectionName = source.readString()
        }

        val numberRelocations: UInt = source.readVarUInt32()

        val relocationVisitor: RelocationSectionVisitor = visitor.visitRelocationSection()
        relocationVisitor.visitSection(sectionKind, sectionName!!)

        for (relocationIndex in 0u until numberRelocations) {
            val relocationKind: RelocationKind = source.readRelocationKind()

            when (relocationKind) {
                RelocationKind.FUNC_INDEX_LEB, RelocationKind.TABLE_INDEX_SLEB, RelocationKind.TABLE_INDEX_I32, RelocationKind.TYPE_INDEX_LEB, RelocationKind.GLOBAL_INDEX_LEB -> {
                    val offset: UInt = source.readIndex()
                    val index: UInt = source.readIndex()

                    relocationVisitor.visitRelocation(relocationKind, offset, index)
                }

                RelocationKind.MEMORY_ADDRESS_LEB, RelocationKind.MEMORY_ADDRESS_SLEB, RelocationKind.MEMORY_ADDRESS_I32, RelocationKind.FUNCTION_OFFSET_I32, RelocationKind.SECTION_OFFSET_I32 -> {
                    val offset: UInt = source.readIndex()
                    val index: UInt = source.readIndex()
                    val addend: Int = source.readVarInt32()

                    relocationVisitor.visitRelocation(relocationKind, offset, index, addend)
                }

                else -> throw ParserException("Unsupported relocation section: $relocationKind")
            }
        }

        relocationVisitor.visitEnd()
    }

    protected fun readExceptionSection(visitor: ModuleVisitor) {
        this.numberExceptions = source.readVarUInt32()

        if (this.numberExceptions > WasmBinary.MAX_EXCEPTIONS) {
            throw ParserException("Number of exceptions $numberExceptions exceed the maximum of ${WasmBinary.MAX_EXCEPTIONS}")
        }

        val exceptionVisitor: ExceptionSectionVisitor = visitor.visitExceptionSection()
        for (index in 0u until numberExceptions) {
            val exceptionIndex = numberExceptionImports + index

            val exceptionType = readExceptionType()

            exceptionVisitor.visitExceptionType(exceptionIndex, exceptionType)
        }

        exceptionVisitor.visitEnd()
    }

    protected fun readExceptionType(): Array<WasmType> {
        val numberExceptionTypes: UInt = source.readVarUInt32()

        if (numberExceptionTypes > WasmBinary.MAX_EXCEPTION_TYPES) {
            throw ParserException("Number of exceptions types $numberExceptionTypes exceed the maximum of ${WasmBinary.MAX_EXCEPTIONS}")
        }

        val exceptionTypes = Array(numberExceptionTypes.toInt()) { WasmType.NONE }
        for (exceptionIndex in 0u until numberExceptionTypes) {
            val exceptionType: WasmType = source.readType()

            if (!exceptionType.isValueType()) {
                throw ParserException("Invalid exception type: %#$exceptionType")
            }

            exceptionTypes[exceptionIndex.toInt()] = exceptionType
        }

        return exceptionTypes
    }

    protected fun readDataSection(visitor: ModuleVisitor) {
        val dataSegmentCount: UInt = source.readVarUInt32()

        if (dataSegmentCount >= 0u && getNumberTotalMemories() == 0u) {
            throw ParserException("Data section without memory section")
        }

        if (dataSegmentCount > WasmBinary.MAX_DATA_SEGMENTS) {
            throw ParserException("Number of data segments $numberGlobals exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENTS}")
        }

        val dataVisitor = visitor.visitDataSection()
        for (index in 0u until dataSegmentCount) {
            val startIndex: UInt = source.position
            val dataSegmentVisitor = dataVisitor.visitDataSegment(index)

            val mode = source.readVarUInt32()
            when (mode) {
                0u -> {
                    val initializerExpressionVisitor = dataSegmentVisitor.visitInitializerExpression()
                    readInitExpression(initializerExpressionVisitor, true)
                    initializerExpressionVisitor.visitEnd()

                    val dataSize: UInt = source.readVarUInt32()

                    if (dataSize + (source.position - startIndex) > WasmBinary.MAX_DATA_SEGMENT_LENGTH) {
                        throw ParserException("Data segment size of $dataSize${source.position - startIndex} exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENT_LENGTH}")
                    }

                    val data = ByteArray(dataSize.toInt())
                    source.readTo(data, 0u, dataSize)

                    dataSegmentVisitor.visitData(data)
                }

                1u -> {
                    val dataSize: UInt = source.readVarUInt32()

                    if (dataSize + (source.position - startIndex) > WasmBinary.MAX_DATA_SEGMENT_LENGTH) {
                        throw ParserException("Data segment size of $dataSize${source.position - startIndex} exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENT_LENGTH}")
                    }

                    val data = ByteArray(dataSize.toInt())
                    source.readTo(data, 0u, dataSize)

                    if (dataSize != source.position - startIndex) {
                        throw ParserException("Invalid size of section id: $section")
                    }

                    dataSegmentVisitor.visitData(data)
                }

                2u -> {
                    val memoryIndex: UInt = source.readVarUInt32()
                    if (memoryIndex != 0u) {
                        throw ParserException("Bad memory index, must be 0.")
                    }

                    dataSegmentVisitor.visitMemoryIndex(memoryIndex)

                    val initializerExpressionVisitor = dataSegmentVisitor.visitInitializerExpression()
                    readInitExpression(initializerExpressionVisitor, true)
                    initializerExpressionVisitor.visitEnd()

                    val dataSize: UInt = source.readVarUInt32()

                    if (dataSize + (source.position - startIndex) > WasmBinary.MAX_DATA_SEGMENT_LENGTH) {
                        throw ParserException("Data segment size of $dataSize${source.position - startIndex} exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENT_LENGTH}")
                    }

                    val data = ByteArray(dataSize.toInt())
                    source.readTo(data, 0u, dataSize)

                    if (dataSize != source.position - startIndex) {
                        throw ParserException("Invalid size of section id: $section")
                    }

                    dataSegmentVisitor.visitData(data)
                }

                else -> throw ParserException("Invalid mode: $mode")
            }

            dataSegmentVisitor.visitEnd()
        }

        dataVisitor.visitEnd()
    }

    protected fun readMemorySection(visitor: ModuleVisitor) {
        this.numberMemories = source.readVarUInt32()
        if (numberMemories == 0u) {
            return
        }

        if (numberMemories > WasmBinary.MAX_MEMORIES) {
            throw ParserException("Number of memories $numberMemories exceed the maximum of ${WasmBinary.MAX_MEMORIES}")
        }

        val memoryVisitor: MemorySectionVisitor = visitor.visitMemorySection()
        for (index in 0u until numberMemories) {
            val memoryIndex = numberMemoryImports + index

            val limits: ResizableLimits = source.readResizableLimits()

            if (limits.initial > WasmBinary.MAX_MEMORY_PAGES) {
                throw ParserException("Invalid initial memory pages")
            }

            if (limits.isShared() && (limits.maximum == null)) {
                throw ParserException("Shared memory must have a max size")
            }

            if (limits.maximum != null) {
                if (limits.maximum > WasmBinary.MAX_MEMORY_PAGES) {
                    throw ParserException("Invalid memory max page")
                }

                if (limits.initial > limits.maximum) {
                    throw ParserException("Initial memory size greater than maximum")
                }
            }

            memoryVisitor.visitMemory(memoryIndex, limits)
        }

        memoryVisitor.visitEnd()
    }

    protected fun readCodeSection(visitor: ModuleVisitor) {
        val codeSize: UInt = source.readVarUInt32()

        if (codeSize != this.numberFunctions) {
            throw ParserException("Invalid function section size: $codeSize must be equal to functionSize of: ${this.numberFunctions}")
        }

        val codeVisitor: CodeSectionVisitor = visitor.visitCodeSection()

        for (index in 0u until codeSize) {
            val functionIndex = this.numberFunctionImports + index

            val functionBodyVisitor = codeVisitor.visitFunctionBody(functionIndex)

            val bodySize: UInt = source.readVarUInt32()
            if (bodySize == 0u) {
                throw ParserException("Empty function size")
            }

            if (bodySize > WasmBinary.MAX_FUNCTION_SIZE) {
                throw ParserException("Function body size $bodySize exceed the maximum of ${WasmBinary.MAX_FUNCTION_SIZE}")
            }

            val startAvailable: UInt = source.position

            var totalLocals: UInt = 0u
            val localsSize: UInt = source.readVarUInt32()

            if (localsSize > WasmBinary.MAX_FUNCTION_LOCALS) {
                throw ParserException("Number of function locals $localsSize exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS}")
            }

            for (localIndex in 0u until localsSize) {
                val numberLocalTypes: UInt = source.readVarUInt32()

                val localType: WasmType = source.readType()
                if (!localType.isValueType()) {
                    throw ParserException("Invalid local type: %#$localType")
                }

                totalLocals += numberLocalTypes

                if (numberLocalTypes > WasmBinary.MAX_FUNCTION_LOCALS_TOTAL) {
                    throw ParserException("Number of total function locals $totalLocals exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS_TOTAL}")
                }

                functionBodyVisitor.visitLocalVariable(localIndex, numberLocalTypes, localType)
            }

            val remainingSize: UInt = bodySize - (source.position - startAvailable)
            readFunctionBody(functionBodyVisitor, remainingSize)

            if (bodySize != source.position - startAvailable) {
                throw ParserException("Binary offset at function exit not at expected location.")
            }

            functionBodyVisitor.visitEnd()
        }

        codeVisitor.visitEnd()
    }

    protected fun readFunctionBody(functionBodyVisitor: FunctionBodyVisitor, remainingSize: UInt) {
        val endBodyPosition: UInt = source.position + remainingSize

        var seenEndOpcode = false
        functionBodyVisitor.visitCode()

        var numberOfInstructions = 0u
        while (endBodyPosition - source.position > 0u) {
            val opcode: Opcode = source.readOpcode()

            numberOfInstructions++
            seenEndOpcode = false

            when (opcode) {
                UNREACHABLE -> {
                    functionBodyVisitor.visitUnreachableInstruction()
                }

                NOP -> {
                    functionBodyVisitor.visitNopInstruction()
                }

                BLOCK -> {
                    val type: WasmType = source.readType()

                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitBlockInstruction(blockType)
                }

                LOOP -> {
                    val type: WasmType = source.readType()

                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitLoopInstruction(blockType)
                }

                IF -> {
                    val type: WasmType = source.readType()

                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitIfInstruction(blockType)
                }

                ELSE -> {
                    functionBodyVisitor.visitElseInstruction()
                }

                TRY -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid try code: exceptions not enabled.")
                    }

                    val type: WasmType = source.readType()
                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitTryInstruction(blockType)
                }

                CATCH -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch code: exceptions not enabled.")
                    }

                    functionBodyVisitor.visitCatchInstruction()
                }

                THROW -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid throw code: exceptions not enabled.")
                    }

                    val exceptionIndex: UInt = source.readIndex()
                    if (exceptionIndex >= getNumberTotalExceptions()) {
                        throw ParserException("invalid call exception index: %$exceptionIndex")
                    }

                    functionBodyVisitor.visitThrowInstruction(exceptionIndex)
                }

                RETHROW -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid rethrow code: exceptions not enabled.")
                    }

                    functionBodyVisitor.visitRethrowInstruction()
                }

                THROW_REF -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch code: exceptions not enabled.")
                    }

                    functionBodyVisitor.visitThrowRefInstruction()
                }

                END -> {
                    if (endBodyPosition == source.position) {
                        seenEndOpcode = true
                        functionBodyVisitor.visitEndFunctionInstruction()
                    } else {
                        functionBodyVisitor.visitEndInstruction()
                    }
                }

                BR -> {
                    val depth: UInt = source.readIndex()

                    functionBodyVisitor.visitBrInstruction(depth)
                }

                BR_IF -> {
                    val depth: UInt = source.readIndex()

                    functionBodyVisitor.visitBrIfInstruction(depth)
                }

                BR_TABLE -> {
                    val numberTargets: UInt = source.readVarUInt32()
                    val targets: Array<UInt> = Array(numberTargets.toInt()) { 0u }

                    for (targetIndex in 0u until numberTargets) {
                        val depth: UInt = source.readIndex()

                        targets[targetIndex.toInt()] = depth
                    }

                    val defaultTarget: UInt = source.readIndex()

                    functionBodyVisitor.visitBrTableInstruction(targets, defaultTarget)
                }

                RETURN -> {
                    functionBodyVisitor.visitReturnInstruction()
                }

                CALL -> {
                    val funcIndex: UInt = source.readIndex()

                    if (funcIndex >= getNumberTotalFunctions()) {
                        throw ParserException("Invalid call function index: %$funcIndex")
                    }

                    functionBodyVisitor.visitCallInstruction(funcIndex)
                }

                CALL_INDIRECT -> {
                    val signatureIndex: UInt = source.readIndex()
                    if (signatureIndex >= numberSignatures) {
                        throw ParserException("Invalid call_indirect signature index")
                    }

                    val reserved: UInt = source.readVarUInt32()
                    if (reserved != 0u) {
                        throw ParserException("Call_indirect reserved value must be 0")
                    }

                    functionBodyVisitor.visitCallIndirectInstruction(signatureIndex, reserved = false)
                }

                DROP -> {
                    functionBodyVisitor.visitDropInstruction()
                }

                SELECT -> {
                    functionBodyVisitor.visitSelectInstruction()
                }

                GET_GLOBAL -> {
                    val globalIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitGetGlobalInstruction(globalIndex)
                }

                SET_LOCAL -> {
                    val localIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitSetLocalInstruction(localIndex)
                }

                TEE_LOCAL -> {
                    val localIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitTeeLocalInstruction(localIndex)
                }

                GET_LOCAL -> {
                    val localIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitGetLocalInstruction(localIndex)
                }

                SET_GLOBAL -> {
                    val globalIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitSetGlobalInstruction(globalIndex)
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
                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitLoadInstruction(opcode, alignment, offset)
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
                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitStoreInstruction(opcode, alignment, offset)
                }

                MEMORY_SIZE -> {
                    val reserved: UInt = source.readVarUInt1()
                    if (reserved != 0u) {
                        throw ParserException("MemorySize reserved value must be 0.")
                    }
                    functionBodyVisitor.visitMemorySizeInstruction(reserved = false)
                }

                MEMORY_GROW -> {
                    val reserved: UInt = source.readVarUInt1()
                    if (reserved != 0u) {
                        throw ParserException("MemoryGrow reserved value must be 0")
                    }

                    functionBodyVisitor.visitMemoryGrowInstruction(reserved = false)
                }

                I32_CONST -> {
                    val value: Int = source.readVarInt32()

                    functionBodyVisitor.visitConstInt32Instruction(value)
                }

                I64_CONST -> {
                    val value: Long = source.readVarInt64()

                    functionBodyVisitor.visitConstInt64Instruction(value)
                }

                F32_CONST -> {
                    val value: Float = source.readFloat32()

                    functionBodyVisitor.visitConstFloat32Instruction(value)
                }

                F64_CONST -> {
                    val value: Double = source.readFloat64()

                    functionBodyVisitor.visitConstFloat64Instruction(value)
                }

                I32_EQZ -> {
                    functionBodyVisitor.visitEqualZeroInstruction(opcode)
                }

                I32_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                I32_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                I32_LT_S -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                I32_LE_S -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                I32_LT_U -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                I32_LE_U -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                I32_GT_S -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                I32_GE_S -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                I32_GT_U -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                I32_GE_U -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                I64_EQZ -> {
                    functionBodyVisitor.visitEqualZeroInstruction(opcode)
                }

                I64_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                I64_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                I64_LT_S -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                I64_LE_S -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                I64_LT_U -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                I64_LE_U -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                I64_GT_S -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                I64_GE_S -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                I64_GT_U -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                I64_GE_U -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                F32_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                F32_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                F32_LT -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                F32_LE -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                F32_GT -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                F32_GE -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                F64_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                F64_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                F64_LT -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                F64_LE -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                F64_GT -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                F64_GE -> {
                    functionBodyVisitor.visitCompareInstruction(opcode)
                }

                I32_CLZ -> {
                    functionBodyVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                I32_CTZ -> {
                    functionBodyVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                I32_POPCNT -> {
                    functionBodyVisitor.visitPopulationCountInstruction(opcode)
                }

                I32_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                I32_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                I32_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                I32_DIV_S -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                I32_DIV_U -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                I32_REM_S -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                I32_REM_U -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                I32_AND -> {
                    functionBodyVisitor.visitAndInstruction(opcode)
                }

                I32_OR -> {
                    functionBodyVisitor.visitOrInstruction(opcode)
                }

                I32_XOR -> {
                    functionBodyVisitor.visitXorInstruction(opcode)
                }

                I32_SHL -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_SHR_U -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_SHR_S -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_ROTL -> {
                    functionBodyVisitor.visitRotateLeftInstruction(opcode)
                }

                I32_ROTR -> {
                    functionBodyVisitor.visitRotateRightInstruction(opcode)
                }

                I64_CLZ -> {
                    functionBodyVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                I64_CTZ -> {
                    functionBodyVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                I64_POPCNT -> {
                    functionBodyVisitor.visitPopulationCountInstruction(opcode)
                }

                I64_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                I64_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                I64_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                I64_DIV_S -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                I64_DIV_U -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                I64_REM_S -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                I64_REM_U -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                I64_AND -> {
                    functionBodyVisitor.visitAndInstruction(opcode)
                }

                I64_OR -> {
                    functionBodyVisitor.visitOrInstruction(opcode)
                }

                I64_XOR -> {
                    functionBodyVisitor.visitXorInstruction(opcode)
                }

                I64_SHL -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I64_SHR_U -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I64_SHR_S -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I64_ROTL -> {
                    functionBodyVisitor.visitRotateLeftInstruction(opcode)
                }

                I64_ROTR -> {
                    functionBodyVisitor.visitRotateRightInstruction(opcode)
                }

                F32_ABS -> {
                    functionBodyVisitor.visitAbsoluteInstruction(opcode)
                }

                F32_NEG -> {
                    functionBodyVisitor.visitNegativeInstruction(opcode)
                }

                F32_CEIL -> {
                    functionBodyVisitor.visitCeilingInstruction(opcode)
                }

                F32_FLOOR -> {
                    functionBodyVisitor.visitFloorInstruction(opcode)
                }

                F32_TRUNC -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                F32_NEAREST -> {
                    functionBodyVisitor.visitNearestInstruction(opcode)
                }

                F32_SQRT -> {
                    functionBodyVisitor.visitSqrtInstruction(opcode)
                }

                F32_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                F32_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                F32_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                F32_DIV -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                F32_MIN -> {
                    functionBodyVisitor.visitMinInstruction(opcode)
                }

                F32_MAX -> {
                    functionBodyVisitor.visitMaxInstruction(opcode)
                }

                F32_COPYSIGN -> {
                    functionBodyVisitor.visitCopySignInstruction(opcode)
                }

                F64_ABS -> {
                    functionBodyVisitor.visitAbsoluteInstruction(opcode)
                }

                F64_NEG -> {
                    functionBodyVisitor.visitNegativeInstruction(opcode)
                }

                F64_CEIL -> {
                    functionBodyVisitor.visitCeilingInstruction(opcode)
                }

                F64_FLOOR -> {
                    functionBodyVisitor.visitFloorInstruction(opcode)
                }

                F64_TRUNC -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                F64_NEAREST -> {
                    functionBodyVisitor.visitNearestInstruction(opcode)
                }

                F64_SQRT -> {
                    functionBodyVisitor.visitSqrtInstruction(opcode)
                }

                F64_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                F64_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                F64_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                F64_DIV -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                F64_MIN -> {
                    functionBodyVisitor.visitMinInstruction(opcode)
                }

                F64_MAX -> {
                    functionBodyVisitor.visitMaxInstruction(opcode)
                }

                F64_COPYSIGN -> {
                    functionBodyVisitor.visitCopySignInstruction(opcode)
                }

                I32_WRAP_I64 -> {
                    functionBodyVisitor.visitWrapInstruction(opcode)
                }

                I32_TRUNC_SF32, I32_TRUNC_UF32, I32_TRUNC_SF64, I32_TRUNC_UF64 -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                I64_EXTEND_SI32, I64_EXTEND_UI32 -> {
                    functionBodyVisitor.visitExtendInstruction(opcode)
                }

                I64_TRUNC_SF32, I64_TRUNC_UF32, I64_TRUNC_SF64, I64_TRUNC_UF64 -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                F32_CONVERT_SI32, F32_CONVERT_UI32, F32_CONVERT_SI64, F32_CONVERT_UI64 -> {
                    functionBodyVisitor.visitConvertInstruction(opcode)
                }

                F32_DEMOTE_F64 -> {
                    functionBodyVisitor.visitDemoteInstruction(opcode)
                }

                F64_CONVERT_SI32, F64_CONVERT_UI32, F64_CONVERT_SI64, F64_CONVERT_UI64 -> {
                    functionBodyVisitor.visitConvertInstruction(opcode)
                }

                F64_PROMOTE_F32 -> {
                    functionBodyVisitor.visitPromoteInstruction(opcode)
                }

                I32_REINTERPRET_F32, I64_REINTERPRET_F64, F32_REINTERPRET_I32, F64_REINTERPRET_I64 -> {
                    functionBodyVisitor.visitReinterpretInstruction(opcode)
                }

                I32_EXTEND8_S, I32_EXTEND16_S, I64_EXTEND8_S, I64_EXTEND16_S, I64_EXTEND32_S -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid opcode: ${opcode.opcodeName} not enabled.")
                    }

                    functionBodyVisitor.visitExtendInstruction(opcode)
                }

                I32_TRUNC_S_SAT_F32, I32_TRUNC_U_SAT_F32, I32_TRUNC_S_SAT_F64, I32_TRUNC_U_SAT_F64, I64_TRUNC_S_SAT_F32, I64_TRUNC_U_SAT_F32, I64_TRUNC_S_SAT_F64, I64_TRUNC_U_SAT_F64 -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                MEMORY_ATOMIC_NOTIFY -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicWakeInstruction(opcode, alignment, offset)
                }

                MEMORY_ATOMIC_WAIT32,
                MEMORY_ATOMIC_WAIT64 -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicWaitInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_LOAD, I64_ATOMIC_LOAD, I32_ATOMIC_LOAD8_U, I32_ATOMIC_LOAD16_U, I64_ATOMIC_LOAD8_U, I64_ATOMIC_LOAD16_U, I64_ATOMIC_LOAD32_U -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicLoadInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_STORE, I64_ATOMIC_STORE, I32_ATOMIC_STORE8, I32_ATOMIC_STORE16, I64_ATOMIC_STORE8, I64_ATOMIC_STORE16, I64_ATOMIC_STORE32 -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicStoreInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_ADD, I64_ATOMIC_RMW_ADD, I32_ATOMIC_RMW8_U_ADD, I32_ATOMIC_RMW16_U_ADD, I64_ATOMIC_RMW8_U_ADD, I64_ATOMIC_RMW16_U_ADD, I64_ATOMIC_RMW32_U_ADD -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwAddInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_SUB, I64_ATOMIC_RMW_SUB, I32_ATOMIC_RMW8_U_SUB, I32_ATOMIC_RMW16_U_SUB, I64_ATOMIC_RMW8_U_SUB, I64_ATOMIC_RMW16_U_SUB, I64_ATOMIC_RMW32_U_SUB -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_AND, I64_ATOMIC_RMW_AND, I32_ATOMIC_RMW8_U_AND, I32_ATOMIC_RMW16_U_AND, I64_ATOMIC_RMW8_U_AND, I64_ATOMIC_RMW16_U_AND, I64_ATOMIC_RMW32_U_AND -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwAndInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_OR, I64_ATOMIC_RMW_OR, I32_ATOMIC_RMW8_U_OR, I32_ATOMIC_RMW16_U_OR, I64_ATOMIC_RMW8_U_OR, I64_ATOMIC_RMW16_U_OR, I64_ATOMIC_RMW32_U_OR -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwOrInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_XOR, I64_ATOMIC_RMW_XOR, I32_ATOMIC_RMW8_U_XOR, I32_ATOMIC_RMW16_U_XOR, I64_ATOMIC_RMW8_U_XOR, I64_ATOMIC_RMW16_U_XOR, I64_ATOMIC_RMW32_U_XOR -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwXorInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_XCHG, I64_ATOMIC_RMW_XCHG, I32_ATOMIC_RMW8_U_XCHG, I32_ATOMIC_RMW16_U_XCHG, I64_ATOMIC_RMW8_U_XCHG, I64_ATOMIC_RMW16_U_XCHG, I64_ATOMIC_RMW32_U_XCHG -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_CMPXCHG, I64_ATOMIC_RMW_CMPXCHG, I32_ATOMIC_RMW8_U_CMPXCHG, I32_ATOMIC_RMW16_U_CMPXCHG, I64_ATOMIC_RMW8_U_CMPXCHG, I64_ATOMIC_RMW16_U_CMPXCHG, I64_ATOMIC_RMW32_U_CMPXCHG -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
                }

                V128_CONST -> {
                    if (!options.features.isSIMDEnabled) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val value: V128Value = source.readV128()

                    functionBodyVisitor.visitSimdConstInstruction(value)
                }

                V128_LOAD -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitSimdLoadInstruction(opcode, alignment, offset)
                }

                V128_STORE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitSimdStoreInstruction(opcode, alignment, offset)
                }

                I8X16_SPLAT, I16X8_SPLAT, I32X4_SPLAT, I64X2_SPLAT, F32X4_SPLAT, F64X2_SPLAT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val value: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitSimdSplatInstruction(opcode, value)
                }

                I8X16_EXTRACT_LANE_S, I8X16_EXTRACT_LANE_U, I16X8_EXTRACT_LANE_S, I16X8_EXTRACT_LANE_U, I32X4_EXTRACT_LANE, I64X2_EXTRACT_LANE, F32X4_EXTRACT_LANE, F64X2_EXTRACT_LANE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val index: UInt = source.readIndex()
                    functionBodyVisitor.visitSimdExtractLaneInstruction(opcode, index)
                }

                I8X16_REPLACE_LANE, I16X8_REPLACE_LANE, I32X4_REPLACE_LANE, I64X2_REPLACE_LANE, F32X4_REPLACE_LANE, F64X2_REPLACE_LANE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val index: UInt = source.readIndex()
                    functionBodyVisitor.visitSimdReplaceLaneInstruction(opcode, index)
                }

                V8X16_SHUFFLE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val lanesIndex: UIntArray = UIntArray(16) { 0u }

                    for (i in 0u until 16u) {
                        lanesIndex[i.toInt()] = source.readVarUInt32()
                    }

                    functionBodyVisitor.visitSimdShuffleInstruction(opcode, V128Value(lanesIndex))
                }

                I8X16_ADD, I16X8_ADD, I32X4_ADD, I64X2_ADD -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAddInstruction(opcode)
                }

                I8X16_SUB, I16X8_SUB, I32X4_SUB, I64X2_SUB -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSubtractInstruction(opcode)
                }

                I8X16_MUL, I16X8_MUL, I32X4_MUL -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMultiplyInstruction(opcode)
                }

                I8X16_NEG, I16X8_NEG, I32X4_NEG, I64X2_NEG -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNegativeInstruction(opcode)
                }

                I8X16_ADD_SATURATE_S, I8X16_ADD_SATURATE_U, I16X8_ADD_SATURATE_S, I16X8_ADD_SATURATE_U -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAddSaturateInstruction(opcode)
                }

                I8X16_SUB_SATURATE_S, I8X16_SUB_SATURATE_U, I16X8_SUB_SATURATE_S, I16X8_SUB_SATURATE_U -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSubtractSaturateInstruction(opcode)
                }

                I8X16_SHL, I16X8_SHL, I32X4_SHL, I64X2_SHL, I8X16_SHL_S, I8X16_SHL_U, I16X8_SHL_S, I16X8_SHL_U, I32X4_SHL_S, I32X4_SHL_U, I64X2_SHL_S, I64X2_SHL_U -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdShiftLeftInstruction(opcode)
                }

                V128_AND -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAndInstruction(opcode)
                }

                V128_OR -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdOrInstruction(opcode)
                }

                V128_XOR -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdXorInstruction(opcode)
                }

                V128_NOT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNotInstruction(opcode)
                }

                V128_BITSELECT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdBitSelectInstruction(opcode)
                }

                I8X16_ANY_TRUE, I16X8_ANY_TRUE, I32X4_ANY_TRUE, I64X2_ANY_TRUE, I8X16_ALL_TRUE, I16X8_ALL_TRUE, I32X4_ALL_TRUE, I64X2_ALL_TRUE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAllTrueInstruction(opcode)
                }

                I8X16_EQ, I16X8_EQ, I32X4_EQ, F32X4_EQ, F64X2_EQ -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdEqualInstruction(opcode)
                }

                I8X16_NE, I16X8_NE, I32X4_NE, F32X4_NE, F64X2_NE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNotEqualInstruction(opcode)
                }

                I8X16_LT_S, I8X16_LT_U, I16X8_LT_S, I16X8_LT_U, I32X4_LT_S, I32X4_LT_U, F32X4_LT, F64X2_LT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdLessThanInstruction(opcode)
                }

                I8X16_LE_S, I8X16_LE_U, I16X8_LE_S, I16X8_LE_U, I32X4_LE_S, I32X4_LE_U, F32X4_LE, F64X2_LE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdLessEqualInstruction(opcode)
                }

                I8X16_GT_S, I8X16_GT_U, I16X8_GT_S, I16X8_GT_U, I32X4_GT_S, I32X4_GT_U, F32X4_GT, F64X2_GT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdGreaterThanInstruction(opcode)
                }

                I8X16_GE_S, I8X16_GE_U, I16X8_GE_S, I16X8_GE_U, I32X4_GE_S, I32X4_GE_U, F32X4_GE, F64X2_GE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdGreaterEqualInstruction(opcode)
                }

                F32X4_NEG, F64X2_NEG -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNegativeInstruction(opcode)
                }

                F32X4_ABS, F64X2_ABS -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAbsInstruction(opcode)
                }

                F32X4_MIN, F64X2_MIN -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMinInstruction(opcode)
                }

                F32X4_MAX, F64X2_MAX -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMaxInstruction(opcode)
                }

                F32X4_ADD, F64X2_ADD -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAddInstruction(opcode)
                }

                F32X4_SUB, F64X2_SUB -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSubtractInstruction(opcode)
                }

                F32X4_DIV, F64X2_DIV -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdDivideInstruction(opcode)
                }

                F32X4_MUL, F64X2_MUL -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMultiplyInstruction(opcode)
                }

                F32X4_SQRT, F64X2_SQRT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSqrtInstruction(opcode)
                }

                F32X4_CONVERT_S_I32X4, F32X4_CONVERT_U_I32X4, F64X2_CONVERT_S_I64X2, F64X2_CONVERT_U_I64X2 -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdConvertInstruction(opcode)
                }

                I32X4_TRUNC_S_F32X4_SAT, I32X4_TRUNC_U_F32X4_SAT, I64X2_TRUNC_S_F64X2_SAT, I64X2_TRUNC_U_F64X2_SAT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdTruncateInstruction(opcode)
                }

                else -> throw ParserException("Unexpected opcode: %$opcode(0x${opcode.opcode.toHexString()})")
            }
        }

        if (numberOfInstructions > WasmBinary.MAX_FUNCTION_INSTRUCTIONS) {
            throw ParserException("Number of function locals $numberOfInstructions exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS}")
        }

        if (!seenEndOpcode) {
            throw ParserException("Function Body must end with END opcode")
        }
    }

    protected fun readGlobalSection(visitor: ModuleVisitor) {
        this.numberGlobals = source.readVarUInt32()

        if (this.numberGlobals > WasmBinary.MAX_GLOBALS) {
            throw ParserException("Number of globals $numberGlobals exceed the maximum of ${WasmBinary.MAX_GLOBALS}")
        }

        val globalVisitor: GlobalSectionVisitor = visitor.visitGlobalSection()
        for (index in 0u until numberGlobals) {
            val globalIndex = numberGlobalImports + index

            val contentType: WasmType = source.readType()
            if (!contentType.isValueType()) {
                throw ParserException("Invalid global type: %#$contentType")
            }

            val mutable = source.readVarUInt1() == 1u

            val globalVariableVisitor = globalVisitor.visitGlobalVariable(globalIndex)
            globalVariableVisitor.visitGlobalVariable(contentType, mutable)

            val initializerExpressionVisitor = globalVariableVisitor.visitInitializerExpression()
            readInitExpression(initializerExpressionVisitor, false)
            initializerExpressionVisitor.visitEnd()

            globalVariableVisitor.visitEnd()
        }

        globalVisitor.visitEnd()
    }

    protected fun readElementSection(visitor: ModuleVisitor) {
        this.numberElementSegments = source.readVarUInt32()

        if (numberElementSegments > WasmBinary.MAX_ELEMENT_SEGMENTS) {
            throw ParserException("Number of element segments " + numberElementSegments + " exceed the maximum of " + WasmBinary.MAX_ELEMENT_SEGMENTS)
        }

        if (numberElementSegments != 0u && getNumberTotalTables() <= 0u) {
            throw ParserException("Element section without table section.")
        }

        val elementVisitor: ElementSectionVisitor = visitor.visitElementSection()
        for (index in 0u until numberElementSegments) {
            val startIndex: UInt = source.position
            val elementSegmentVisitor = elementVisitor.visitElementSegment(index)

            val tableIndex: UInt = source.readIndex()
            if (tableIndex != 0u) {
                throw ParserException("Table elements must refer to table 0.")
            }
            elementSegmentVisitor.visitTableIndex(tableIndex)

            val initializerExpressionVisitor = elementSegmentVisitor.visitInitializerExpression()
            readInitExpression(initializerExpressionVisitor, true)
            initializerExpressionVisitor.visitEnd()

            val numberFunctionIndexes: UInt = source.readVarUInt32()

            if (numberFunctionIndexes + (source.position - startIndex) > WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH) {
                throw ParserException(("Element segment size of ${numberFunctionIndexes + (source.position - startIndex)}") + " exceed the maximum of " + WasmBinary.MAX_ELEMENT_SEGMENT_LENGTH)
            }

            for (segmentFunctionIndex in 0u until numberFunctionIndexes) {
                val functionIndex: UInt = source.readIndex()

                elementSegmentVisitor.visitFunctionIndex(segmentFunctionIndex, functionIndex)
            }

            elementSegmentVisitor.visitEnd()
        }

        elementVisitor.visitEnd()
    }

    protected fun readInitExpression(visitor: InitializerExpressionVisitor, requireUInt: Boolean) {
        var opcode = source.readOpcode()
        when (opcode) {
            I32_CONST -> {
                val value: Int = source.readVarInt32()

                visitor.visitInitExprI32ConstExpr(value)
            }

            I64_CONST -> {
                val value: Long = source.readVarInt64()

                visitor.visitInitExprI64ConstExpr(value)
            }

            F32_CONST -> {
                val value: Float = source.readFloat32()

                visitor.visitInitExprF32ConstExpr(value)
            }

            F64_CONST -> {
                val value: Double = source.readFloat64()

                visitor.visitInitExprF64ConstExpr(value)
            }

            V128_CONST -> {
                if (!options.features.isSIMDEnabled) {
                    throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                }

                val value: V128Value = source.readV128()

                visitor.visitInitExprV128ConstExpr(value)
            }

            GET_GLOBAL -> {
                val globaIndex: UInt = source.readIndex()

                if (globaIndex > getNumberTotalGlobals()) {
                    throw ParserException("get_global index of $globaIndex exceed the maximum of ${getNumberTotalGlobals()}")
                }

                if (globaIndex > numberGlobalImports) {
                    throw ParserException("get_global index of $globaIndex exceed the number of globals of $numberGlobalImports")
                }

                visitor.visitInitExprGetGlobalExpr(globaIndex)
            }

            END -> {
                visitor.visitInitExprEnd()
                return
            }

            else -> throw ParserException("Unexpected opcode in initializer expression: %$opcode(0x${opcode.opcode.toHexString()})")
        }
        if (requireUInt && (opcode != I32_CONST) && (opcode != GET_GLOBAL)) {
            throw ParserException("Expected i32 init_expr")
        }

        opcode = source.readOpcode()
        if (opcode == END) {
            visitor.visitInitExprEnd()
        } else {
            throw ParserException("Expected END opcode after initializer expression: %$opcode")
        }
    }

    protected fun readTableSection(visitor: ModuleVisitor) {
        this.numberTables = source.readVarUInt32()

        if (numberTables > WasmBinary.MAX_TABLES) {
            throw ParserException("Number of tables $numberTables exceed the maximum of ${WasmBinary.MAX_TABLES}")
        }

        val tableVisitor: TableSectionVisitor = visitor.visitTableSection()
        for (index in 0u until numberTables) {
            val tableIndex = numberTableImports + index

            val elementType: WasmType = source.readType()
            if (elementType != WasmType.ANYFUNC) {
                throw ParserException("Table type is not AnyFunc.")
            }

            val limits: ResizableLimits = source.readResizableLimits()
            if (limits.isShared()) {
                throw ParserException("Tables may not be shared.")
            }

            if (limits.initial > WasmBinary.MAX_TABLE_PAGES) {
                throw ParserException("Invalid initial memory pages")
            }

            if (limits.maximum != null) {
                if (limits.maximum > WasmBinary.MAX_TABLE_PAGES) {
                    throw ParserException("Invalid table max page")
                }

                if (limits.initial > limits.maximum) {
                    throw ParserException("Initial table page size greater than maximum")
                }
            }

            tableVisitor.visitTable(tableIndex, elementType, limits)
        }

        tableVisitor.visitEnd()
    }

    protected fun readExportSection(visitor: ModuleVisitor) {
        this.numberExports = source.readVarUInt32()

        if (this.numberExports > WasmBinary.MAX_EXPORTS) {
            throw ParserException("Number of exports $numberExports exceed the maximum of ${WasmBinary.MAX_EXPORTS}")
        }

        val exportVisitor: ExportSectionVisitor = visitor.visitExportSection()
        val names = mutableSetOf<String>()
        for (exportIndex in 0u until numberExports) {
            val name: String = source.readString()

            if (!names.add(name)) {
                throw ParserException("Duplicate export name $name")
            }

            val externalKind: ExternalKind = source.readExternalKind()
            val itemIndex: UInt = source.readIndex()

            when (externalKind) {
                ExternalKind.FUNCTION -> {
                    if (itemIndex >= getNumberTotalFunctions()) {
                        throw ParserException("Invalid export function index: %$itemIndex")
                    }
                }

                ExternalKind.TABLE -> {
                    if (getNumberTotalTables() == 0u) {
                        throw ParserException("Cannot index non existing table")
                    }

                    if (itemIndex != 0u) {
                        throw ParserException("Only table index 0 is supported, but using index $itemIndex")
                    }

                    if (itemIndex > getNumberTotalTables()) {
                        throw ParserException("Invalid export table index: %$itemIndex")
                    }
                }

                ExternalKind.MEMORY -> {
                    if (getNumberTotalMemories() == 0u) {
                        throw ParserException("Cannot index non existing memory")
                    }

                    if (itemIndex != 0u) {
                        throw ParserException("Only memory index 0 is supported, but using index $itemIndex")
                    }

                    if (itemIndex > getNumberTotalMemories()) {
                        throw ParserException("Invalid export memories index: %$itemIndex")
                    }
                }

                ExternalKind.GLOBAL -> {
                    if (itemIndex >= getNumberTotalGlobals()) {
                        throw ParserException("Invalid export global index: %$itemIndex")
                    }
                }

                ExternalKind.EXCEPTION -> {
                    run {
                        if (!options.features.isExceptionHandlingEnabled) {
                            throw ParserException("Invalid export exception kind: exceptions not enabled.")
                        }
                    }
                    throw IllegalArgumentException()
                }

                else -> throw IllegalArgumentException()
            }
            exportVisitor.visitExport(exportIndex, externalKind, itemIndex, name)
        }

        exportVisitor.visitEnd()
    }

    public fun readV128(): V128Value {
        val value: UIntArray = uintArrayOf()

        for (i in 0..3) {
            value[i] = source.readUInt32()
        }

        return V128Value(value)
    }

    protected fun readStartSection(visitor: ModuleVisitor) {
        val functionIndex: UInt = source.readIndex()

        if (functionIndex >= getNumberTotalFunctions()) {
            throw ParserException("Invalid start function index: %$functionIndex")
        }

        val startSection: StartSectionVisitor = visitor.visitStartSection()
        startSection.visitStartFunctionIndex(functionIndex)
        startSection.visitEnd()
    }

    protected fun readFunctionSection(visitor: ModuleVisitor) {
        this.numberFunctions = source.readVarUInt32()

        if (this.numberFunctions > WasmBinary.MAX_FUNCTIONS) {
            throw ParserException("Number of functions $numberFunctions exceed the maximum of ${WasmBinary.MAX_FUNCTIONS}")
        }

        val functionVisitor: FunctionSectionVisitor = visitor.visitFunctionSection()
        for (index in 0u until numberFunctions) {
            val functionIndex = numberFunctionImports + index

            val signatureIndex: UInt = source.readIndex()
            if (signatureIndex >= numberSignatures) {
                throw ParserException("Invalid function signature index: %$signatureIndex")
            }

            functionVisitor.visitFunction(functionIndex, signatureIndex)
        }

        functionVisitor.visitEnd()
    }

    protected fun readTypeSection(visitor: ModuleVisitor) {
        this.numberSignatures = source.readVarUInt32()

        if (this.numberSignatures > WasmBinary.MAX_TYPES) {
            throw ParserException("Number of types $numberSignatures exceed the maximum of ${WasmBinary.MAX_TYPES}")
        }

        val typeVisitor: TypeSectionVisitor = visitor.visitTypeSection()
        for (typeIndex in 0u until numberSignatures) {
            val form: WasmType = source.readType()

            if (form != WasmType.FUNCTION) {
                throw ParserException("Invalid signature form with type: $form")
            }

            val parameterCount: UInt = source.readVarUInt32()
            if (parameterCount > WasmBinary.MAX_FUNCTION_PARAMS) {
                throw ParserException("Number of function parameters $parameterCount exceed the maximum of ${WasmBinary.MAX_FUNCTION_PARAMS}")
            }

            val parameters = Array(parameterCount.toInt()) { WasmType.NONE }
            for (paramIndex in 0u until parameterCount) {
                val type: WasmType = source.readType()

                if (!type.isValueType()) {
                    throw ParserException("Expected valid param type but got: ${type.name}")
                }

                parameters[paramIndex.toInt()] = type
            }

            val resultCount: UInt = source.readVarUInt1()

            if (resultCount > WasmBinary.MAX_FUNCTION_RESULTS) {
                throw ParserException("Number of function results $resultCount exceed the maximum of ${WasmBinary.MAX_FUNCTION_RESULTS}")
            }

            if (resultCount != 0u && resultCount != 1u) {
                throw ParserException("Result size must be 0 or 1 but got: $resultCount")
            }

            var resultType: Array<WasmType>
            if (resultCount == 0u) {
                resultType = arrayOf()
            } else {
                if (!options.features.isMultiValueEnabled && (resultCount > 1u)) {
                    throw ParserException("Function with multi-value result not allowed.")
                }

                resultType = Array(resultCount.toInt()) { WasmType.NONE }
                for (rtype in 0 until resultCount.toInt()) {
                    val type: WasmType = source.readType()

                    if (!type.isValueType()) {
                        throw ParserException("Expected valid param value type but got: ${type.name}")
                    }

                    resultType[rtype] = type
                }
            }

            typeVisitor.visitType(typeIndex, parameters, resultType)
        }

        typeVisitor.visitEnd()
    }

    protected fun readImportSection(visitor: ModuleVisitor) {
        this.numberImports = source.readVarUInt32()

        if (this.numberImports > WasmBinary.MAX_IMPORTS) {
            throw ParserException("Number of imports $numberImports exceed the maximum of ${WasmBinary.MAX_IMPORTS}")
        }

        val importVisitor: ImportSectionVisitor = visitor.visitImportSection()
        for (importIndex in 0u until numberImports) {
            val moduleName: String = source.readString()
            val fieldName: String = source.readString()

            val externalKind: ExternalKind = source.readExternalKind()
            when (externalKind) {
                ExternalKind.FUNCTION -> {
                    val typeIndex: UInt = source.readIndex()

                    if (typeIndex >= this.numberSignatures) {
                        throw ParserException("Invalid import function index $typeIndex")
                    }

                    importVisitor.visitFunction(importIndex, moduleName, fieldName, numberFunctionImports, typeIndex)

                    numberFunctionImports++
                }

                ExternalKind.TABLE -> {
                    val elementType: WasmType = source.readType()

                    if (!elementType.isElementType()) {
                        throw ParserException("Imported table type is not AnyFunc.")
                    }

                    val limits: ResizableLimits = source.readResizableLimits()
                    if (limits.isShared()) {
                        throw ParserException("Tables may not be shared")
                    }

                    importVisitor.visitTable(importIndex, moduleName, fieldName, numberTableImports, elementType, limits)

                    numberTableImports++
                }

                ExternalKind.MEMORY -> {
                    val pageLimits: ResizableLimits = source.readResizableLimits()
                    importVisitor.visitMemory(importIndex, moduleName, fieldName, numberMemoryImports, pageLimits)

                    numberMemoryImports++
                }

                ExternalKind.GLOBAL -> {
                    val globalType: WasmType = source.readType()

                    if (!globalType.isValueType()) {
                        throw ParserException("Invalid global type: %#$globalType")
                    }

                    val mutable = source.readVarUInt1() == 1u
                    if (mutable) {
                        throw ParserException("Import mutate globals are not allowed")
                    }

                    importVisitor.visitGlobal(importIndex, moduleName, fieldName, numberGlobalImports, globalType, false)

                    numberGlobalImports++
                }

                ExternalKind.EXCEPTION -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid import exception kind: exceptions not enabled.")
                    }

                    val signatures = readExceptionType()
                    importVisitor.visitException(importIndex, moduleName, fieldName, numberExceptionImports, signatures)

                    numberExceptionImports++
                }

                else -> throw ParserException("Not supported external kind:$externalKind")
            }
        }

        importVisitor.visitEnd()
    }

    protected fun getNumberTotalFunctions(): UInt {
        return numberFunctionImports + numberFunctions
    }

    protected fun getNumberTotalTables(): UInt {
        return numberTableImports + numberTables
    }

    protected fun getNumberTotalMemories(): UInt {
        return numberMemoryImports + numberMemories
    }

    protected fun getNumberTotalGlobals(): UInt {
        return numberGlobalImports + numberGlobals
    }

    protected fun getNumberTotalExceptions(): UInt {
        return numberExceptionImports + numberExceptions
    }
}
