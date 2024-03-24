package org.wasmium.wasm.binary.visitors

public open class CodeSectionAdapter(protected val delegate: CodeSectionVisitor? = null) : CodeSectionVisitor {
    override fun visitFunctionBody(functionIndex: UInt): FunctionBodyVisitor {
        if (delegate != null) {
            return FunctionBodyAdapter(delegate.visitFunctionBody(functionIndex))
        }

        return FunctionBodyAdapter()
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
