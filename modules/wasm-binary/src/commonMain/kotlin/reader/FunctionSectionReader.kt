package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class FunctionSectionReader(
    private val context: ReaderContext,
) {
    public fun readFunctionSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfFunctions = source.readVarUInt32()

        if (context.numberOfFunctions > WasmBinary.MAX_FUNCTIONS) {
            throw ParserException("Number of functions ${context.numberOfFunctions} exceed the maximum of ${WasmBinary.MAX_FUNCTIONS}")
        }

        val functionVisitor = visitor.visitFunctionSection()
        for (index in 0u until context.numberOfFunctions) {
            val functionIndex = context.numberOfFunctionImports + index

            val signatureIndex = source.readIndex()
            if (signatureIndex >= context.numberOfSignatures) {
                throw ParserException("Invalid function signature index: %$signatureIndex")
            }

            functionVisitor?.visitFunction(signatureIndex)
        }

        functionVisitor?.visitEnd()
    }
}
