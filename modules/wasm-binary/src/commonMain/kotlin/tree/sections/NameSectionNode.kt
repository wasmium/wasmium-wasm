package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionName
import org.wasmium.wasm.binary.visitors.NameSectionVisitor

public class NameSectionNode : CustomSectionNode(SectionName.NAME.sectionName), NameSectionVisitor {
    public var module: ModuleNameNode? = null
    public val functions: MutableList<FunctionNameNode> = mutableListOf()
    public val globals: MutableList<FunctionNameNode> = mutableListOf()
    public val locals: MutableList<LocalNameNode> = mutableListOf()
    public val labels: MutableList<LocalNameNode> = mutableListOf()
    public val tables: MutableList<FunctionNameNode> = mutableListOf()
    public val tags: MutableList<FunctionNameNode> = mutableListOf()
    public val memories: MutableList<FunctionNameNode> = mutableListOf()
    public val elements: MutableList<FunctionNameNode> = mutableListOf()
    public val datas: MutableList<FunctionNameNode> = mutableListOf()

    public fun accept(nameSectionVisitor: NameSectionVisitor) {
        module?.let {
            nameSectionVisitor.visitModuleName(it.name)
        }

        for (function in functions) {
            nameSectionVisitor.visitFunctionName(function.functionIndex, function.functionName)
        }

        for (local in locals) {
            nameSectionVisitor.visitLocalName(local.functionIndex, local.localIndex, local.name)
        }

        for (label in labels) {
            nameSectionVisitor.visitLocalName(label.functionIndex, label.localIndex, label.name)
        }

        for (globals in globals) {
            nameSectionVisitor.visitFunctionName(globals.functionIndex, globals.functionName)
        }

        for (table in tables) {
            nameSectionVisitor.visitTagName(table.functionIndex, table.functionName)
        }

        for (tag in tags) {
            nameSectionVisitor.visitTagName(tag.functionIndex, tag.functionName)
        }

        for (memory in memories) {
            nameSectionVisitor.visitMemoryName(memory.functionIndex, memory.functionName)
        }

        for (element in elements) {
            nameSectionVisitor.visitElementName(element.functionIndex, element.functionName)
        }

        for (data in datas) {
            nameSectionVisitor.visitDataName(data.functionIndex, data.functionName)
        }

        nameSectionVisitor.visitEnd()
    }

    public override fun visitModuleName(name: String) {
        this.module = ModuleNameNode(name)
    }

    public override fun visitFunctionName(functionIndex: UInt, name: String) {
        functions.add(FunctionNameNode(functionIndex, name))
    }

    public override fun visitGlobalName(functionIndex: UInt, name: String) {
        globals.add(FunctionNameNode(functionIndex, name))
    }

    public override fun visitDataName(functionIndex: UInt, name: String) {
        datas.add(FunctionNameNode(functionIndex, name))
    }

    public override fun visitElementName(functionIndex: UInt, name: String) {
        elements.add(FunctionNameNode(functionIndex, name))
    }

    public override fun visitTagName(functionIndex: UInt, name: String) {
        tags.add(FunctionNameNode(functionIndex, name))
    }

    public override fun visitMemoryName(functionIndex: UInt, name: String) {
        memories.add(FunctionNameNode(functionIndex, name))
    }

    public override fun visitTableName(functionIndex: UInt, name: String) {
        tables.add(FunctionNameNode(functionIndex, name))
    }

    public override fun visitLocalName(functionIndex: UInt, localIndex: UInt, name: String) {
        locals.add(LocalNameNode(functionIndex, localIndex, name))
    }

    public override fun visitLabelName(functionIndex: UInt, localIndex: UInt, name: String) {
        labels.add(LocalNameNode(functionIndex, localIndex, name))
    }

    public override fun visitEnd() {
        // empty
    }
}
