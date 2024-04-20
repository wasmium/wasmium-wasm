package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.TableSectionVisitor

public class TableSectionValidator(private val delegate: TableSectionVisitor, private val context: ValidatorContext) : TableSectionVisitor {
    override fun visitTable(elementType: WasmType, limits: ResizableLimits) {
        delegate.visitTable(elementType, limits)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }
}
