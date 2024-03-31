package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class CodeSectionReader(
    private val context: ReaderContext,
    private val expressionReader: ExpressionReader = ExpressionReader(context)
) {

    public fun readCodeSection(source: WasmBinaryReader, payloadSize: UInt, visitor: ModuleVisitor) {
        val numberOfCodes = source.readVarUInt32()
        if (numberOfCodes != context.numberOfFunctions) {
            throw ParserException("Invalid function section size: $numberOfCodes must be equal to functionSize of: ${context.numberOfFunctions}")
        }

        val codeVisitor = visitor.visitCodeSection()

        for (index in 0u until numberOfCodes) {
            val functionIndex = context.numberOfFunctionImports + index
            // TODO check max?

            val bodySize = source.readVarUInt32()
            if (bodySize == 0u) {
                throw ParserException("Empty function size")
            }

            if (bodySize > WasmBinary.MAX_FUNCTION_SIZE) {
                throw ParserException("Function body size $bodySize exceed the maximum of ${WasmBinary.MAX_FUNCTION_SIZE}")
            }

            val startAvailable = source.position

            val locals = mutableListOf<LocalVariable>()
            var totalOfLocals = 0u
            val localsSize = source.readVarUInt32()

            if (localsSize > WasmBinary.MAX_FUNCTION_LOCALS) {
                throw ParserException("Number of function locals $localsSize exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS}")
            }

            for (localIndex in 0u until localsSize) {
                val numberOfLocalTypes = source.readVarUInt32()

                val localType = source.readType()
                if (!localType.isValueType()) {
                    throw ParserException("Invalid local type: %#$localType")
                }

                totalOfLocals += numberOfLocalTypes

                if (numberOfLocalTypes > WasmBinary.MAX_FUNCTION_LOCALS_TOTAL) {
                    throw ParserException("Number of total function locals $totalOfLocals exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS_TOTAL}")
                }

                locals.add(LocalVariable(numberOfLocalTypes, localType))
            }

            val expressionVisitor = codeVisitor?.visitCode(locals)
            val remainingSize = bodySize - (source.position - startAvailable)
            expressionReader.readExpression(source, remainingSize, expressionVisitor)

            if (bodySize != source.position - startAvailable) {
                throw ParserException("Binary offset at function exit not at expected location.")
            }

            expressionVisitor?.visitEnd()
        }

        codeVisitor?.visitEnd()
    }

    private enum class DataUse {
        NO_USE,
        USES_DATA,
        ;

        fun or(other: DataUse): DataUse = if (this == USES_DATA) this else other
    }
}
