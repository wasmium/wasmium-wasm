package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.SectionKind
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
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ModuleReader(
    private val options: ReaderOptions,
) {
    private val context = ReaderContext(options)
    private val codeSectionReader: CodeSectionReader = CodeSectionReader(context)
    private val dataSectionReader: DataSectionReader = DataSectionReader(context)
    private val dataCountSectionReader: DataCountSectionReader = DataCountSectionReader(context)
    private val elementSectionReader: ElementSectionReader = ElementSectionReader(context)
    private val exportSectionReader: ExportSectionReader = ExportSectionReader(context)
    private val functionSectionReader: FunctionSectionReader = FunctionSectionReader(context)
    private val memorySectionReader: MemorySectionReader = MemorySectionReader(context)
    private val tagSectionReader: TagSectionReader = TagSectionReader(context)
    private val globalSectionReader: GlobalSectionReader = GlobalSectionReader(context)
    private val importSectionReader: ImportSectionReader = ImportSectionReader(context)
    private val startSectionReader: StartSectionReader = StartSectionReader(context)
    private val tableSectionReader: TableSectionReader = TableSectionReader(context)
    private val typeSectionReader: TypeSectionReader = TypeSectionReader(context)
    private val customSectionReader: CustomSectionReader = CustomSectionReader(context)

    public fun readModule(source: WasmBinaryReader, visitor: ModuleVisitor): ReaderResult {
        readHeader(source, visitor)

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
                    CUSTOM -> customSectionReader.readCustomSection(source, payloadSize, visitor)
                    TYPE -> typeSectionReader.readTypeSection(source, visitor)
                    IMPORT -> importSectionReader.readImportSection(source, visitor)
                    FUNCTION -> functionSectionReader.readFunctionSection(source, visitor)
                    TABLE -> tableSectionReader.readTableSection(source, visitor)
                    MEMORY -> memorySectionReader.readMemorySection(source, visitor)
                    TAG -> tagSectionReader.readTagSection(source, visitor)
                    GLOBAL -> globalSectionReader.readGlobalSection(source, visitor)
                    EXPORT -> exportSectionReader.readExportSection(source, visitor)
                    START -> startSectionReader.readStartSection(source, visitor)
                    ELEMENT -> elementSectionReader.readElementSection(source, visitor)
                    CODE -> codeSectionReader.readCodeSection(source, visitor)
                    DATA -> dataSectionReader.readDataSection(source, visitor)
                    DATA_COUNT -> dataCountSectionReader.readDataCountSection(source, visitor)
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

    private fun readHeader(source: WasmBinaryReader, visitor: ModuleVisitor) {
        // minimum allowed module size
        val minSize = 8u
        if (!source.request(minSize)) {
            throw ParserException("Expecting module size of at least: $minSize")
        }

        // read magic
        val magic = source.readUInt32()
        if (magic != WasmBinary.Meta.MAGIC_NUMBER) {
            throw ParserException("Module does not start with: $magic")
        }

        // read version
        val version = source.readUInt32()
        if (version != WasmBinary.Meta.VERSION_1 && version != WasmBinary.Meta.VERSION_2) {
            throw ParserException("Unsupported version number: $version")
        }

        visitor.visitHeader(version)
    }
}
