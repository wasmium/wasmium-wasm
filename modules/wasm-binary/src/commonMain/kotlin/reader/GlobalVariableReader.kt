package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor

public class GlobalVariableReader(
    private val context: ReaderContext,
    private val expressionReader: ExpressionReader = ExpressionReader(context),
) {
    public fun readGlobalVariable(source: WasmBinaryReader, globalVisitor: GlobalSectionVisitor) {
        val globalType = source.readGlobalType()
        val expressionVisitor = globalVisitor.visitGlobalVariable(globalType)

        expressionReader.readExpression(source, expressionVisitor)
        expressionVisitor.visitEnd()
    }
}
