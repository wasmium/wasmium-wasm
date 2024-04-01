package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ElementSegmentVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class ElementSegmentVerifier(private val delegate: ElementSegmentVisitor, private val context: VerifierContext) : ElementSegmentVisitor {
    private var done: Boolean = false

    override fun visitElementIndices(elementIndices: List<UInt>) {
        checkEnd()

        delegate.visitElementIndices(elementIndices)
    }

    override fun visitNonActiveMode(passive: Boolean) {
        checkEnd()

        delegate.visitNonActiveMode(passive)
    }

    override fun visitActiveMode(tableIndex: UInt): InitializerExpressionVisitor {
        checkEnd()

        return InitializerExpressionVerifier(delegate.visitInitializerExpression(), context)
    }

    override fun visitType(type: WasmType) {
        checkEnd()

        delegate.visitType(type)
    }

    override fun visitInitializerExpression(): InitializerExpressionVisitor {
        checkEnd()

        return InitializerExpressionVerifier(delegate.visitInitializerExpression(), context)
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
