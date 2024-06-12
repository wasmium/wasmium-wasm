package org.wasmium.wasm.binary.visitor

public open class FunctionNameAdapter(protected val delegate: FunctionNameVisitor? = null) : FunctionNameVisitor {

    override fun visitFunctionName(functionName: String): Unit = delegate?.visitFunctionName(functionName) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
