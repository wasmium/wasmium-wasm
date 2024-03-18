package org.wasmium.wasm.binary.reader

public class WasmBinaryContext(
    public val options: ReaderOptions,
) {
    /** Total number of imported functions. */
    public var numberFunctionImports: UInt = 0u

    /** Total number of imported tables. */
    public var numberTableImports: UInt = 0u

    /** Total number of imported memories. */
    public var numberMemoryImports: UInt = 0u

    /** Total number of imported globals. */
    public var numberGlobalImports: UInt = 0u

    /** Total number of imported exceptions. */
    public var numberExceptionImports: UInt = 0u

    /** Total number of signatures. */
    public var numberSignatures: UInt = 0u

    /** Total number of tables. */
    public var numberTables: UInt = 0u

    /** Total number of memories. */
    public var numberMemories: UInt = 0u

    /** Total number of globals. */
    public var numberGlobals: UInt = 0u

    /** Total number of imports. */
    public var numberImports: UInt = 0u

    /** Total number of functions. */
    public var numberFunctions: UInt = 0u

    /** Total number of exports. */
    public var numberExports: UInt = 0u

    /** Total number of elements. */
    public var numberElementSegments: UInt = 0u

    /** Total number of exceptions. */
    public var numberExceptions: UInt = 0u

    public val numberTotalFunctions: UInt
        get() = numberFunctionImports + numberFunctions

    public val numberTotalTables: UInt
        get() = numberTableImports + numberTables

    public val numberTotalMemories: UInt
        get() = numberMemoryImports + numberMemories

    public val numberTotalGlobals: UInt
        get() = numberGlobalImports + numberGlobals

    public val numberTotalExceptions: UInt
        get() = numberExceptionImports + numberExceptions

    public var nameSectionConsumed: Boolean = false

    public val messages: MutableList<String> = mutableListOf()
}
