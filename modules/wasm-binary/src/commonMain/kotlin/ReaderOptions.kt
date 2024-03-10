package org.wasmium.wasm.binary

public class ReaderOptions(
    public val isDebugNamesEnabled: Boolean,
    public val isSkipCodeSection: Boolean,
    public val features: Features,
)
