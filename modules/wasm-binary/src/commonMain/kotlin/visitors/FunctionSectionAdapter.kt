package org.wasmium.wasm.binary.visitors

public class FunctionSectionAdapter(protected val delegate: FunctionSectionVisitor? = null) : FunctionSectionVisitor {
    public override fun visitFunction(functionIndex: UInt, typeIndex: UInt) {
        delegate?.visitFunction(functionIndex, typeIndex)
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
