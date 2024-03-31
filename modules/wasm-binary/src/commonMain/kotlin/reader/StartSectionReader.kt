package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class StartSectionReader(
    private val context: ReaderContext,
) {
    public fun readStartSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val functionIndex = source.readIndex()

        if (functionIndex >= context.numberOfTotalFunctions) {
            throw ParserException("Invalid start function index: %$functionIndex")
        }

        val startSection = visitor.visitStartSection(functionIndex)
        startSection?.visitEnd()
    }
}
