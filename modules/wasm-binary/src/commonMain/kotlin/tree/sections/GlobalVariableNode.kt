package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.GlobalType

public class GlobalVariableNode(
    public val globalType: GlobalType,
    public val initializer: ExpressionNode,
)
