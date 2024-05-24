package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.sections.MemoryType
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor

public class MemorySectionValidator(private val delegate: MemorySectionVisitor? = null, private val context: ValidatorContext) : MemorySectionVisitor {
    override fun visitMemory(memoryType: MemoryType) {
        context.checkMemoryType(memoryType)

        context.memoryTypes.add(memoryType)

        delegate?.visitMemory(memoryType)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
