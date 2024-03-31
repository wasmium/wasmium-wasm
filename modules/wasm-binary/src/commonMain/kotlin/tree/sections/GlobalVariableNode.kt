package org.wasmium.wasm.binary.tree.sections

public class GlobalVariableNode(
    public val globalType: GlobalTypeNode,
    public val initializer: InitializerExpressionNode,
)
