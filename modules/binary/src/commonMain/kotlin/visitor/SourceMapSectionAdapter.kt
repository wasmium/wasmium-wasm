package org.wasmium.wasm.binary.visitor

public open class SourceMapSectionAdapter(protected val delegate: SourceMapSectionVisitor? = null) : SourceMapSectionVisitor {

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
