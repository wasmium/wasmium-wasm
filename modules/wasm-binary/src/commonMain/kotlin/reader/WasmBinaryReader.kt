@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.toHexString
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.LinkingKind
import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.tree.NameKind
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.WasmVersion
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.CustomSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor
import org.wasmium.wasm.binary.visitors.ExportSectionVisitor
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor
import org.wasmium.wasm.binary.visitors.NameSectionVisitor
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor
import org.wasmium.wasm.binary.visitors.StartSectionVisitor
import org.wasmium.wasm.binary.visitors.TableSectionVisitor
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

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

        var previousSection: SectionKind? = null

        while (!source.exhausted()) {
            numberOfSections++

            if (numberOfSections > WasmBinary.MAX_SECTIONS) {
                throw ParserException("Sections size of $numberOfSections exceed the maximum of ${WasmBinary.MAX_SECTIONS}")
            }

            section = source.readSectionKind()
            if (section != SectionKind.CUSTOM) {
                // not consider CUSTOM section for ordering
                if (previousSection != null) {
                    if (section.ordinal < previousSection.ordinal) {
                        throw ParserException("Invalid section order of ${previousSection.name} followed by ${section.name}")
                    }
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
                SectionKind.CUSTOM -> readCustomSection(visitor, payloadSize)
                SectionKind.TYPE -> readTypeSection(visitor)
                SectionKind.IMPORT -> readImportSection(visitor)
                SectionKind.FUNCTION -> readFunctionSection(visitor)
                SectionKind.TABLE -> readTableSection(visitor)
                SectionKind.MEMORY -> readMemorySection(visitor)
                SectionKind.GLOBAL -> readGlobalSection(visitor)
                SectionKind.EXPORT -> readExportSection(visitor)
                SectionKind.START -> readStartSection(visitor)
                SectionKind.ELEMENT -> readElementSection(visitor)
                SectionKind.CODE -> {
                    if (options.isSkipCodeSection) {
                        source.skip(payloadSize)
                    } else {
                        readCodeSection(visitor)
                    }
                }
                SectionKind.DATA_COUNT -> readDataCountSection(visitor)

                SectionKind.DATA -> readDataSection(visitor)
            }

            if (payloadSize != source.position - startPosition) {
                throw ParserException("Invalid size of section id: $section, expected: $payloadSize, actual: ${source.position - startPosition}")
            }

            if (seenNameSection && section != SectionKind.CUSTOM) {
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

        var previousNameKind: NameKind? = null

        while (source.position < startIndex + sectionPayloadSize) {
            val nameKind: NameKind = source.readNameKind()

            if (previousNameKind != null) {
                if (nameKind.nameKindId < previousNameKind.nameKindId) {
                    throw ParserException("Name kind sub subsection out of order")
                }
            }

            val subsectionSize: UInt = source.readVarUInt32()
            if (!source.require(subsectionSize)) {
                throw ParserException("Name subsection greater then input")
            }

            val startIndex = source.position

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

                        val numberLocals = source.readVarUInt32()
                        var previousLocalIndex: UInt? = null
                        var local = 0u
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
            }

            if (subsectionSize != source.position - startIndex) {
                throw ParserException("Invalid size of subsection id: $nameKind")
            }

            previousNameKind = nameKind
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
                        val symbolType = source.readLinkingSymbolType()
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
                                var segment = 0u
                                var offset = 0u
                                var size = 0u

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
        // TODO
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
        if (sectionKind == SectionKind.CUSTOM) {
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
                        throw ParserException("Invalid size of section id: ${SectionKind.DATA}")
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
                        throw ParserException("Invalid size of section id: ${SectionKind.DATA}")
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

            var totalLocals = 0u
            val localsSize = source.readVarUInt32()

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
                Opcode.UNREACHABLE -> {
                    functionBodyVisitor.visitUnreachableInstruction()
                }

                Opcode.NOP -> {
                    functionBodyVisitor.visitNopInstruction()
                }

                Opcode.BLOCK -> {
                    val type: WasmType = source.readType()

                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitBlockInstruction(blockType)
                }

                Opcode.LOOP -> {
                    val type: WasmType = source.readType()

                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitLoopInstruction(blockType)
                }

                Opcode.IF -> {
                    val type: WasmType = source.readType()

                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitIfInstruction(blockType)
                }

                Opcode.ELSE -> {
                    functionBodyVisitor.visitElseInstruction()
                }

                Opcode.TRY -> {
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

                Opcode.CATCH -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch code: exceptions not enabled.")
                    }

                    functionBodyVisitor.visitCatchInstruction()
                }

                Opcode.THROW -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid throw code: exceptions not enabled.")
                    }

                    val exceptionIndex: UInt = source.readIndex()
                    if (exceptionIndex >= getNumberTotalExceptions()) {
                        throw ParserException("invalid call exception index: %$exceptionIndex")
                    }

                    functionBodyVisitor.visitThrowInstruction(exceptionIndex)
                }

                Opcode.RETHROW -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid rethrow code: exceptions not enabled.")
                    }

                    functionBodyVisitor.visitRethrowInstruction()
                }

                Opcode.THROW_REF -> {
                    if (!options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch code: exceptions not enabled.")
                    }

                    functionBodyVisitor.visitThrowRefInstruction()
                }

                Opcode.END -> {
                    if (endBodyPosition == source.position) {
                        seenEndOpcode = true
                        functionBodyVisitor.visitEndFunctionInstruction()
                    } else {
                        functionBodyVisitor.visitEndInstruction()
                    }
                }

                Opcode.BR -> {
                    val depth: UInt = source.readIndex()

                    functionBodyVisitor.visitBrInstruction(depth)
                }

                Opcode.BR_IF -> {
                    val depth: UInt = source.readIndex()

                    functionBodyVisitor.visitBrIfInstruction(depth)
                }

                Opcode.BR_TABLE -> {
                    val numberTargets: UInt = source.readVarUInt32()
                    val targets: Array<UInt> = Array(numberTargets.toInt()) { 0u }

                    for (targetIndex in 0u until numberTargets) {
                        val depth: UInt = source.readIndex()

                        targets[targetIndex.toInt()] = depth
                    }

                    val defaultTarget: UInt = source.readIndex()

                    functionBodyVisitor.visitBrTableInstruction(targets, defaultTarget)
                }

                Opcode.RETURN -> {
                    functionBodyVisitor.visitReturnInstruction()
                }

                Opcode.CALL -> {
                    val funcIndex: UInt = source.readIndex()

                    if (funcIndex >= getNumberTotalFunctions()) {
                        throw ParserException("Invalid call function index: %$funcIndex")
                    }

                    functionBodyVisitor.visitCallInstruction(funcIndex)
                }

                Opcode.CALL_INDIRECT -> {
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

                Opcode.DROP -> {
                    functionBodyVisitor.visitDropInstruction()
                }

                Opcode.SELECT -> {
                    functionBodyVisitor.visitSelectInstruction()
                }

                Opcode.GET_GLOBAL -> {
                    val globalIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitGetGlobalInstruction(globalIndex)
                }

                Opcode.SET_LOCAL -> {
                    val localIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitSetLocalInstruction(localIndex)
                }

                Opcode.TEE_LOCAL -> {
                    val localIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitTeeLocalInstruction(localIndex)
                }

                Opcode.GET_LOCAL -> {
                    val localIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitGetLocalInstruction(localIndex)
                }

                Opcode.SET_GLOBAL -> {
                    val globalIndex: UInt = source.readIndex()

                    functionBodyVisitor.visitSetGlobalInstruction(globalIndex)
                }

                Opcode.I32_LOAD,
                Opcode.I64_LOAD,
                Opcode.F32_LOAD,
                Opcode.F64_LOAD,
                Opcode.I32_LOAD8_S,
                Opcode.I32_LOAD8_U,
                Opcode.I32_LOAD16_S,
                Opcode.I32_LOAD16_U,
                Opcode.I64_LOAD8_S,
                Opcode.I64_LOAD8_U,
                Opcode.I64_LOAD16_S,
                Opcode.I64_LOAD16_U,
                Opcode.I64_LOAD32_S,
                Opcode.I64_LOAD32_U -> {
                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitLoadInstruction(opcode, alignment, offset)
                }

                Opcode.I32_STORE8,
                Opcode.I32_STORE16,
                Opcode.I64_STORE8,
                Opcode.I64_STORE16,
                Opcode.I64_STORE32,
                Opcode.I32_STORE,
                Opcode.I64_STORE,
                Opcode.F32_STORE,
                Opcode.F64_STORE -> {
                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitStoreInstruction(opcode, alignment, offset)
                }

                Opcode.MEMORY_SIZE -> {
                    val reserved: UInt = source.readVarUInt1()
                    if (reserved != 0u) {
                        throw ParserException("MemorySize reserved value must be 0.")
                    }
                    functionBodyVisitor.visitMemorySizeInstruction(reserved = false)
                }

                Opcode.MEMORY_GROW -> {
                    val reserved: UInt = source.readVarUInt1()
                    if (reserved != 0u) {
                        throw ParserException("MemoryGrow reserved value must be 0")
                    }

                    functionBodyVisitor.visitMemoryGrowInstruction(reserved = false)
                }

                Opcode.I32_CONST -> {
                    val value: Int = source.readVarInt32()

                    functionBodyVisitor.visitConstInt32Instruction(value)
                }

                Opcode.I64_CONST -> {
                    val value: Long = source.readVarInt64()

                    functionBodyVisitor.visitConstInt64Instruction(value)
                }

                Opcode.F32_CONST -> {
                    val value: Float = source.readFloat32()

                    functionBodyVisitor.visitConstFloat32Instruction(value)
                }

                Opcode.F64_CONST -> {
                    val value: Double = source.readFloat64()

                    functionBodyVisitor.visitConstFloat64Instruction(value)
                }

                Opcode.I32_EQZ -> {
                    functionBodyVisitor.visitEqualZeroInstruction(opcode)
                }

                Opcode.I32_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                Opcode.I32_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                Opcode.I32_LT_S -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                Opcode.I32_LE_S -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                Opcode.I32_LT_U -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                Opcode.I32_LE_U -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                Opcode.I32_GT_S -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                Opcode.I32_GE_S -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                Opcode.I32_GT_U -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                Opcode.I32_GE_U -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                Opcode.I64_EQZ -> {
                    functionBodyVisitor.visitEqualZeroInstruction(opcode)
                }

                Opcode.I64_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                Opcode.I64_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                Opcode.I64_LT_S -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                Opcode.I64_LE_S -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                Opcode.I64_LT_U -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                Opcode.I64_LE_U -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                Opcode.I64_GT_S -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                Opcode.I64_GE_S -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                Opcode.I64_GT_U -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                Opcode.I64_GE_U -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                Opcode.F32_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                Opcode.F32_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                Opcode.F32_LT -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                Opcode.F32_LE -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                Opcode.F32_GT -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                Opcode.F32_GE -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                Opcode.F64_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                Opcode.F64_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                Opcode.F64_LT -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                Opcode.F64_LE -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                Opcode.F64_GT -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                Opcode.F64_GE -> {
                    functionBodyVisitor.visitCompareInstruction(opcode)
                }

                Opcode.I32_CLZ -> {
                    functionBodyVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                Opcode.I32_CTZ -> {
                    functionBodyVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                Opcode.I32_POPCNT -> {
                    functionBodyVisitor.visitPopulationCountInstruction(opcode)
                }

                Opcode.I32_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                Opcode.I32_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                Opcode.I32_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                Opcode.I32_DIV_S -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                Opcode.I32_DIV_U -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                Opcode.I32_REM_S -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                Opcode.I32_REM_U -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                Opcode.I32_AND -> {
                    functionBodyVisitor.visitAndInstruction(opcode)
                }

                Opcode.I32_OR -> {
                    functionBodyVisitor.visitOrInstruction(opcode)
                }

                Opcode.I32_XOR -> {
                    functionBodyVisitor.visitXorInstruction(opcode)
                }

                Opcode.I32_SHL -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                Opcode.I32_SHR_U -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                Opcode.I32_SHR_S -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                Opcode.I32_ROTL -> {
                    functionBodyVisitor.visitRotateLeftInstruction(opcode)
                }

                Opcode.I32_ROTR -> {
                    functionBodyVisitor.visitRotateRightInstruction(opcode)
                }

                Opcode.I64_CLZ -> {
                    functionBodyVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                Opcode.I64_CTZ -> {
                    functionBodyVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                Opcode.I64_POPCNT -> {
                    functionBodyVisitor.visitPopulationCountInstruction(opcode)
                }

                Opcode.I64_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                Opcode.I64_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                Opcode.I64_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                Opcode.I64_DIV_S -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                Opcode.I64_DIV_U -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                Opcode.I64_REM_S -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                Opcode.I64_REM_U -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                Opcode.I64_AND -> {
                    functionBodyVisitor.visitAndInstruction(opcode)
                }

                Opcode.I64_OR -> {
                    functionBodyVisitor.visitOrInstruction(opcode)
                }

                Opcode.I64_XOR -> {
                    functionBodyVisitor.visitXorInstruction(opcode)
                }

                Opcode.I64_SHL -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                Opcode.I64_SHR_U -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                Opcode.I64_SHR_S -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                Opcode.I64_ROTL -> {
                    functionBodyVisitor.visitRotateLeftInstruction(opcode)
                }

                Opcode.I64_ROTR -> {
                    functionBodyVisitor.visitRotateRightInstruction(opcode)
                }

                Opcode.F32_ABS -> {
                    functionBodyVisitor.visitAbsoluteInstruction(opcode)
                }

                Opcode.F32_NEG -> {
                    functionBodyVisitor.visitNegativeInstruction(opcode)
                }

                Opcode.F32_CEIL -> {
                    functionBodyVisitor.visitCeilingInstruction(opcode)
                }

                Opcode.F32_FLOOR -> {
                    functionBodyVisitor.visitFloorInstruction(opcode)
                }

                Opcode.F32_TRUNC -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                Opcode.F32_NEAREST -> {
                    functionBodyVisitor.visitNearestInstruction(opcode)
                }

                Opcode.F32_SQRT -> {
                    functionBodyVisitor.visitSqrtInstruction(opcode)
                }

                Opcode.F32_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                Opcode.F32_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                Opcode.F32_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                Opcode.F32_DIV -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                Opcode.F32_MIN -> {
                    functionBodyVisitor.visitMinInstruction(opcode)
                }

                Opcode.F32_MAX -> {
                    functionBodyVisitor.visitMaxInstruction(opcode)
                }

                Opcode.F32_COPYSIGN -> {
                    functionBodyVisitor.visitCopySignInstruction(opcode)
                }

                Opcode.F64_ABS -> {
                    functionBodyVisitor.visitAbsoluteInstruction(opcode)
                }

                Opcode.F64_NEG -> {
                    functionBodyVisitor.visitNegativeInstruction(opcode)
                }

                Opcode.F64_CEIL -> {
                    functionBodyVisitor.visitCeilingInstruction(opcode)
                }

                Opcode.F64_FLOOR -> {
                    functionBodyVisitor.visitFloorInstruction(opcode)
                }

                Opcode.F64_TRUNC -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                Opcode.F64_NEAREST -> {
                    functionBodyVisitor.visitNearestInstruction(opcode)
                }

                Opcode.F64_SQRT -> {
                    functionBodyVisitor.visitSqrtInstruction(opcode)
                }

                Opcode.F64_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                Opcode.F64_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                Opcode.F64_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                Opcode.F64_DIV -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                Opcode.F64_MIN -> {
                    functionBodyVisitor.visitMinInstruction(opcode)
                }

                Opcode.F64_MAX -> {
                    functionBodyVisitor.visitMaxInstruction(opcode)
                }

                Opcode.F64_COPYSIGN -> {
                    functionBodyVisitor.visitCopySignInstruction(opcode)
                }

                Opcode.I32_WRAP_I64 -> {
                    functionBodyVisitor.visitWrapInstruction(opcode)
                }

                Opcode.I32_TRUNC_SF32, Opcode.I32_TRUNC_UF32, Opcode.I32_TRUNC_SF64, Opcode.I32_TRUNC_UF64 -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                Opcode.I64_EXTEND_SI32, Opcode.I64_EXTEND_UI32 -> {
                    functionBodyVisitor.visitExtendInstruction(opcode)
                }

                Opcode.I64_TRUNC_SF32, Opcode.I64_TRUNC_UF32, Opcode.I64_TRUNC_SF64, Opcode.I64_TRUNC_UF64 -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                Opcode.F32_CONVERT_SI32, Opcode.F32_CONVERT_UI32, Opcode.F32_CONVERT_SI64, Opcode.F32_CONVERT_UI64 -> {
                    functionBodyVisitor.visitConvertInstruction(opcode)
                }

                Opcode.F32_DEMOTE_F64 -> {
                    functionBodyVisitor.visitDemoteInstruction(opcode)
                }

                Opcode.F64_CONVERT_SI32, Opcode.F64_CONVERT_UI32, Opcode.F64_CONVERT_SI64, Opcode.F64_CONVERT_UI64 -> {
                    functionBodyVisitor.visitConvertInstruction(opcode)
                }

                Opcode.F64_PROMOTE_F32 -> {
                    functionBodyVisitor.visitPromoteInstruction(opcode)
                }

                Opcode.I32_REINTERPRET_F32, Opcode.I64_REINTERPRET_F64, Opcode.F32_REINTERPRET_I32, Opcode.F64_REINTERPRET_I64 -> {
                    functionBodyVisitor.visitReinterpretInstruction(opcode)
                }

                Opcode.I32_EXTEND8_S, Opcode.I32_EXTEND16_S, Opcode.I64_EXTEND8_S, Opcode.I64_EXTEND16_S, Opcode.I64_EXTEND32_S -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid opcode: ${opcode.code} not enabled.")
                    }

                    functionBodyVisitor.visitExtendInstruction(opcode)
                }

                Opcode.I32_TRUNC_S_SAT_F32, Opcode.I32_TRUNC_U_SAT_F32, Opcode.I32_TRUNC_S_SAT_F64, Opcode.I32_TRUNC_U_SAT_F64, Opcode.I64_TRUNC_S_SAT_F32, Opcode.I64_TRUNC_U_SAT_F32, Opcode.I64_TRUNC_S_SAT_F64, Opcode.I64_TRUNC_U_SAT_F64 -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                Opcode.MEMORY_ATOMIC_NOTIFY -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicWakeInstruction(opcode, alignment, offset)
                }

                Opcode.MEMORY_ATOMIC_WAIT32,
                Opcode.MEMORY_ATOMIC_WAIT64 -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicWaitInstruction(opcode, alignment, offset)
                }

                Opcode.I32_ATOMIC_LOAD, Opcode.I64_ATOMIC_LOAD, Opcode.I32_ATOMIC_LOAD8_U, Opcode.I32_ATOMIC_LOAD16_U, Opcode.I64_ATOMIC_LOAD8_U, Opcode.I64_ATOMIC_LOAD16_U, Opcode.I64_ATOMIC_LOAD32_U -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicLoadInstruction(opcode, alignment, offset)
                }

                Opcode.I32_ATOMIC_STORE, Opcode.I64_ATOMIC_STORE, Opcode.I32_ATOMIC_STORE8, Opcode.I32_ATOMIC_STORE16, Opcode.I64_ATOMIC_STORE8, Opcode.I64_ATOMIC_STORE16, Opcode.I64_ATOMIC_STORE32 -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicStoreInstruction(opcode, alignment, offset)
                }

                Opcode.I32_ATOMIC_RMW_ADD, Opcode.I64_ATOMIC_RMW_ADD, Opcode.I32_ATOMIC_RMW8_U_ADD, Opcode.I32_ATOMIC_RMW16_U_ADD, Opcode.I64_ATOMIC_RMW8_U_ADD, Opcode.I64_ATOMIC_RMW16_U_ADD, Opcode.I64_ATOMIC_RMW32_U_ADD -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwAddInstruction(opcode, alignment, offset)
                }

                Opcode.I32_ATOMIC_RMW_SUB, Opcode.I64_ATOMIC_RMW_SUB, Opcode.I32_ATOMIC_RMW8_U_SUB, Opcode.I32_ATOMIC_RMW16_U_SUB, Opcode.I64_ATOMIC_RMW8_U_SUB, Opcode.I64_ATOMIC_RMW16_U_SUB, Opcode.I64_ATOMIC_RMW32_U_SUB -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)
                }

                Opcode.I32_ATOMIC_RMW_AND, Opcode.I64_ATOMIC_RMW_AND, Opcode.I32_ATOMIC_RMW8_U_AND, Opcode.I32_ATOMIC_RMW16_U_AND, Opcode.I64_ATOMIC_RMW8_U_AND, Opcode.I64_ATOMIC_RMW16_U_AND, Opcode.I64_ATOMIC_RMW32_U_AND -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwAndInstruction(opcode, alignment, offset)
                }

                Opcode.I32_ATOMIC_RMW_OR, Opcode.I64_ATOMIC_RMW_OR, Opcode.I32_ATOMIC_RMW8_U_OR, Opcode.I32_ATOMIC_RMW16_U_OR, Opcode.I64_ATOMIC_RMW8_U_OR, Opcode.I64_ATOMIC_RMW16_U_OR, Opcode.I64_ATOMIC_RMW32_U_OR -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwOrInstruction(opcode, alignment, offset)
                }

                Opcode.I32_ATOMIC_RMW_XOR, Opcode.I64_ATOMIC_RMW_XOR, Opcode.I32_ATOMIC_RMW8_U_XOR, Opcode.I32_ATOMIC_RMW16_U_XOR, Opcode.I64_ATOMIC_RMW8_U_XOR, Opcode.I64_ATOMIC_RMW16_U_XOR, Opcode.I64_ATOMIC_RMW32_U_XOR -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwXorInstruction(opcode, alignment, offset)
                }

                Opcode.I32_ATOMIC_RMW_XCHG, Opcode.I64_ATOMIC_RMW_XCHG, Opcode.I32_ATOMIC_RMW8_U_XCHG, Opcode.I32_ATOMIC_RMW16_U_XCHG, Opcode.I64_ATOMIC_RMW8_U_XCHG, Opcode.I64_ATOMIC_RMW16_U_XCHG, Opcode.I64_ATOMIC_RMW32_U_XCHG -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)
                }

                Opcode.I32_ATOMIC_RMW_CMPXCHG, Opcode.I64_ATOMIC_RMW_CMPXCHG, Opcode.I32_ATOMIC_RMW8_U_CMPXCHG, Opcode.I32_ATOMIC_RMW16_U_CMPXCHG, Opcode.I64_ATOMIC_RMW8_U_CMPXCHG, Opcode.I64_ATOMIC_RMW16_U_CMPXCHG, Opcode.I64_ATOMIC_RMW32_U_CMPXCHG -> {
                    if (!options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
                }

                Opcode.V128_CONST -> {
                    if (!options.features.isSIMDEnabled) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val value: V128Value = source.readV128()

                    functionBodyVisitor.visitSimdConstInstruction(value)
                }

                Opcode.V128_LOAD -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitSimdLoadInstruction(opcode, alignment, offset)
                }

                Opcode.V128_STORE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val alignment: UInt = source.readVarUInt32()
                    val offset: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitSimdStoreInstruction(opcode, alignment, offset)
                }

                Opcode.I8X16_SPLAT, Opcode.I16X8_SPLAT, Opcode.I32X4_SPLAT, Opcode.I64X2_SPLAT, Opcode.F32X4_SPLAT, Opcode.F64X2_SPLAT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val value: UInt = source.readVarUInt32()

                    functionBodyVisitor.visitSimdSplatInstruction(opcode, value)
                }

                Opcode.I8X16_EXTRACT_LANE_S, Opcode.I8X16_EXTRACT_LANE_U, Opcode.I16X8_EXTRACT_LANE_S, Opcode.I16X8_EXTRACT_LANE_U, Opcode.I32X4_EXTRACT_LANE, Opcode.I64X2_EXTRACT_LANE, Opcode.F32X4_EXTRACT_LANE, Opcode.F64X2_EXTRACT_LANE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val index: UInt = source.readIndex()
                    functionBodyVisitor.visitSimdExtractLaneInstruction(opcode, index)
                }

                Opcode.I8X16_REPLACE_LANE, Opcode.I16X8_REPLACE_LANE, Opcode.I32X4_REPLACE_LANE, Opcode.I64X2_REPLACE_LANE, Opcode.F32X4_REPLACE_LANE, Opcode.F64X2_REPLACE_LANE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val index: UInt = source.readIndex()
                    functionBodyVisitor.visitSimdReplaceLaneInstruction(opcode, index)
                }

                Opcode.V8X16_SHUFFLE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val lanesIndex = UIntArray(16) { 0u }

                    for (i in 0u until 16u) {
                        lanesIndex[i.toInt()] = source.readVarUInt32()
                    }

                    functionBodyVisitor.visitSimdShuffleInstruction(opcode, V128Value(lanesIndex))
                }

                Opcode.I8X16_ADD, Opcode.I16X8_ADD, Opcode.I32X4_ADD, Opcode.I64X2_ADD -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAddInstruction(opcode)
                }

                Opcode.I8X16_SUB, Opcode.I16X8_SUB, Opcode.I32X4_SUB, Opcode.I64X2_SUB -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSubtractInstruction(opcode)
                }

                Opcode.I8X16_MUL, Opcode.I16X8_MUL, Opcode.I32X4_MUL -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMultiplyInstruction(opcode)
                }

                Opcode.I8X16_NEG, Opcode.I16X8_NEG, Opcode.I32X4_NEG, Opcode.I64X2_NEG -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNegativeInstruction(opcode)
                }

                Opcode.I8X16_ADD_SATURATE_S, Opcode.I8X16_ADD_SATURATE_U, Opcode.I16X8_ADD_SATURATE_S, Opcode.I16X8_ADD_SATURATE_U -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAddSaturateInstruction(opcode)
                }

                Opcode.I8X16_SUB_SATURATE_S, Opcode.I8X16_SUB_SATURATE_U, Opcode.I16X8_SUB_SATURATE_S, Opcode.I16X8_SUB_SATURATE_U -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSubtractSaturateInstruction(opcode)
                }

                Opcode.I8X16_SHL, Opcode.I16X8_SHL, Opcode.I32X4_SHL, Opcode.I64X2_SHL, Opcode.I8X16_SHL_S, Opcode.I8X16_SHL_U, Opcode.I16X8_SHL_S, Opcode.I16X8_SHL_U, Opcode.I32X4_SHL_S, Opcode.I32X4_SHL_U, Opcode.I64X2_SHL_S, Opcode.I64X2_SHL_U -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdShiftLeftInstruction(opcode)
                }

                Opcode.V128_AND -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAndInstruction(opcode)
                }

                Opcode.V128_OR -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdOrInstruction(opcode)
                }

                Opcode.V128_XOR -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdXorInstruction(opcode)
                }

                Opcode.V128_NOT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNotInstruction(opcode)
                }

                Opcode.V128_BITSELECT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdBitSelectInstruction(opcode)
                }

                Opcode.I8X16_ANY_TRUE, Opcode.I16X8_ANY_TRUE, Opcode.I32X4_ANY_TRUE, Opcode.I64X2_ANY_TRUE, Opcode.I8X16_ALL_TRUE, Opcode.I16X8_ALL_TRUE, Opcode.I32X4_ALL_TRUE, Opcode.I64X2_ALL_TRUE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAllTrueInstruction(opcode)
                }

                Opcode.I8X16_EQ, Opcode.I16X8_EQ, Opcode.I32X4_EQ, Opcode.F32X4_EQ, Opcode.F64X2_EQ -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdEqualInstruction(opcode)
                }

                Opcode.I8X16_NE, Opcode.I16X8_NE, Opcode.I32X4_NE, Opcode.F32X4_NE, Opcode.F64X2_NE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNotEqualInstruction(opcode)
                }

                Opcode.I8X16_LT_S, Opcode.I8X16_LT_U, Opcode.I16X8_LT_S, Opcode.I16X8_LT_U, Opcode.I32X4_LT_S, Opcode.I32X4_LT_U, Opcode.F32X4_LT, Opcode.F64X2_LT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdLessThanInstruction(opcode)
                }

                Opcode.I8X16_LE_S, Opcode.I8X16_LE_U, Opcode.I16X8_LE_S, Opcode.I16X8_LE_U, Opcode.I32X4_LE_S, Opcode.I32X4_LE_U, Opcode.F32X4_LE, Opcode.F64X2_LE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdLessEqualInstruction(opcode)
                }

                Opcode.I8X16_GT_S, Opcode.I8X16_GT_U, Opcode.I16X8_GT_S, Opcode.I16X8_GT_U, Opcode.I32X4_GT_S, Opcode.I32X4_GT_U, Opcode.F32X4_GT, Opcode.F64X2_GT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdGreaterThanInstruction(opcode)
                }

                Opcode.I8X16_GE_S, Opcode.I8X16_GE_U, Opcode.I16X8_GE_S, Opcode.I16X8_GE_U, Opcode.I32X4_GE_S, Opcode.I32X4_GE_U, Opcode.F32X4_GE, Opcode.F64X2_GE -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdGreaterEqualInstruction(opcode)
                }

                Opcode.F32X4_NEG, Opcode.F64X2_NEG -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNegativeInstruction(opcode)
                }

                Opcode.F32X4_ABS, Opcode.F64X2_ABS -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAbsInstruction(opcode)
                }

                Opcode.F32X4_MIN, Opcode.F64X2_MIN -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMinInstruction(opcode)
                }

                Opcode.F32X4_MAX, Opcode.F64X2_MAX -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMaxInstruction(opcode)
                }

                Opcode.F32X4_ADD, Opcode.F64X2_ADD -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAddInstruction(opcode)
                }

                Opcode.F32X4_SUB, Opcode.F64X2_SUB -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSubtractInstruction(opcode)
                }

                Opcode.F32X4_DIV, Opcode.F64X2_DIV -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdDivideInstruction(opcode)
                }

                Opcode.F32X4_MUL, Opcode.F64X2_MUL -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMultiplyInstruction(opcode)
                }

                Opcode.F32X4_SQRT, Opcode.F64X2_SQRT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSqrtInstruction(opcode)
                }

                Opcode.F32X4_CONVERT_S_I32X4, Opcode.F32X4_CONVERT_U_I32X4, Opcode.F64X2_CONVERT_S_I64X2, Opcode.F64X2_CONVERT_U_I64X2 -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdConvertInstruction(opcode)
                }

                Opcode.I32X4_TRUNC_S_F32X4_SAT, Opcode.I32X4_TRUNC_U_F32X4_SAT, Opcode.I64X2_TRUNC_S_F64X2_SAT, Opcode.I64X2_TRUNC_U_F64X2_SAT -> {
                    if (!opcode.isEnabled(options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdTruncateInstruction(opcode)
                }

                Opcode.MEMORY_FILL -> {
                    if (!options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid memory.fill code: bulk memory not enabled.")
                    }

                    val address = source.readVarUInt32()
                    val value = source.readVarUInt32()
                    val size = source.readVarUInt32()

                    functionBodyVisitor.visitMemoryFillInstruction(opcode, address, value, size)
                }

                Opcode.MEMORY_COPY -> {
                    if (!options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid memory.copy code: bulk memory not enabled.")
                    }

                    val target = source.readVarUInt32()
                    val address = source.readVarUInt32()
                    val size = source.readVarUInt32()

                    functionBodyVisitor.visitMemoryFillInstruction(opcode, target, address, size)
                }

                Opcode.MEMORY_INIT -> {
                    if (!options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid memory.copy code: bulk memory not enabled.")
                    }

                    val target = source.readVarUInt32()
                    val address = source.readVarUInt32()
                    val size = source.readVarUInt32()

                    val zero = source.readVarUInt32()

                    functionBodyVisitor.visitMemoryInitInstruction(opcode, target, address, size)
                }

                Opcode.DATA_DROP -> {
                    if (!options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid data.drop code: bulk memory not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
                }

                Opcode.CALL_REF -> {
                    if (!options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid call_ref code: reference types not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
                }

                Opcode.RETURN_CALL_REF -> {
                    if (!options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid return_call_ref code: reference types not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
                }

                Opcode.REF_AS_NON_NULL -> {
                    if (!options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid ref_as_non_null code: reference types not enabled.")
                    }

                    // TODO
                }

                Opcode.BR_ON_NULL -> {
                    if (!options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid br_on_null code: reference types not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
                }

                Opcode.BR_ON_NON_NULL -> {
                    if (!options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid br_on_non_null code: reference types not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
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
            Opcode.I32_CONST -> {
                val value: Int = source.readVarInt32()

                visitor.visitInitExprI32ConstExpr(value)
            }

            Opcode.I64_CONST -> {
                val value: Long = source.readVarInt64()

                visitor.visitInitExprI64ConstExpr(value)
            }

            Opcode.F32_CONST -> {
                val value: Float = source.readFloat32()

                visitor.visitInitExprF32ConstExpr(value)
            }

            Opcode.F64_CONST -> {
                val value: Double = source.readFloat64()

                visitor.visitInitExprF64ConstExpr(value)
            }

            Opcode.V128_CONST -> {
                if (!options.features.isSIMDEnabled) {
                    throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                }

                val value: V128Value = source.readV128()

                visitor.visitInitExprV128ConstExpr(value)
            }

            Opcode.GET_GLOBAL -> {
                val globaIndex: UInt = source.readIndex()

                if (globaIndex > getNumberTotalGlobals()) {
                    throw ParserException("get_global index of $globaIndex exceed the maximum of ${getNumberTotalGlobals()}")
                }

                if (globaIndex > numberGlobalImports) {
                    throw ParserException("get_global index of $globaIndex exceed the number of globals of $numberGlobalImports")
                }

                visitor.visitInitExprGetGlobalExpr(globaIndex)
            }

            Opcode.END -> {
                visitor.visitInitExprEnd()
                return
            }

            else -> throw ParserException("Unexpected opcode in initializer expression: %$opcode(0x${opcode.opcode.toHexString()})")
        }
        if (requireUInt && (opcode != Opcode.I32_CONST) && (opcode != Opcode.GET_GLOBAL)) {
            throw ParserException("Expected i32 init_expr")
        }

        opcode = source.readOpcode()
        if (opcode == Opcode.END) {
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
            if (elementType != WasmType.FUNC_REF) {
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
            }
            exportVisitor.visitExport(exportIndex, externalKind, itemIndex, name)
        }

        exportVisitor.visitEnd()
    }

    public fun readV128(): V128Value {
        val value = uintArrayOf()

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

            if (form != WasmType.FUNC) {
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
            }
        }

        importVisitor.visitEnd()
    }

    private fun readDataCountSection(visitor: ModuleVisitor) {
        val dataCount = source.readVarUInt32()

        val dataCountSectionVisitor = visitor.visitDataCountSection()
        dataCountSectionVisitor.visitDataCount(dataCount)
        dataCountSectionVisitor.visitEnd()
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
