package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.tree.sections.TypeSignatureNode

public class VerifierContext {
    /** Total number of signatures. */
    public var numberOfSignatures: UInt = 0u

    /** Total number of functions. */
    public var numberOfFunctions: UInt = 0u

    public var numberOfExceptions: UInt = 0u

    public var numberOfMemories: UInt = 0u

    public var numberOfFunctionImports: UInt = 0u

    public var numberOfRelocations: UInt = 0u

    public var numberOfDataSegments: UInt = 0u

    /** List of type signatures. */
    public val typeSignatures: MutableList<TypeSignatureNode> = mutableListOf()

    public val exportIndexes: MutableList<UInt> = mutableListOf()

    public val mutableGlobals: MutableList<Boolean> = mutableListOf()
}
