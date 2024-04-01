package org.wasmium.wasm.binary.visitors

public open class FunctionNameAdapter(protected val delegate: FunctionNameVisitor? = null) : FunctionNameVisitor {

    public override fun visitFunctionName(functionName: String): Unit = delegate?.visitFunctionName(functionName) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
