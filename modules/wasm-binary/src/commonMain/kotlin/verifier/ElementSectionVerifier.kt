package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSegmentVisitor

public class ElementSectionVerifier(private val delegate: ElementSectionVisitor? = null, private val context: VerifierContext) : ElementSectionVisitor {
    private var done: Boolean = false
    private var numberOfElementSegments: UInt = 0u

    override fun visitElementSegment(): ElementSegmentVisitor {
        checkEnd()

        numberOfElementSegments++

        return ElementSegmentVerifier(delegate?.visitElementSegment(), context)
    }

    override fun visitEnd() {
        checkEnd()

        if (numberOfElementSegments > WasmBinary.MAX_ELEMENT_SEGMENTS) {
            throw VerifierException("Number of element segments $numberOfElementSegments exceed the maximum of ${WasmBinary.MAX_ELEMENT_SEGMENTS}")
        }

        context.numberOfElements = numberOfElementSegments

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
