package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.sections.MemoryType
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor

public class MemorySectionValidator(private val delegate: MemorySectionVisitor? = null, private val context: ValidatorContext) : MemorySectionVisitor {
    override fun visitMemory(limits: ResizableLimits) {
        context.checkMemoryType(limits)

        context.memories.add(MemoryType(limits))

        delegate?.visitMemory(limits)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
