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
            val typeIndex = source.readIndex()

            val functionType = context.functionTypes.getOrElse(typeIndex.toInt()){
                throw IllegalStateException("Function type not found")
            }

            functionVisitor.visitFunction(functionType)
        }

        functionVisitor.visitEnd()
    }
}
