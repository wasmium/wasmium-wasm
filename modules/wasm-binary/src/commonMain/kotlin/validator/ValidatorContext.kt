package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.sections.GlobalType
import org.wasmium.wasm.binary.tree.sections.TypeSignature

public class ValidatorContext(
    public val options: ValidatorOptions,
) {
    /** List of type signatures. */
    public val signatures: MutableList<TypeSignature> = mutableListOf()

    /** List of functions signature indexes */
    public val functions: MutableList<UInt> = mutableListOf()

    public val globals: MutableList<GlobalType> = mutableListOf()
}
