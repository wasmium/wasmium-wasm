package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.Limits
import org.wasmium.wasm.binary.tree.MemoryType
import org.wasmium.wasm.binary.tree.TableType
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.CodeType

public class ValidatorContext(
    public val options: ValidatorOptions,
) {
    /** List of type signatures. */
    public val types: MutableList<FunctionType> = mutableListOf()

    public val functions: MutableList<FunctionType> = mutableListOf()

    public val globals: MutableList<GlobalType> = mutableListOf()

    public val tables: MutableList<TableType> = mutableListOf()

    public val memoryTypes: MutableList<MemoryType> = mutableListOf()

    public val tags: MutableList<TagType> = mutableListOf()

    public val codes: MutableList<CodeType> = mutableListOf()

    public val returns: MutableList<WasmType> = mutableListOf()

    public var numberOfImportFunctions: UInt = 0u

    private fun checkLimits(limits: Limits, max: UInt, message: String) {
        if (limits.initial > max) {
            throw ValidatorException("Initial value must not exceed value of $max")
        }

        if (limits.maximum != null) {
            if (limits.maximum < limits.initial) {
                throw ValidatorException("Maximum value must not be less than initial value")
            }

            if (limits.maximum > max) {
                throw ValidatorException("Maximum value must not exceed value of $max")
            }
        }
    }

    public fun checkTableType(tableType: TableType) {
        if (!tableType.elementType.isReferenceType()) {
            throw ValidatorException("Table element type must be a reference type")
        }

        if (tableType.limits.isShared()) {
            throw ParserException("Tables may not be shared.")
        }

        checkLimits(tableType.limits, UInt.MAX_VALUE - 1u, "Table size must be at most 2^32 - 1")
    }

    public fun checkMemoryType(memoryType: MemoryType) {
        checkLimits(memoryType.limits, 1u shl 16, "Memory size must not exceed 65536 pages (4GiB)")
    }

    public fun checkGlobalType(globalType: GlobalType) {
        if (!globalType.contentType.isValueType()) {
            throw ValidatorException("Global type must be a value type")
        }
    }

    public fun checkFunctionType(functionType: FunctionType): FunctionType {
        checkValueTypes(functionType.parameters)
        checkValueTypes(functionType.results)

        if (!options.features.isMultiValueEnabled && (functionType.results.size > 1)) {
            throw ValidatorException("Function with multi-value result not allowed.")
        }

        return functionType
    }

    private fun checkValueTypes(types: List<WasmType>) {
        for (type in types) {
            if (!type.isValueType()) {
                throw ParserException("Type $type is not value type")
            }
        }
    }

    public fun createLocalContext(locals: List<WasmType>, returns: List<WasmType>): LocalContext = LocalContext(
        options = options,
        types = types,
        functions = functions,
        globals = globals,
        tables = tables,
        memories = memoryTypes,
        tags = tags,
        codes = codes,
        locals = locals,
        returns = returns,
    )
}

public class LocalContext(
    public val options: ValidatorOptions,
    public val types: List<FunctionType>,
    public val functions: List<FunctionType>,
    public val globals: List<GlobalType>,
    public val tables: List<TableType>,
    public val memories: List<MemoryType>,
    public val tags: List<TagType>,
    public val codes: List<CodeType>,

    public val locals: List<WasmType>,
    public val returns: List<WasmType>,
)
