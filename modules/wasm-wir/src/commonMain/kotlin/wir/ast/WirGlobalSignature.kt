package org.wasmium.wir.ast

import org.wasmium.wir.ast.type.WirValueType

public class WirGlobalSignature(
    public val valueType: WirValueType,
    public val mutable: Boolean,
)
