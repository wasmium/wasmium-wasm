package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.visitors.DataCountSectionVisitor

public class DataCountSectionVerifier(
    private val delegate: DataCountSectionVisitor,
    private val context: VerifierContext,
    private val dataCount: UInt
) : DataCountSectionVisitor {
    private var done: Boolean = false

    override fun visitEnd() {
        checkEnd()

        if (context.numberOfDataSegments != dataCount) {
            throw VerifierException("Number of data segments ${context.numberOfDataSegments} is different then the data count of $dataCount")
        }

        done = true
        delegate.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
