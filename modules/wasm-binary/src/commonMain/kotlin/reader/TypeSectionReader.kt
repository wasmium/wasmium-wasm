package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class TypeSectionReader(
    private val context: ReaderContext,
) {
    public fun readTypeSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfTypes = source.readVarUInt32()

        val typeVisitor = visitor.visitTypeSection()
        for (typeIndex in 0u until context.numberOfTypes) {
            val functionType = source.readFunctionType()

            context.functionTypes.add(functionType)

            typeVisitor.visitType(functionType)
        }

        typeVisitor.visitEnd()
    }
}
