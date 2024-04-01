package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSegmentVisitor

public class DataSectionVerifier(private val delegate: DataSectionVisitor, private val context: VerifierContext) : DataSectionVisitor {
    private var done: Boolean = false
    private var numberOfDataSegments: UInt = 0u

    override fun visitDataSegment(): DataSegmentVisitor {
        checkEnd()

        numberOfDataSegments++

        return DataSegmentVerifier(delegate.visitDataSegment(), context)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfDataSegments > WasmBinary.MAX_DATA_SEGMENTS) {
            throw VerifierException("Number of data segments $numberOfDataSegments exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENTS}");
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
