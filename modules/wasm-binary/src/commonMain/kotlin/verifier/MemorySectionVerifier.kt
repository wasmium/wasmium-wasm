package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.sections.MemoryType
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor

public class MemorySectionVerifier(private val delegate: MemorySectionVisitor? = null, private val context: VerifierContext) : MemorySectionVisitor {
    private var done: Boolean = false
    private var numberOfMemories: UInt = 0u

    override fun visitMemory(memoryType: MemoryType) {
        checkEnd()

        if (memoryType.limits.initial > WasmBinary.MAX_MEMORY_PAGES) {
            throw ParserException("Invalid initial memory pages")
        }

        if (memoryType.limits.maximum != null) {
            if (memoryType.limits.maximum > WasmBinary.MAX_MEMORY_PAGES) {
                throw ParserException("Invalid memory max page")
            }
        }

        numberOfMemories++

        delegate?.visitMemory(memoryType)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfMemories != 1u) {
            throw VerifierException("Only one memory element is allowed in v1.0")
        }

        if (this.numberOfMemories > WasmBinary.MAX_MEMORIES) {
            throw VerifierException("Number of memories ${this.numberOfMemories} exceed the maximum of ${WasmBinary.MAX_MEMORIES}")
        }

        context.numberOfMemories = numberOfMemories

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
