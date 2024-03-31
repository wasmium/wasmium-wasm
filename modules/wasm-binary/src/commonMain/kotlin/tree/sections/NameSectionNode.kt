package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.IndexName
import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.NameSectionVisitor

public class NameSectionNode : CustomSectionNode(SectionName.NAME.sectionName), NameSectionVisitor {
    public var module: ModuleNameNode? = null
    public val functions: MutableList<IndexName> = mutableListOf()
    public val globals: MutableList<IndexName> = mutableListOf()
    public val locals: MutableList<LocalNameNode> = mutableListOf()
    public val labels: MutableList<LocalNameNode> = mutableListOf()
    public val fields: MutableList<LocalNameNode> = mutableListOf()
    public val tables: MutableList<IndexName> = mutableListOf()
    public val tags: MutableList<IndexName> = mutableListOf()
    public val memories: MutableList<IndexName> = mutableListOf()
    public val elements: MutableList<IndexName> = mutableListOf()
    public val datas: MutableList<IndexName> = mutableListOf()
    public val types: MutableList<IndexName> = mutableListOf()

    public fun accept(nameSectionVisitor: NameSectionVisitor) {
        module?.let {
            nameSectionVisitor.visitModuleName(it.name)
        }

        for (local in locals) {
            val names = local.names.map { IndexName(it.index, it.name) }
            nameSectionVisitor.visitLocalNames(local.functionIndex, names)
        }

        for (label in labels) {
            val names = label.names.map { IndexName(it.index, it.name) }
            nameSectionVisitor.visitLabelNames(label.functionIndex, names)
        }

        for (field in fields) {
            val names = field.names.map { IndexName(it.index, it.name) }
            nameSectionVisitor.visitLabelNames(field.functionIndex, names)
        }

        nameSectionVisitor.visitFunctionNames(functions)

        nameSectionVisitor.visitGlobalNames(globals)

        nameSectionVisitor.visitTableNames(tables)

        nameSectionVisitor.visitTagNames(tags)

        nameSectionVisitor.visitMemoryNames(memories)

        nameSectionVisitor.visitElementNames(elements)

        nameSectionVisitor.visitDataNames(datas)

        nameSectionVisitor.visitDataNames(types)

        nameSectionVisitor.visitEnd()
    }

    public override fun visitModuleName(name: String) {
        this.module = ModuleNameNode(name)
    }

    public override fun visitFunctionNames(names: List<IndexName>) {
        functions.addAll(names)
    }

    public override fun visitGlobalNames(names: List<IndexName>) {
        globals.addAll(names)
    }

    public override fun visitDataNames(names: List<IndexName>) {
        datas.addAll(names)
    }

    override fun visitTypeNames(names: List<IndexName>) {
        TODO("Not yet implemented")
    }

    public override fun visitElementNames(names: List<IndexName>) {
        elements.addAll(names)
    }

    public override fun visitTagNames(names: List<IndexName>) {
        tags.addAll(names)
    }

    public override fun visitMemoryNames(names: List<IndexName>) {
        memories.addAll(names)
    }

    public override fun visitTableNames(names: List<IndexName>) {
        tables.addAll(names)
    }

    public override fun visitLocalNames(functionIndex: UInt, names: List<IndexName>) {
        locals.add(LocalNameNode(functionIndex, names))
    }

    public override fun visitLabelNames(functionIndex: UInt, names: List<IndexName>) {
        labels.add(LocalNameNode(functionIndex, names))
    }

    override fun visitFieldNames(functionIndex: UInt, names: List<IndexName>) {
        TODO("Not yet implemented")
    }

    public override fun visitEnd() {
        // empty
    }
}
