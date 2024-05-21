package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.Features

public class WriterOptions(
    public val isDebugNamesEnabled: Boolean,
    public val isRelocatableEnabled: Boolean,
    /** Available features. */
    public val features: Features,
)
