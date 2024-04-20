package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.IndexName
import org.wasmium.wasm.binary.visitors.NameSectionVisitor

public class NameSectionValidator(private val delegate: NameSectionVisitor, private val context: ValidatorContext) : NameSectionVisitor {
    override fun visitModuleName(name: String) {
        delegate.visitModuleName(name)
    }

    override fun visitFunctionNames(names: List<IndexName>) {
        delegate.visitFunctionNames(names)
    }

    override fun visitGlobalNames(names: List<IndexName>) {
        delegate.visitGlobalNames(names)
    }

    override fun visitTagNames(names: List<IndexName>) {
        delegate.visitTagNames(names)
    }

    override fun visitTableNames(names: List<IndexName>) {
        delegate.visitTableNames(names)
    }

    override fun visitMemoryNames(names: List<IndexName>) {
        delegate.visitMemoryNames(names)
    }

    override fun visitElementNames(names: List<IndexName>) {
        delegate.visitElementNames(names)
    }

    override fun visitDataNames(names: List<IndexName>) {
        delegate.visitDataNames(names)
    }

    override fun visitTypeNames(names: List<IndexName>) {
        delegate.visitTypeNames(names)
    }

    override fun visitLocalNames(functionIndex: UInt, names: List<IndexName>) {
        delegate.visitLocalNames(functionIndex, names)
    }

    override fun visitLabelNames(functionIndex: UInt, names: List<IndexName>) {
        delegate.visitLabelNames(functionIndex, names)
    }

    override fun visitFieldNames(functionIndex: UInt, names: List<IndexName>) {
        delegate.visitFieldNames(functionIndex, names)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
