package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary.MAX_ELEMENT_SEGMENT_FUNCTION_INDEXES
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ElementSegmentVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class ElementSegmentVerifier(private val delegate: ElementSegmentVisitor, private val context: VerifierContext) : ElementSegmentVisitor {
    private var done: Boolean = false

    override fun visitElementIndices(elementIndices: List<UInt>) {
        checkEnd()

        if (elementIndices.size.toUInt() > MAX_ELEMENT_SEGMENT_FUNCTION_INDEXES) {
            throw ParserException("Number of element indices ${elementIndices.size} exceed the maximum of $MAX_ELEMENT_SEGMENT_FUNCTION_INDEXES")
        }

        delegate.visitElementIndices(elementIndices)
    }

    override fun visitNonActiveMode(passive: Boolean) {
        checkEnd()

        delegate.visitNonActiveMode(passive)
    }

    override fun visitActiveMode(tableIndex: UInt): InitializerExpressionVisitor {
        checkEnd()

        if (tableIndex != 0u) {
            throw ParserException("Table elements must refer to table 0.")
        }

        return InitializerExpressionVerifier(delegate.visitInitializerExpression(), context)
    }

    override fun visitType(type: WasmType) {
        checkEnd()

        if (type != WasmType.FUNC_REF) {
            throw ParserException("Invalid element kind $type")
        }

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
