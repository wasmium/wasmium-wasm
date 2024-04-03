package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class InitializerExpressionVerifier(private val delegate: InitializerExpressionVisitor, private val context: VerifierContext) : InitializerExpressionVisitor {
    private var done: Boolean = false

    override fun visitInitExprI32ConstExpr(value: Int) {
        checkEnd()

        delegate.visitInitExprI32ConstExpr(value)
    }

    override fun visitInitExprI64ConstExpr(value: Long) {
        checkEnd()

        delegate.visitInitExprI64ConstExpr(value)
    }

    override fun visitInitExprF32ConstExpr(value: Float) {
        checkEnd()

        delegate.visitInitExprF32ConstExpr(value)
    }

    override fun visitInitExprF64ConstExpr(value: Double) {
        checkEnd()

        delegate.visitInitExprF64ConstExpr(value)
    }

    override fun visitInitExprGetGlobalExpr(globalIndex: UInt) {
        checkEnd()

        if (globalIndex > context.numberOfTotalGlobals) {
            throw ParserException("get_global index of $globalIndex exceed the maximum of ${context.numberOfTotalGlobals}")
        }

        if (globalIndex > context.numberOfGlobalImports) {
            throw ParserException("get_global index of $globalIndex exceed the number of globals of ${context.numberOfGlobalImports}")
        }

        delegate.visitInitExprGetGlobalExpr(globalIndex)
    }

    override fun visitInitExprV128ConstExpr(value: V128Value) {
        checkEnd()

        delegate.visitInitExprV128ConstExpr(value)
    }

    override fun visitInitExprEnd() {
        checkEnd()

        delegate.visitInitExprEnd()
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
