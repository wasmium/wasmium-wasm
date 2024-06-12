package org.wasmium.wasm.binary.visitor

public open class StartSectionAdapter(protected val delegate: StartSectionVisitor? = null) : StartSectionVisitor {

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
