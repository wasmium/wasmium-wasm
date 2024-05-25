package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.TypeIndex
import org.wasmium.wasm.binary.tree.sections.MemoryType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ImportSectionVerifier(private val delegate: ImportSectionVisitor? = null, private val context: VerifierContext) : ImportSectionVisitor {
    private var done: Boolean = false
    private var numberOfImports: UInt = 0u

    override fun visitFunction(moduleName: String, fieldName: String, typeIndex: TypeIndex) {
        checkEnd()

        if (typeIndex.index >= context.numberOfTypes) {
            throw ParserException("Invalid import function index $typeIndex")
        }

        numberOfImports++
        context.numberOfFunctionImports++

        delegate?.visitFunction(moduleName, fieldName, typeIndex)
    }

    override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutability: GlobalType.Mutability) {
        checkEnd()

        numberOfImports++
        context.numberOfGlobalImports++

        delegate?.visitGlobal(moduleName, fieldName, type, mutability)
    }

    override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: MemoryLimits) {
        checkEnd()

        numberOfImports++
        context.numberOfTableImports++

        delegate?.visitTable(moduleName, fieldName, elementType, limits)
    }

    override fun visitMemory(moduleName: String, fieldName: String, memoryType: MemoryType) {
        checkEnd()

        numberOfImports++
        context.numberOfMemoryImports++

        delegate?.visitMemory(moduleName, fieldName, memoryType)
    }

    override fun visitTag(moduleName: String, fieldName: String, tagType: TagType) {
        checkEnd()

        numberOfImports++
        context.numberOfTagImports++

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
