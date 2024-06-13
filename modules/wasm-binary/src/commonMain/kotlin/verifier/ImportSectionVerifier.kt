package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.MemoryType
import org.wasmium.wasm.binary.tree.TableType
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.visitor.ImportSectionVisitor

public class ImportSectionVerifier(private val delegate: ImportSectionVisitor? = null, private val context: VerifierContext) : ImportSectionVisitor {
    private var done: Boolean = false
    private var numberOfImports: UInt = 0u

    override fun visitFunction(moduleName: String, fieldName: String, functionType: FunctionType) {
        checkEnd()

        numberOfImports++
        context.numberOfFunctionImports++

        if (context.numberOfTotalFunctions > WasmBinary.MAX_FUNCTIONS) {
            throw VerifierException("Number of tag imports ${context.numberOfTotalFunctions} exceed the maximum of ${WasmBinary.MAX_FUNCTIONS}")
        }

        delegate?.visitFunction(moduleName, fieldName, functionType)
    }

    override fun visitGlobal(moduleName: String, fieldName: String, globalType: GlobalType) {
        checkEnd()

        numberOfImports++
        context.numberOfGlobalImports++

        if (context.numberOfTotalGlobals > WasmBinary.MAX_GLOBALS) {
            throw VerifierException("Number of tag imports ${context.numberOfTotalGlobals} exceed the maximum of ${WasmBinary.MAX_GLOBALS}")
        }

        delegate?.visitGlobal(moduleName, fieldName, globalType)
    }

    override fun visitTable(moduleName: String, fieldName: String, tableType: TableType) {
        checkEnd()

        numberOfImports++
        context.numberOfTableImports++

        if (!context.options.features.isReferenceTypesEnabled && context.numberOfTableImports > 1u) {
            throw VerifierException("Multiple table imports is not allowed")
        }

        if (context.options.features.isReferenceTypesEnabled && context.numberOfTotalTables > WasmBinary.MAX_TABLES) {
            throw VerifierException("Number of table imports ${context.numberOfTotalTables} exceed the maximum of ${WasmBinary.MAX_TABLES}")
        }

        delegate?.visitTable(moduleName, fieldName, tableType)
    }

    override fun visitMemory(moduleName: String, fieldName: String, memoryType: MemoryType) {
        checkEnd()

        numberOfImports++
        context.numberOfMemoryImports++

        if (!context.options.features.isReferenceTypesEnabled && context.numberOfMemoryImports > 1u) {
            throw VerifierException("Multiple memory imports is not allowed")
        }

        if (context.options.features.isReferenceTypesEnabled && context.numberOfTotalMemories > WasmBinary.MAX_MEMORIES) {
            throw VerifierException("Number of memory imports ${context.numberOfTotalMemories} exceed the maximum of ${WasmBinary.MAX_MEMORIES}")
        }

        delegate?.visitMemory(moduleName, fieldName, memoryType)
    }

    override fun visitTag(moduleName: String, fieldName: String, tagType: TagType) {
        checkEnd()

        numberOfImports++
        context.numberOfTagImports++

        if (context.options.features.isThreadsEnabled && context.numberOfTotalTags > WasmBinary.MAX_TAGS) {
            throw VerifierException("Number of tag imports ${context.numberOfTotalTags} exceed the maximum of ${WasmBinary.MAX_TAGS}")
        }

        delegate?.visitTag(moduleName, fieldName, tagType)
    }

    override fun visitEnd() {
        checkEnd()

        if (numberOfImports > WasmBinary.MAX_IMPORTS) {
            throw VerifierException("Number of imports $numberOfImports exceed the maximum of ${WasmBinary.MAX_IMPORTS}")
        }

        done = true
        delegate?.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
