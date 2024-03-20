package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
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
import org.wasmium.wasm.binary.tree.SectionKind.TYPE
import org.wasmium.wasm.binary.tree.WasmVersion
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class ModuleReader(
    private val context: ReaderContext,
    private val codeSectionReader: CodeSectionReader = CodeSectionReader(context),
    private val dataSectionReader: DataSectionReader = DataSectionReader(context),
    private val dataCountSectionReader: DataCountSectionReader = DataCountSectionReader(context),
    private val elementSectionReader: ElementSectionReader = ElementSectionReader(context),
    private val exportSectionReader: ExportSectionReader = ExportSectionReader(context),
    private val functionSectionReader: FunctionSectionReader = FunctionSectionReader(context),
    private val globalSectionReader: GlobalSectionReader = GlobalSectionReader(context),
    private val importSectionReader: ImportSectionReader = ImportSectionReader(context),
    private val memorySectionReader: MemorySectionReader = MemorySectionReader(context),
    private val startSectionReader: StartSectionReader = StartSectionReader(context),
    private val tableSectionReader: TableSectionReader = TableSectionReader(context),
    private val typeSectionReader: TypeSectionReader = TypeSectionReader(context),
    private val customSectionReader: CustomSectionReader = CustomSectionReader(context),
) {
    public fun readModule(source: WasmSource, visitor: ModuleVisitor): ReaderResult {
        // minimum allowed module size
        val minSize = 8u
        if (!source.require(minSize)) {
            throw ParserException("Expecting module size of at least: $minSize")
        }

        // read magic
        val magic = source.readUInt32()
        if (magic != WasmBinary.MAGIC_NUMBER) {
            throw ParserException("Module does not start with: $magic")
        }

        // read version
        val version = source.readUInt32()
        if ((version <= 0u) || (version > WasmVersion.V1.version)) {
            throw ParserException("Unsupported version number: $version")
        }

        visitor.visit(version)
        readSection(source, visitor)
        visitor.visitEnd()

        // maximum allowed module size
        if (source.position > WasmBinary.MAX_MODULE_SIZE) {
            throw ParserException("Module size of ${source.position} is too large, maximum allowed is ${WasmBinary.MAX_MODULE_SIZE}")
        }

        return ReaderResult.Success(context.messages)
    }

    private fun readSection(source: WasmSource, visitor: ModuleVisitor) {
        // current section
        var section: SectionKind
        // total read sections
        var numberOfSections = 0u

        var lastSection: SectionKind? = null

        while (!source.exhausted()) {
            if (numberOfSections > WasmBinary.MAX_SECTIONS) {
                throw ParserException("Sections size of $numberOfSections exceed the maximum of ${WasmBinary.MAX_SECTIONS}")
            }

            section = source.readSectionKind()
            if (section != CUSTOM) {
                // not consider CUSTOM section for ordering
                if (lastSection != null) {
                    if (section.ordinal < lastSection.ordinal) {
                        throw ParserException("Invalid section order of ${lastSection.name} followed by ${section.name}")
                    }
                }

                lastSection = section
            }

            val payloadSize = source.readVarUInt32()
            if (payloadSize > WasmBinary.MAX_SECTION_LENGTH) {
                throw ParserException("Section size of $payloadSize exceed the maximum of ${WasmBinary.MAX_SECTION_LENGTH}")
            }

            if (!source.require(payloadSize)) {
                throw ParserException("Section payload greater then input.")
            }

            val startPosition = source.position

            when (section) {
                CUSTOM -> customSectionReader.readCustomSection(source, payloadSize, visitor)
                TYPE -> typeSectionReader.readTypeSection(source, visitor)
                IMPORT -> importSectionReader.readImportSection(source, visitor)
                FUNCTION -> functionSectionReader.readFunctionSection(source, visitor)
                TABLE -> tableSectionReader.readTableSection(source, visitor)
                MEMORY -> memorySectionReader.readMemorySection(source, visitor)
                GLOBAL -> globalSectionReader.readGlobalSection(source, visitor)
                EXPORT -> exportSectionReader.readExportSection(source, visitor)
                START -> startSectionReader.readStartSection(source, visitor)
                ELEMENT -> elementSectionReader.readElementSection(source, visitor)
                CODE -> codeSectionReader.readCodeSection(source, payloadSize, visitor)
                DATA -> dataSectionReader.readDataSection(source, visitor)
                DATA_COUNT -> dataCountSectionReader.readDataCountSection(source, visitor)
            }

            if (payloadSize != source.position - startPosition) {
                throw ParserException("Invalid size of section id: $section, expected: $payloadSize, actual: ${source.position - startPosition}")
            }

            if (context.nameSectionConsumed && section != CUSTOM) {
                throw ParserException("${section.name} section can not occur after Name section")
            }

            numberOfSections++
        }
    }
}
