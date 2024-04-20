package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.visitors.DataCountSectionVisitor

public class DataCountSectionVerifier(
    private val delegate: DataCountSectionVisitor? = null,
    private val context: VerifierContext,
    private val dataSegmentCount: UInt
) : DataCountSectionVisitor {
    private var done: Boolean = false

    override fun visitEnd() {
        checkEnd()

        if (dataSegmentCount > WasmBinary.MAX_DATA_SEGMENTS) {
            throw VerifierException("Number of data segments $dataSegmentCount exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENTS}")
        }

        context.dataSegmentCount = dataSegmentCount

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
