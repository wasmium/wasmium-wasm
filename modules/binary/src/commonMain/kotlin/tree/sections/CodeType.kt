package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.LocalVariable

public class CodeType(
    public val locals: List<LocalVariable>,
    public val expression: ExpressionNode,
)
