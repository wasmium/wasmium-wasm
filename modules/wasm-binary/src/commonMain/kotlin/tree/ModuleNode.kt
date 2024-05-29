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
import org.wasmium.wasm.binary.tree.SectionKind.TAG
import org.wasmium.wasm.binary.tree.SectionKind.TYPE
import org.wasmium.wasm.binary.tree.SectionName.LINKING
import org.wasmium.wasm.binary.tree.SectionName.NAME
import org.wasmium.wasm.binary.tree.SectionName.RELOCATION
import org.wasmium.wasm.binary.tree.SectionName.SOURCE_MAPPING_URL
import org.wasmium.wasm.binary.tree.sections.CodeSectionNode
import org.wasmium.wasm.binary.tree.sections.CustomSectionNode
import org.wasmium.wasm.binary.tree.sections.DataCountSectionNode
import org.wasmium.wasm.binary.tree.sections.DataSectionNode
import org.wasmium.wasm.binary.tree.sections.ElementSectionNode
import org.wasmium.wasm.binary.tree.sections.ExportSectionNode
import org.wasmium.wasm.binary.tree.sections.ExternalDebugSectionNode
import org.wasmium.wasm.binary.tree.sections.FunctionSectionNode
import org.wasmium.wasm.binary.tree.sections.GlobalSectionNode
import org.wasmium.wasm.binary.tree.sections.ImportSectionNode
import org.wasmium.wasm.binary.tree.sections.LinkingSectionNode
import org.wasmium.wasm.binary.tree.sections.MemorySectionNode
import org.wasmium.wasm.binary.tree.sections.NameSectionNode
import org.wasmium.wasm.binary.tree.sections.RelocationSectionNode
import org.wasmium.wasm.binary.tree.sections.SectionNode
import org.wasmium.wasm.binary.tree.sections.SourceMapSectionNode
import org.wasmium.wasm.binary.tree.sections.StartSectionNode
import org.wasmium.wasm.binary.tree.sections.TableSectionNode
import org.wasmium.wasm.binary.tree.sections.TagSectionNode
import org.wasmium.wasm.binary.tree.sections.TypeSectionNode
import org.wasmium.wasm.binary.tree.sections.UnknownSectionNode
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.DataCountSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ExportSectionVisitor
import org.wasmium.wasm.binary.visitors.ExternalDebugSectionVisitor
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor
import org.wasmium.wasm.binary.visitors.NameSectionVisitor
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor
import org.wasmium.wasm.binary.visitors.SourceMapSectionVisitor
import org.wasmium.wasm.binary.visitors.StartSectionVisitor
import org.wasmium.wasm.binary.visitors.TableSectionVisitor
import org.wasmium.wasm.binary.visitors.TagSectionVisitor
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
                        RELOCATION.sectionName -> {
                            val relocationSection = customSection as RelocationSectionNode

                            visitor.visitRelocationSection()?.let {
                                relocationSection.accept(it)
                            }
                        }

                        LINKING.sectionName -> {
                            val linkingSection = customSection as LinkingSectionNode

                            visitor.visitLinkingSection()?.let {
                                linkingSection.accept(it)
                            }
                        }

                        NAME.sectionName -> {
                            val nameSection = customSection as NameSectionNode

                            visitor.visitNameSection()?.let {
                                nameSection.accept(it)
                            }
                        }

                        SOURCE_MAPPING_URL.sectionName -> {
                            val sourceMap = customSection as SourceMapSectionNode

                            visitor.visitSourceMapSection(sourceMap.url)?.let {
                                sourceMap.accept(it)
                            }
                        }

                        else -> {
                            val unknownSection = customSection as UnknownSectionNode

                            visitor.visitUnknownSection(unknownSection.name, unknownSection.content)?.let {
                                unknownSection.accept(it)
                            }
                        }
                    }
                }

                TYPE -> {
                    val typeSection = section as TypeSectionNode

                    visitor.visitTypeSection() ?.let {
                        typeSection.accept(it)
                    }
                }

                IMPORT -> {
                    val importSection = section as ImportSectionNode

                    visitor.visitImportSection().let {
                        importSection.accept(it)
                    }
                }

                FUNCTION -> {
                    val functionSection = section as FunctionSectionNode

                    visitor.visitFunctionSection().let {
                        functionSection.accept(it)
                    }
                }

                TABLE -> {
                    val tableSection = section as TableSectionNode

                    visitor.visitTableSection().let {
                        tableSection.accept(it)
                    }
                }

                MEMORY -> {
                    val memorySection = section as MemorySectionNode

                    visitor.visitMemorySection().let {
                        memorySection.accept(it)
                    }
                }

                GLOBAL -> {
                    val globalSection = section as GlobalSectionNode

                    visitor.visitGlobalSection().let {
                        globalSection.accept(it)
                    }
                }

                EXPORT -> {
                    val exportSection = section as ExportSectionNode

                    visitor.visitExportSection().let {
                        exportSection.accept(it)
                    }
                }

                START -> {
                    val startSection = section as StartSectionNode

                    visitor.visitStartSection(startSection.functionIndex).let {
                        startSection.accept(it)
                    }
                }

                ELEMENT -> {
                    val elementSection = section as ElementSectionNode

                    visitor.visitElementSection().let {
                        elementSection.accept(it)
                    }
                }

                DATA_COUNT -> {
                    val dataCountSection = section as DataCountSectionNode

                    visitor.visitDataCountSection(dataCountSection.dataCount) .let {
                        dataCountSection.accept(it)
                    }
                }

                CODE -> {
                    val codeSection = section as CodeSectionNode

                    visitor.visitCodeSection().let {
                        codeSection.accept(it)
                    }
                }

                DATA -> {
                    val dataSection = section as DataSectionNode

                    visitor.visitDataSection().let {
                        dataSection.accept(it)
                    }
                }

                TAG -> {
                    val tagSection = section as TagSectionNode

                    visitor.visitTagSection().let {
                        tagSection.accept(it)
                    }
                }
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

    public override fun visitTagSection(): TagSectionVisitor {
        val tagSection = TagSectionNode()
        sections.add(tagSection)

        return tagSection
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

    public override fun visitSourceMapSection(sourceMapUrl: String): SourceMapSectionVisitor {
        val sourceMapSection = SourceMapSectionNode(sourceMapUrl)

        sections.add(sourceMapSection)

        return sourceMapSection
    }

    public override fun visitExternalDebugSection(externalDebugUrl: String): ExternalDebugSectionVisitor {
        val externalDebugSection = ExternalDebugSectionNode(externalDebugUrl)

        sections.add(externalDebugSection)

        return externalDebugSection
    }

    public override fun visitEnd() {
        // empty
    }
}
