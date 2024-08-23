package org.wasmium.wasm.binary.visitor

public open class ExternalDebugSectionAdapter(protected val delegate: ExternalDebugSectionVisitor? = null) : ExternalDebugSectionVisitor {

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
