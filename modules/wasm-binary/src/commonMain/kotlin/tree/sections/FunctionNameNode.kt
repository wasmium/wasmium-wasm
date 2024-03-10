package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitors.FunctionNameVisitor

public class FunctionNameNode : NameNode, FunctionNameVisitor {
    public var functionIndex: UInt? = null
    public var functionName: String? = null

    public override val nameKind: NameNodeKind
        get() = NameNodeKind.FUNCTION

    public override fun visitFunctionName(functionName: String) {
        this.functionName = functionName
    }

    public override fun visitEnd() {
        // empty
    }
}
