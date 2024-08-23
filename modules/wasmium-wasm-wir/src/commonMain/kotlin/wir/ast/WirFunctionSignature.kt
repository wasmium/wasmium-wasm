package org.wasmium.wir.ast

import org.wasmium.wir.ast.type.WirValueType

public class WirFunctionSignature(
    public val parameters: List<WirValueType>,
    public val results: List<WirValueType>,
)
