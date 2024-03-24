package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.SectionKind

public abstract class CustomSectionNode(
    public open val customSectionName: String
) : SectionNode(SectionKind.CUSTOM)
