package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitors.UnknownSectionVisitor

public open class UnknownSectionNode(
    public override var customSectionName: String,
    public open var content: ByteArray,
) : CustomSectionNode(customSectionName), UnknownSectionVisitor {

    public fun accept(unknownSectionVisitor: UnknownSectionVisitor) {
        unknownSectionVisitor.visitSection(customSectionName, content)

        unknownSectionVisitor.visitEnd()
    }

    public override fun visitSection(name: String, content: ByteArray) {
        this.customSectionName = name
        this.content = content
    }

    public override fun visitEnd() {
        // empty
    }
}
