package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.TableSectionVisitor

public class TableSectionVerifier(private val delegate: TableSectionVisitor, private val context: VerifierContext) : TableSectionVisitor {
    private var done: Boolean = false
    private var numberOfTables: UInt = 0u

    public override fun visitTable(elementType: WasmType, limits: ResizableLimits) {
        checkEnd()

        numberOfTables++

        delegate.visitTable(elementType, limits)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfTables > WasmBinary.MAX_TABLES) {
            throw VerifierException("Number of tables $numberOfTables exceed the maximum of ${WasmBinary.MAX_TABLES}");
        }

        done = true
        delegate.visitEnd()
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
