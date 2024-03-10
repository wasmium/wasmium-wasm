package org.wasmium.wasm.binary.visitors

public open class FunctionNameAdapter(protected val delegate: FunctionNameVisitor? = null) : FunctionNameVisitor {
    public override fun visitFunctionName(name: String) {
        delegate?.visitFunctionName(name)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
