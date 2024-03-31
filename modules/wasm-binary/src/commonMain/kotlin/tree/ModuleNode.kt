package org.wasmium.wasm.binary.tree

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
import org.wasmium.wasm.binary.tree.SectionName.EXCEPTION
import org.wasmium.wasm.binary.tree.SectionName.LINKING
import org.wasmium.wasm.binary.tree.SectionName.NAME
import org.wasmium.wasm.binary.tree.SectionName.RELOCATION
import org.wasmium.wasm.binary.tree.sections.CodeSectionNode
import org.wasmium.wasm.binary.tree.sections.CustomSectionNode
import org.wasmium.wasm.binary.tree.sections.DataCountSectionNode
import org.wasmium.wasm.binary.tree.sections.DataSectionNode
import org.wasmium.wasm.binary.tree.sections.ElementSectionNode
import org.wasmium.wasm.binary.tree.sections.ExceptionSectionNode
import org.wasmium.wasm.binary.tree.sections.ExportSectionNode
import org.wasmium.wasm.binary.tree.sections.FunctionSectionNode
import org.wasmium.wasm.binary.tree.sections.GlobalSectionNode
import org.wasmium.wasm.binary.tree.sections.ImportSectionNode
import org.wasmium.wasm.binary.tree.sections.LinkingSectionNode
import org.wasmium.wasm.binary.tree.sections.MemorySectionNode
import org.wasmium.wasm.binary.tree.sections.NameSectionNode
import org.wasmium.wasm.binary.tree.sections.RelocationSectionNode
import org.wasmium.wasm.binary.tree.sections.SectionNode
import org.wasmium.wasm.binary.tree.sections.StartSectionNode
import org.wasmium.wasm.binary.tree.sections.TableSectionNode
import org.wasmium.wasm.binary.tree.sections.TypeSectionNode
import org.wasmium.wasm.binary.tree.sections.UnknownSectionNode
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
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
import org.wasmium.wasm.binary.visitors.UnknownSectionVisitor

public class ModuleNode : ModuleVisitor {
    public var version: UInt? = null

    public val sections: MutableList<SectionNode> = mutableListOf()

    public fun accept(visitor: ModuleVisitor) {
        visitor.visitHeader(version!!)

        for (section in sections) {
            when (section.sectionKind) {
                CUSTOM -> {
                    val customSection = section as CustomSectionNode

                    when (customSection.name) {
                        EXCEPTION.sectionName -> {
                            val exceptionSection = customSection as ExceptionSectionNode

                            val exceptionSectionVisitor = visitor.visitExceptionSection()
                            if (exceptionSectionVisitor != null) {
                                exceptionSection.accept(exceptionSectionVisitor)
                            }
                        }

                        RELOCATION.sectionName -> {
                            val relocationSection = customSection as RelocationSectionNode

                            val relocationSectionVisitor = visitor.visitRelocationSection()
                            if (relocationSectionVisitor != null) {
                                relocationSection.accept(relocationSectionVisitor)
                            }
                        }

                        LINKING.sectionName -> {
                            val linkingSection = customSection as LinkingSectionNode

                            val linkingSectionVisitor = visitor.visitLinkingSection()
                            if (linkingSectionVisitor != null) {
                                linkingSection.accept(linkingSectionVisitor)
                            }
                        }

                        NAME.sectionName -> {
                            val nameSection = customSection as NameSectionNode

                            val nameSectionVisitor = visitor.visitNameSection()
                            if (nameSectionVisitor != null) {
                                nameSection.accept(nameSectionVisitor)
                            }
                        }

                        else -> {
                            val unknownSection = customSection as UnknownSectionNode

                            val unknownSectionVisitor = visitor.visitUnknownSection(unknownSection.name, unknownSection.content)
                            if (unknownSectionVisitor != null) {
                                unknownSection.accept(unknownSectionVisitor)
                            }
                        }
                    }
                }

                TYPE -> {
                    val typeSection = section as TypeSectionNode

                    val typeSectionVisitor = visitor.visitTypeSection()
                    if (typeSectionVisitor != null) {
                        typeSection.accept(typeSectionVisitor)
                    }
                }

                IMPORT -> {
                    val importSection = section as ImportSectionNode

                    val importSectionVisitor = visitor.visitImportSection()
                    if (importSectionVisitor != null) {
                        importSection.accept(importSectionVisitor)
                    }
                }

                FUNCTION -> {
                    val functionSection = section as FunctionSectionNode

                    val functionSectionVisitor = visitor.visitFunctionSection()
                    if (functionSectionVisitor != null) {
                        functionSection.accept(functionSectionVisitor)
                    }
                }

                TABLE -> {
                    val tableSection = section as TableSectionNode

                    val tableSectionVisitor = visitor.visitTableSection()
                    if (tableSectionVisitor != null) {
                        tableSection.accept(tableSectionVisitor)
                    }
                }

                MEMORY -> {
                    val memorySection = section as MemorySectionNode

                    val memorySectionVisitor = visitor.visitMemorySection()
                    if (memorySectionVisitor != null) {
                        memorySection.accept(memorySectionVisitor)
                    }
                }

                GLOBAL -> {
                    val globalSection = section as GlobalSectionNode

                    val globalSectionVisitor = visitor.visitGlobalSection()
                    if (globalSectionVisitor != null) {
                        globalSection.accept(globalSectionVisitor)
                    }
                }

                EXPORT -> {
                    val exportSection = section as ExportSectionNode

                    val exportSectionVisitor = visitor.visitExportSection()
                    if (exportSectionVisitor != null) {
                        exportSection.accept(exportSectionVisitor)
                    }
                }

                START -> {
                    val startSection = section as StartSectionNode

                    val startSectionVisitor = visitor.visitStartSection(startSection.functionIndex)
                    if (startSectionVisitor != null) {
                        startSection.accept(startSectionVisitor)
                    }
                }

                ELEMENT -> {
                    val elementSection = section as ElementSectionNode

                    val elementSectionVisitor = visitor.visitElementSection()
                    if (elementSectionVisitor != null) {
                        elementSection.accept(elementSectionVisitor)
                    }
                }

                DATA_COUNT -> {
                    val dataCountSection = section as DataCountSectionNode

                    val dataCountSectionVisitor = visitor.visitDataCountSection(dataCountSection.dataCount)
                    if (dataCountSectionVisitor != null) {
                        dataCountSection.accept(dataCountSectionVisitor)
                    }
                }

                CODE -> {
                    val codeSection = section as CodeSectionNode

                    val codeSectionVisitor = visitor.visitCodeSection()
                    if (codeSectionVisitor != null) {
                        codeSection.accept(codeSectionVisitor)
                    }
                }

                DATA -> {
                    val dataSection = section as DataSectionNode

                    val dataSectionVisitor = visitor.visitDataSection()
                    if (dataSectionVisitor != null) {
                        dataSection.accept(dataSectionVisitor)
                    }
                }

                else -> {}
            }
        }

        visitor.visitEnd()
    }

