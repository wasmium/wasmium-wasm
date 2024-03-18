package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.visitors.ModuleVisitor
import org.wasmium.wasm.binary.visitors.StartSectionVisitor

public class StartSectionReader(
    private val context: WasmBinaryContext,
) {
    public fun readStartSection(source: WasmSource, visitor: ModuleVisitor) {
        val functionIndex = source.readIndex()

        if (functionIndex >= context.numberTotalFunctions) {
            throw ParserException("Invalid start function index: %$functionIndex")
        }

        val startSection: StartSectionVisitor = visitor.visitStartSection()
        startSection.visitStartFunctionIndex(functionIndex)
        startSection.visitEnd()
    }
}
