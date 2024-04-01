package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.visitors.DataSegmentVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class DataSegmentVerifier(private val delegate: DataSegmentVisitor, private val context: VerifierContext) : DataSegmentVisitor {
    private var done: Boolean = false
    private var numberOfDataSegments: UInt = 0u

    override fun visitActive(memoryIndex: UInt): InitializerExpressionVisitor {
        checkEnd()

        numberOfDataSegments++

        return InitializerExpressionVerifier(delegate.visitActive(memoryIndex), context)
    }

    override fun visitData(data: ByteArray) {
        checkEnd()

        numberOfDataSegments++

        delegate.visitData(data)
    }

    override fun visitEnd() {
        checkEnd()

        context.numberOfDataSegments = numberOfDataSegments

        done = true
        delegate.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
