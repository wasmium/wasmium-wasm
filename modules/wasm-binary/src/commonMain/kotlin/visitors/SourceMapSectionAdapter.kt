package org.wasmium.wasm.binary.visitors

public open class SourceMapSectionAdapter(protected val delegate: SourceMapSectionVisitor? = null) : SourceMapSectionVisitor {

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
