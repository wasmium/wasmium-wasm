package org.wasmium.wir.ast

import org.wasmium.wir.ast.type.WirValueType

public class WirSignature(
    public val parameters: List<WirValueType>,
    public val results: List<WirValueType>,
)
