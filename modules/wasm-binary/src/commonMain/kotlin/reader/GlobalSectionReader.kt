package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class GlobalSectionReader(
    private val context: ReaderContext,
    private val globalVariableReader: GlobalVariableReader = GlobalVariableReader(context),
) {
    public fun readGlobalSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberGlobals = source.readVarUInt32()

        if (context.numberGlobals > WasmBinary.MAX_GLOBALS) {
            throw ParserException("Number of globals ${context.numberGlobals} exceed the maximum of ${WasmBinary.MAX_GLOBALS}")
        }

        val globalVisitor = visitor.visitGlobalSection()
        for (index in 0u until context.numberGlobals) {
            globalVariableReader.readGlobalVariable(source, index, globalVisitor)
        }

        globalVisitor?.visitEnd()
    }
}
