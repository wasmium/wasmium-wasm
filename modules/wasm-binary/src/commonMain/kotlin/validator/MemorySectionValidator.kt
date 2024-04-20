package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor

public class MemorySectionValidator(private val delegate: MemorySectionVisitor, private val context: ValidatorContext) : MemorySectionVisitor {
    override fun visitMemory(limits: ResizableLimits) {
        delegate.visitMemory(limits)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
