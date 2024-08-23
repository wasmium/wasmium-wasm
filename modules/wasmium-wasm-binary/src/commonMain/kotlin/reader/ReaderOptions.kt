package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.Features
import org.wasmium.wasm.binary.tree.SectionKind

public class ReaderOptions(
    public val debugNames: Boolean,
    public val skipSections: List<SectionKind>,
    public val features: Features,
)
