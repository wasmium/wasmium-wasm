package org.wasmium.wasm.binary.tree.sections

public class GlobalVariableNode(
    public val globalType: GlobalType,
    public val initializer: ExpressionNode,
)
