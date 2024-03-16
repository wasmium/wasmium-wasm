package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.FunctionTypeNode
import org.wasmium.wasm.binary.tree.sections.TypeSectionNode
import org.wasmium.wasm.binary.visitors.TypeSectionAdapter

public class TypeSectionWriter(private val context: WriterContext) : TypeSectionAdapter() {
    init {
        this.context.typeSection = TypeSectionNode()
    }

    public override fun visitType(typeIndex: UInt, parameters: Array<WasmType>, results: Array<WasmType>) {
        val functionType = FunctionTypeNode()
        functionType.typeIndex = typeIndex
        functionType.parameters = parameters
        functionType.results = results

        context.typeSection?.types?.add(functionType)
    }

    public override fun visitEnd() {
        // empty
    }
}
