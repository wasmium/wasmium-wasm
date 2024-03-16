package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.SectionKind.*
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.CustomSectionVisitor
import org.wasmium.wasm.binary.visitors.DataCountSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor
import org.wasmium.wasm.binary.visitors.ExportSectionVisitor
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor
import org.wasmium.wasm.binary.visitors.NameSectionVisitor
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor
import org.wasmium.wasm.binary.visitors.StartSectionVisitor
import org.wasmium.wasm.binary.visitors.TableSectionVisitor
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class ModuleNode : ModuleVisitor {
    protected var version: UInt? = null

    protected val sections: MutableList<SectionNode> = mutableListOf()

    public fun accept(visitor: ModuleVisitor) {
        visitor.visit(version!!)

        for (section in sections) {
            when (section.sectionKind) {
                CUSTOM -> {
                    val customSection: CustomSectionNode = section as CustomSectionNode

                    when (customSection.name) {
                        WasmBinary.SECTION_NAME_EXCEPTION -> {
                            val exceptionSection: ExceptionSectionNode = customSection as ExceptionSectionNode

                            val exceptionSectionVisitor = visitor.visitExceptionSection()
                            exceptionSection.accept(exceptionSectionVisitor)
                            exceptionSectionVisitor.visitEnd()
                        }

                        WasmBinary.SECTION_NAME_RELOCATION -> {
                            val relocationSection: RelocationSectionNode = customSection as RelocationSectionNode

                            val relocationSectionVisitor = visitor.visitRelocationSection()
                            relocationSection.accept(relocationSectionVisitor)
                            relocationSectionVisitor.visitEnd()
                        }

                        WasmBinary.SECTION_NAME_LINKING -> {
                            val linkingSection: LinkingSectionNode = customSection as LinkingSectionNode

                            val linkingSectionVisitor = visitor.visitLinkingSection()
                            linkingSection.accept(linkingSectionVisitor)
                            linkingSectionVisitor.visitEnd()
                        }

                        WasmBinary.SECTION_NAME_NAME -> {
                            val nameSection: NameSectionNode = customSection as NameSectionNode

                            val nameSectionVisitor = visitor.visitNameSection()
                            nameSection.accept(nameSectionVisitor)
                            nameSectionVisitor.visitEnd()
                        }

                        else -> {
                            val customSectionVisitor = visitor.visitCustomSection()
                            customSection.accept(customSectionVisitor)
                            customSectionVisitor.visitEnd()
                        }
                    }
                }

                TYPE -> {
                    val typeSection: TypeSectionNode = section as TypeSectionNode

                    val typeSectionVisitor = visitor.visitTypeSection()
                    typeSection.accept(typeSectionVisitor)
                    typeSectionVisitor.visitEnd()
                }

                IMPORT -> {
                    val importSection: ImportSectionNode = section as ImportSectionNode

                    val importSectionVisitor = visitor.visitImportSection()
                    importSection.accept(importSectionVisitor)
                    importSectionVisitor.visitEnd()
                }

                FUNCTION -> {
                    val functionSection: FunctionSectionNode = section as FunctionSectionNode

                    val functionSectionVisitor = visitor.visitFunctionSection()
                    functionSection.accept(functionSectionVisitor)
                    functionSectionVisitor.visitEnd()
                }

                TABLE -> {
                    val tableSection: TableSectionNode = section as TableSectionNode

                    val tableSectionVisitor = visitor.visitTableSection()
                    tableSection.accept(tableSectionVisitor)
                    tableSectionVisitor.visitEnd()
                }

                MEMORY -> {
                    val memorySection: MemorySectionNode = section as MemorySectionNode

                    val memorySectionVisitor = visitor.visitMemorySection()
                    memorySection.accept(memorySectionVisitor)
                    memorySectionVisitor.visitEnd()
                }

                GLOBAL -> {
                    val globalSection: GlobalSectionNode = section as GlobalSectionNode

                    val globalSectionVisitor = visitor.visitGlobalSection()
                    globalSection.accept(globalSectionVisitor)
                    globalSectionVisitor.visitEnd()
                }

                EXPORT -> {
                    val exportSection: ExportSectionNode = section as ExportSectionNode

                    val exportSectionVisitor = visitor.visitExportSection()
                    exportSection.accept(exportSectionVisitor)
                    exportSectionVisitor.visitEnd()
                }

                START -> {
                    val startSection: StartSectionNode = section as StartSectionNode

                    val startSectionVisitor = visitor.visitStartSection()
                    startSection.accept(startSectionVisitor)
                    startSectionVisitor.visitEnd()
                }

                ELEMENT -> {
                    val elementSection: ElementSectionNode = section as ElementSectionNode

                    val elementSectionVisitor = visitor.visitElementSection()
                    elementSection.accept(elementSectionVisitor)
                    elementSectionVisitor.visitEnd()
                }

                CODE -> {
                    val codeSection: CodeSectionNode = section as CodeSectionNode

                    val codeSectionVisitor = visitor.visitCodeSection()
                    codeSection.accept(codeSectionVisitor)
                    codeSectionVisitor.visitEnd()
                }

                DATA -> {
                    val dataSection: DataSectionNode = section as DataSectionNode

                    val dataSectionVisitor = visitor.visitDataSection()
                    dataSection.accept(dataSectionVisitor)
                    dataSectionVisitor.visitEnd()
                }

                DATA_COUNT -> {
                    val dataCountSection = section as DataCountSectionNode

                    val dataCountSectionVisitor = visitor.visitDataCountSection()
                    dataCountSection.accept(dataCountSectionVisitor)
                    dataCountSectionVisitor.visitEnd()
                }
            }
        }

        visitor.visitEnd()
    }

    override fun visit(version: UInt) {
        this.version = version
    }

    override fun visitTypeSection(): TypeSectionVisitor {
        val typeSection = TypeSectionNode()
        sections.add(typeSection)

        return typeSection
    }

    override fun visitFunctionSection(): FunctionSectionVisitor {
        val functionSection = FunctionSectionNode()
        sections.add(functionSection)

        return functionSection
    }

    override fun visitStartSection(): StartSectionVisitor {
        val startSection = StartSectionNode()
        sections.add(startSection)

        return startSection
    }

    override fun visitImportSection(): ImportSectionVisitor {
        val importSection = ImportSectionNode()
        sections.add(importSection)

        return importSection
    }

    override fun visitExportSection(): ExportSectionVisitor {
        val exportSection = ExportSectionNode()
        sections.add(exportSection)

        return exportSection
    }

    override fun visitTableSection(): TableSectionVisitor {
        val tableSection = TableSectionNode()
        sections.add(tableSection)

        return tableSection
    }

    override fun visitElementSection(): ElementSectionVisitor {
        val elementSection = ElementSectionNode()
        sections.add(elementSection)

        return elementSection
    }

    override fun visitGlobalSection(): GlobalSectionVisitor {
        val globalSection = GlobalSectionNode()
        sections.add(globalSection)

        return globalSection
    }

    override fun visitCodeSection(): CodeSectionVisitor {
        val codeSection = CodeSectionNode()
        sections.add(codeSection)

        return codeSection
    }

    override fun visitMemorySection(): MemorySectionVisitor {
        val memorySection = MemorySectionNode()
        sections.add(memorySection)

        return memorySection
    }

    override fun visitDataSection(): DataSectionVisitor {
        val dataSection = DataSectionNode()
        sections.add(dataSection)

        return dataSection
    }

    override fun visitCustomSection(): CustomSectionVisitor {
        val customSection = CustomSectionNode()
        sections.add(customSection)

        return customSection
    }

    override fun visitExceptionSection(): ExceptionSectionVisitor {
        val exceptionSection = ExceptionSectionNode()
        sections.add(exceptionSection)

        return exceptionSection
    }

    override fun visitRelocationSection(): RelocationSectionVisitor {
        val relocationSection = RelocationSectionNode()
        sections.add(relocationSection)

        return relocationSection
    }

    override fun visitLinkingSection(): LinkingSectionVisitor {
        val linkingSection = LinkingSectionNode()
        sections.add(linkingSection)

        return linkingSection
    }

    override fun visitNameSection(): NameSectionVisitor {
        val nameSection = NameSectionNode()
        sections.add(nameSection)

        return nameSection
    }

    public override fun visitDataCountSection(): DataCountSectionVisitor {
        val dataCountSection = DataCountSectionNode()
        sections.add(dataCountSection)

        return dataCountSection
    }

    override fun visitEnd() {
        // empty
    }
}
