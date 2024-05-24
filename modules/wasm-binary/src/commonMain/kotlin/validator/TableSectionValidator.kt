package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.TableType
import org.wasmium.wasm.binary.verifier.VerifierException
import org.wasmium.wasm.binary.visitors.TableSectionVisitor

public class TableSectionValidator(private val delegate: TableSectionVisitor? = null, private val context: ValidatorContext) : TableSectionVisitor {
    override fun visitTable(elementType: WasmType, limits: ResizableLimits) {
        if (!elementType.isReferenceType()) {
            throw VerifierException("Table element type must be a reference type")
        }

        context.tables.add(TableType(elementType, limits))

        delegate?.visitTable(elementType, limits)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
