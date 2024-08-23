package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitor.FunctionNameVisitor

public class FunctionNameNode(
    public var functionIndex: UInt,
    public var functionName: String,
) : NameNode(NameNodeKind.FUNCTION), FunctionNameVisitor {

    public override fun visitFunctionName(functionName: String) {
        this.functionName = functionName
    }

    public override fun visitEnd() {
        // empty
    }
}
