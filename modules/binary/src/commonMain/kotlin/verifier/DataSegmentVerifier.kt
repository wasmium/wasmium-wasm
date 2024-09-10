package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.visitor.DataSegmentVisitor
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class DataSegmentVerifier(private val delegate: DataSegmentVisitor? = null, private val context: VerifierContext) : DataSegmentVisitor {
    private var done: Boolean = false

    override fun visitActive(memoryIndex: UInt): ExpressionVisitor {
        checkEnd()

        if (memoryIndex != 0u) {
            throw ParserException("Bad memory index, must be 0.")
        }

        return ExpressionVerifier(delegate?.visitActive(memoryIndex), context)
    }

    override fun visitData(data: ByteArray) {
        checkEnd()

        delegate?.visitData(data)
    }

    override fun visitEnd() {
        checkEnd()

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
