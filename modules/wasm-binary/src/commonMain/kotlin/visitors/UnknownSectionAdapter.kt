package org.wasmium.wasm.binary.visitors

public open class UnknownSectionAdapter(private val delegate: UnknownSectionVisitor? = null) : UnknownSectionVisitor {

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
