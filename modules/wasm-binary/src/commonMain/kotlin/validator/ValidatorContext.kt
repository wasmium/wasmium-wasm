package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.sections.ExceptionType
import org.wasmium.wasm.binary.tree.sections.GlobalType
import org.wasmium.wasm.binary.tree.sections.MemoryType
import org.wasmium.wasm.binary.tree.sections.TableType
import org.wasmium.wasm.binary.tree.sections.FunctionType

public class ValidatorContext(
    public val options: ValidatorOptions,
) {
    /** List of type signatures. */
    public val types: MutableList<FunctionType> = mutableListOf()

    public val functions: MutableList<FunctionType> = mutableListOf()

    public val globals: MutableList<GlobalType> = mutableListOf()

    public val tables: MutableList<TableType> = mutableListOf()

    public val memories: MutableList<MemoryType> = mutableListOf()

    public val exceptions: MutableList<ExceptionType> = mutableListOf()

    public fun resultType(index: UInt): FunctionType = types[index.toInt()]
}
