package org.wasmium.wasm.binary.verifier

public class VerifierContext(
    public val options: VerifierOptions,
) {
    /** Total number of imported functions. */
    public var numberOfFunctionImports: UInt = 0u

    /** Total number of imported tables. */
    public var numberOfTableImports: UInt = 0u

    /** Total number of imported memories. */
    public var numberOfMemoryImports: UInt = 0u

    /** Total number of imported globals. */
    public var numberOfGlobalImports: UInt = 0u

    /** Total number of imported exceptions. */
    public var numberOfExceptionImports: UInt = 0u

    /** Total number of signatures. */
    public var numberOfSignatures: UInt = 0u

    /** Total number of tables. */
    public var numberOfTables: UInt = 0u

    /** Total number of memories. */
    public var numberOfMemories: UInt = 0u

    /** Total number of globals. */
    public var numberOfGlobals: UInt = 0u

    /** Total number of imports. */
    public var numberOfImports: UInt = 0u

    /** Total number of functions. */
    public var numberOfFunctions: UInt = 0u

    /** Total number of exports. */
    public var numberOfExports: UInt = 0u

    /** Total number of exceptions. */
    public var numberOfExceptions: UInt = 0u

    public var numberOfTags: UInt = 0u

    public var numberOfElements: UInt = 0u

    public var numberOfDataSegments: UInt = 0u

    public var numberOfRelocations: UInt = 0u

    public var dataSegmentCount: UInt = 0u

    public val exportIndexes: MutableList<UInt> = mutableListOf()

    public val numberOfTotalFunctions: UInt get() = numberOfFunctionImports + numberOfFunctions

    public val numberOfTotalTables: UInt get() = numberOfTableImports + numberOfTables

    public val numberTotalMemories: UInt get() = numberOfMemoryImports + numberOfMemories

    public val numberOfTotalGlobals: UInt get() = numberOfGlobalImports + numberOfGlobals

    public val numberOfTotalExceptions: UInt get() = numberOfExceptionImports + numberOfExceptions

    public val messages: MutableList<String> = mutableListOf()
}
