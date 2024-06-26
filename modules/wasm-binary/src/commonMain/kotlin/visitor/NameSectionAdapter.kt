package org.wasmium.wasm.binary.visitor

import org.wasmium.wasm.binary.tree.IndexName

public open class NameSectionAdapter(protected val delegate: NameSectionVisitor? = null) : NameSectionVisitor {

    override fun visitModuleName(name: String): Unit = delegate?.visitModuleName(name) ?: Unit

    override fun visitLocalNames(functionIndex: UInt, names: List<IndexName>): Unit = delegate?.visitLocalNames(functionIndex, names) ?: Unit

    override fun visitLabelNames(functionIndex: UInt, names: List<IndexName>): Unit = delegate?.visitLabelNames(functionIndex, names) ?: Unit

    override fun visitFieldNames(functionIndex: UInt, names: List<IndexName>): Unit = delegate?.visitFieldNames(functionIndex, names) ?: Unit

    override fun visitFunctionNames(names: List<IndexName>): Unit = delegate?.visitFunctionNames(names) ?: Unit

    override fun visitGlobalNames(names: List<IndexName>): Unit = delegate?.visitGlobalNames(names) ?: Unit

    override fun visitTagNames(names: List<IndexName>): Unit = delegate?.visitTagNames(names) ?: Unit

    override fun visitTableNames(names: List<IndexName>): Unit = delegate?.visitTableNames(names) ?: Unit

    override fun visitMemoryNames(names: List<IndexName>): Unit = delegate?.visitMemoryNames(names) ?: Unit

    override fun visitElementNames(names: List<IndexName>): Unit = delegate?.visitElementNames(names) ?: Unit

    override fun visitDataNames(names: List<IndexName>): Unit = delegate?.visitDataNames(names) ?: Unit

    override fun visitTypeNames(names: List<IndexName>): Unit = delegate?.visitTypeNames(names) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
