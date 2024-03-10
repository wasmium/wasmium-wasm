package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.CustomSectionVisitor

public open class CustomSectionNode : SectionNode(SectionKind.CUSTOM), CustomSectionVisitor {
    public var name: String? = null
    public var content: ByteArray = byteArrayOf()

    public fun accept(customSectionVisitor: CustomSectionVisitor) {
        customSectionVisitor.visitSection(name!!, content)
    }

    public override fun visitSection(name: String, content: ByteArray) {
        this.name = name
        this.content = content
    }

    public override fun visitEnd() {
        // empty
    }
}
