package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor

public class GlobalVariableReader(
    private val context: ReaderContext,
    private val expressionReader: ExpressionReader = ExpressionReader(context),
) {
    public fun readGlobalVariable(source: WasmBinaryReader, globalVisitor: GlobalSectionVisitor) {
        val contentType = source.readType()
        if (!contentType.isValueType()) {
            throw ParserException("Invalid global type: %#$contentType")
        }

        val mutability = source.readMutability()

        val expressionVisitor = globalVisitor.visitGlobalVariable(contentType, mutability)
        expressionReader.readExpression(source, expressionVisitor)
        expressionVisitor.visitEnd()
    }
}
