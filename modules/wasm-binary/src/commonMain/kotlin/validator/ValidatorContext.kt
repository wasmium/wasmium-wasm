package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.GlobalType.Mutability
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.CodeType
import org.wasmium.wasm.binary.tree.sections.FunctionType
import org.wasmium.wasm.binary.tree.sections.MemoryType
import org.wasmium.wasm.binary.tree.sections.TableType

public class ValidatorContext(
    public val options: ValidatorOptions,
) {
    /** List of type signatures. */
    public val types: MutableList<FunctionType> = mutableListOf()

    public val functions: MutableList<FunctionType> = mutableListOf()

    public val globals: MutableList<GlobalType> = mutableListOf()

    public val tables: MutableList<TableType> = mutableListOf()

    public val memories: MutableList<MemoryType> = mutableListOf()

    public val tags: MutableList<TagType> = mutableListOf()

    public val codes: MutableList<CodeType> = mutableListOf()

    public val returns: MutableList<WasmType> = mutableListOf()

    public var numberOfImportFunctions: UInt = 0u

    private fun checkResizableLimit(resizableLimits: ResizableLimits, max: UInt, message: String) {
        if (resizableLimits.initial > max) {
            throw ValidatorException("Initial value must not exceed value of $max")
        }

        if (resizableLimits.maximum != null) {
            if (resizableLimits.maximum < resizableLimits.initial) {
                throw ValidatorException("Maximum value must not be less than initial value")
            }

            if (resizableLimits.maximum > max) {
                throw ValidatorException("Maximum value must not exceed value of $max")
            }
        }
    }

    public fun checkTableType(elementType: WasmType, limits: ResizableLimits) {
        if (!elementType.isReferenceType()) {
            throw ValidatorException("Table element type must be a reference type")
        }

        checkResizableLimit(limits, UInt.MAX_VALUE - 1u, "Table size must be at most 2^32 - 1")
    }

    public fun checkMemoryType(limits: ResizableLimits) {
        checkResizableLimit(limits, 1u shl 16, "Memory size must not exceed 65536 pages (4GiB)")
    }

    public fun checkGlobalType(contentType: WasmType, mutability: Mutability) {
        if (!contentType.isValueType()) {
            throw ValidatorException("Global type must be a value type")
        }
    }

    public fun createLocalContext(locals: List<WasmType>, returns: List<WasmType>): LocalContext = LocalContext(
        types = types,
        functions = functions,
        globals = globals,
        tables = tables,
        memories = memories,
        tags = tags,
        codes = codes,
        locals = locals,
        returns = returns,
    )
}

public class LocalContext(
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
