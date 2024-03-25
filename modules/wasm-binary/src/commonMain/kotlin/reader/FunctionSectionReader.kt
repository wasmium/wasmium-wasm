package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class FunctionSectionReader(
    private val context: ReaderContext,
) {
    public fun readFunctionSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberFunctions = source.readVarUInt32()

        if (context.numberFunctions > WasmBinary.MAX_FUNCTIONS) {
            throw ParserException("Number of functions ${context.numberFunctions} exceed the maximum of ${WasmBinary.MAX_FUNCTIONS}")
        }

        val functionVisitor = visitor.visitFunctionSection()
        for (index in 0u until context.numberFunctions) {
            val functionIndex = context.numberFunctionImports + index

            val signatureIndex = source.readIndex()
            if (signatureIndex >= context.numberSignatures) {
                throw ParserException("Invalid function signature index: %$signatureIndex")
            }

            functionVisitor?.visitFunction(functionIndex, signatureIndex)
        }

        functionVisitor?.visitEnd()
    }
}
