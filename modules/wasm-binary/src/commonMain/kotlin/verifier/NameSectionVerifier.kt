package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.tree.IndexName
import org.wasmium.wasm.binary.visitors.NameSectionVisitor

public class NameSectionVerifier(private val delegate: NameSectionVisitor, private val context: VerifierContext) : NameSectionVisitor {
    private var done: Boolean = false

    override fun visitModuleName(name: String) {
        checkEnd()

        delegate.visitModuleName(name)
    }

    override fun visitFunctionNames(names: List<IndexName>) {
        checkEnd()

        delegate.visitFunctionNames(names)
    }

    override fun visitGlobalNames(names: List<IndexName>) {
        checkEnd()

        delegate.visitGlobalNames(names)
    }

    override fun visitTagNames(names: List<IndexName>) {
        checkEnd()

        delegate.visitTagNames(names)
    }

    override fun visitTableNames(names: List<IndexName>) {
        checkEnd()

        delegate.visitTableNames(names)
    }

    override fun visitMemoryNames(names: List<IndexName>) {
        checkEnd()

        delegate.visitMemoryNames(names)
    }

    override fun visitElementNames(names: List<IndexName>) {
        checkEnd()

        delegate.visitElementNames(names)
    }

    override fun visitDataNames(names: List<IndexName>) {
        checkEnd()

        delegate.visitDataNames(names)
    }

    override fun visitTypeNames(names: List<IndexName>) {
        checkEnd()

        delegate.visitTypeNames(names)
    }

    override fun visitLocalNames(functionIndex: UInt, names: List<IndexName>) {
        checkEnd()

        delegate.visitLocalNames(functionIndex, names)
    }

    override fun visitLabelNames(functionIndex: UInt, names: List<IndexName>) {
        checkEnd()

        delegate.visitLabelNames(functionIndex, names)
    }

    override fun visitFieldNames(functionIndex: UInt, names: List<IndexName>) {
        checkEnd()

        delegate.visitFieldNames(functionIndex, names)
    }

    override fun visitEnd() {
        checkEnd()

        done = true
        delegate.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