    public override fun visitHeader(version: UInt) {
        this.version = version
    }

    public override fun visitTypeSection(): TypeSectionVisitor {
        val typeSection = TypeSectionNode()
        sections.add(typeSection)

        return typeSection
    }

    public override fun visitFunctionSection(): FunctionSectionVisitor {
        val functionSection = FunctionSectionNode()
        sections.add(functionSection)

        return functionSection
    }

    public override fun visitStartSection(functionIndex: UInt): StartSectionVisitor {
        val startSection = StartSectionNode(functionIndex)
        sections.add(startSection)

        return startSection
    }

    public override fun visitImportSection(): ImportSectionVisitor {
        val importSection = ImportSectionNode()
        sections.add(importSection)

        return importSection
    }

    public override fun visitExportSection(): ExportSectionVisitor {
        val exportSection = ExportSectionNode()
        sections.add(exportSection)

        return exportSection
    }

    public override fun visitTableSection(): TableSectionVisitor {
        val tableSection = TableSectionNode()
        sections.add(tableSection)

        return tableSection
    }

    public override fun visitElementSection(): ElementSectionVisitor {
        val elementSection = ElementSectionNode()
        sections.add(elementSection)

        return elementSection
    }

    public override fun visitGlobalSection(): GlobalSectionVisitor {
        val globalSection = GlobalSectionNode()
        sections.add(globalSection)

        return globalSection
    }

    public override fun visitCodeSection(): CodeSectionVisitor {
        val codeSection = CodeSectionNode()
        sections.add(codeSection)

        return codeSection
    }

    public override fun visitMemorySection(): MemorySectionVisitor {
        val memorySection = MemorySectionNode()
        sections.add(memorySection)

        return memorySection
    }

    public override fun visitDataSection(): DataSectionVisitor {
        val dataSection = DataSectionNode()
        sections.add(dataSection)

        return dataSection
    }

    public override fun visitUnknownSection(name: String, content: ByteArray): UnknownSectionVisitor {
        val unknownCustomSection = UnknownSectionNode(name, content)
        sections.add(unknownCustomSection)

        return unknownCustomSection
    }

    public override fun visitExceptionSection(): ExceptionSectionVisitor {
        val exceptionSection = ExceptionSectionNode()
        sections.add(exceptionSection)

        return exceptionSection
    }

    public override fun visitRelocationSection(): RelocationSectionVisitor {
        val relocationSection = RelocationSectionNode()
        sections.add(relocationSection)

        return relocationSection
    }

    public override fun visitLinkingSection(): LinkingSectionVisitor {
        val linkingSection = LinkingSectionNode()
        sections.add(linkingSection)

        return linkingSection
    }

    public override fun visitNameSection(): NameSectionVisitor {
        val nameSection = NameSectionNode()
        sections.add(nameSection)

        return nameSection
    }

    public override fun visitDataCountSection(dataCount: UInt): DataCountSectionVisitor {
        val dataCountSection = DataCountSectionNode(dataCount)
        sections.add(dataCountSection)

        return dataCountSection
    }

    public override fun visitEnd() {
        // empty
    }
}
