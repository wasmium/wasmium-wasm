package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.Features

public class ReaderOptions(
    public val isDebugNamesEnabled: Boolean,
    public val isSkipCodeSection: Boolean,
    public val features: Features,
)
