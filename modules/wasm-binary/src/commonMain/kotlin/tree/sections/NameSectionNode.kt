package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitors.NameSectionVisitor

public class NameSectionNode : CustomSectionNode(), NameSectionVisitor {
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
        if (module != null) {
            nameSectionVisitor.visitModuleName(module!!.name!!)
        }

        for (function in functions) {
            nameSectionVisitor.visitFunctionName(function.functionIndex!!, function.functionName!!)
        }

        for (local in locals) {
            nameSectionVisitor.visitLocalName(local.functionIndex!!, local.localIndex!!, local.name!!)
        }

        for (label in labels) {
            nameSectionVisitor.visitLocalName(label.functionIndex!!, label.localIndex!!, label.name!!)
        }

        for (globals in globals) {
            nameSectionVisitor.visitFunctionName(globals.functionIndex!!, globals.functionName!!)
        }

        for (table in tables) {
            nameSectionVisitor.visitTagName(table.functionIndex!!, table.functionName!!)
        }

        for (tag in tags) {
            nameSectionVisitor.visitTagName(tag.functionIndex!!, tag.functionName!!)
        }

        for (memory in memories) {
            nameSectionVisitor.visitMemoryName(memory.functionIndex!!, memory.functionName!!)
        }

        for (element in elements) {
            nameSectionVisitor.visitElementName(element.functionIndex!!, element.functionName!!)
        }

        for (data in datas) {
            nameSectionVisitor.visitDataName(data.functionIndex!!, data.functionName!!)
        }
    }

    public override fun visitModuleName(name: String) {
        val module = ModuleNameNode()
        module.name = name

        this.module = module
    }

    public override fun visitFunctionName(functionIndex: UInt, name: String) {
        val functionName = FunctionNameNode()
        functionName.functionIndex = functionIndex
        functionName.functionName = name

        functions.add(functionName)
    }

    public override fun visitGlobalName(functionIndex: UInt, name: String) {
        val functionName = FunctionNameNode()
        functionName.functionIndex = functionIndex
        functionName.functionName = name

        globals.add(functionName)
    }

    public override fun visitDataName(functionIndex: UInt, name: String) {
        val functionName = FunctionNameNode()
        functionName.functionIndex = functionIndex
        functionName.functionName = name

        datas.add(functionName)
    }

    public override fun visitElementName(functionIndex: UInt, name: String) {
        val functionName = FunctionNameNode()
        functionName.functionIndex = functionIndex
        functionName.functionName = name

        elements.add(functionName)
    }

    public override fun visitTagName(functionIndex: UInt, name: String) {
        val functionName = FunctionNameNode()
        functionName.functionIndex = functionIndex
        functionName.functionName = name

        tags.add(functionName)
    }

    public override fun visitMemoryName(functionIndex: UInt, name: String) {
        val functionName = FunctionNameNode()
        functionName.functionIndex = functionIndex
        functionName.functionName = name

        memories.add(functionName)
    }

    public override fun visitTableName(functionIndex: UInt, name: String) {
        val functionName = FunctionNameNode()
        functionName.functionIndex = functionIndex
        functionName.functionName = name

        tables.add(functionName)
    }

    public override fun visitLocalName(functionIndex: UInt, localIndex: UInt, name: String) {
        val localName = LocalNameNode()
        localName.functionIndex = functionIndex
        localName.localIndex = localIndex
        localName.name = name

        locals.add(localName)
    }

    public override fun visitLabelName(functionIndex: UInt, localIndex: UInt, name: String) {
        val localName = LocalNameNode()
        localName.functionIndex = functionIndex
        localName.localIndex = localIndex
        localName.name = name

        labels.add(localName)
    }

    public override fun visitEnd() {
        // empty
    }
}
