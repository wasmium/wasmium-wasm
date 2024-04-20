package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSegmentVisitor

public class DataSectionVerifier(private val delegate: DataSectionVisitor? = null, private val context: VerifierContext) : DataSectionVisitor {
    private var done: Boolean = false

    override fun visitDataSegment(): DataSegmentVisitor {
        checkEnd()

        context.numberOfDataSegments++

        return DataSegmentVerifier(delegate?.visitDataSegment(), context)
    }

    override fun visitEnd() {
        checkEnd()

        if (context.dataSegmentCount > 0u && context.dataSegmentCount != context.numberOfDataSegments) {
            throw VerifierException("Number of data segments ${context.numberOfDataSegments} is different then the data count of ${context.dataSegmentCount}")
        }

        if (context.numberOfDataSegments > WasmBinary.MAX_DATA_SEGMENTS) {
            throw VerifierException("Number of data segments ${context.numberOfDataSegments} exceed the maximum of ${WasmBinary.MAX_DATA_SEGMENTS}");
        }

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
