package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class FunctionSectionReader(
    private val context: ReaderContext,
) {
    public fun readFunctionSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfFunctions = source.readVarUInt32()

        val functionVisitor = visitor.visitFunctionSection()
        for (index in 0u until context.numberOfFunctions) {
            val functionIndex = context.numberOfFunctionImports + index

            val typeIndex = source.readIndex()

            functionVisitor.visitFunction(typeIndex)
        }

        functionVisitor.visitEnd()
    }
}
