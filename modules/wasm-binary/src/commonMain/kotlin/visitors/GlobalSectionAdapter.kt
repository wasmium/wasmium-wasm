package org.wasmium.wasm.binary.visitors

public open class GlobalSectionAdapter(protected val delegate: GlobalSectionVisitor? = null) : GlobalSectionVisitor {
    public override fun visitGlobalVariable(globalIndex: UInt): GlobalVariableVisitor {
        if (delegate != null) {
            return GlobalVariableAdapter(delegate.visitGlobalVariable(globalIndex))
        }

        return GlobalVariableAdapter()
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}

