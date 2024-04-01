package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor

public class ImportSectionVerifier(private val delegate: ImportSectionVisitor, private val context: VerifierContext) : ImportSectionVisitor {
    private var done: Boolean = false
    private var numberOfImports: UInt = 0u
    private var numberOfFunctionImports: UInt = 0u

    override fun visitFunction(moduleName: String, fieldName: String, typeIndex: UInt) {
        checkEnd()

        numberOfImports++
        numberOfFunctionImports++

        delegate.visitFunction(moduleName, fieldName, typeIndex)
    }

    override fun visitGlobal(moduleName: String, fieldName: String, type: WasmType, mutable: Boolean) {
        checkEnd()

        numberOfImports++

        delegate.visitGlobal(moduleName, fieldName, type, mutable)
    }

    override fun visitTable(moduleName: String, fieldName: String, elementType: WasmType, limits: ResizableLimits) {
        checkEnd()

        numberOfImports++

        delegate.visitTable(moduleName, fieldName, elementType, limits)
    }

    override fun visitMemory(moduleName: String, fieldName: String, limits: ResizableLimits) {
        checkEnd()

        numberOfImports++

        delegate.visitMemory(moduleName, fieldName, limits)
    }

    override fun visitException(moduleName: String, fieldName: String, exceptionTypes: List<WasmType>) {
        checkEnd()

        numberOfImports++

        delegate.visitException(moduleName, fieldName, exceptionTypes)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfImports > WasmBinary.MAX_IMPORTS) {
            throw VerifierException("Number of imports $numberOfImports exceed the maximum of ${WasmBinary.MAX_IMPORTS}")
        }

        context.numberOfFunctionImports = numberOfFunctionImports

        done = true
        delegate.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
