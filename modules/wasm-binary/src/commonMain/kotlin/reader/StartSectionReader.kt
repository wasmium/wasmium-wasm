package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class StartSectionReader(
    private val context: ReaderContext,
) {
    public fun readStartSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        val functionIndex = source.readIndex()

        val startSection = visitor.visitStartSection(functionIndex)
        startSection.visitEnd()
    }
}
