package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitor.UnknownSectionVisitor

public open class UnknownSectionNode(
    public override var name: String,
    public open var content: ByteArray,
) : CustomSectionNode(name), UnknownSectionVisitor {

    public fun accept(unknownSectionVisitor: UnknownSectionVisitor) {
        unknownSectionVisitor.visitEnd()
    }

    public override fun visitEnd() {
        // empty
    }
}
