package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class CodeSectionReader(
    private val context: ReaderContext,
    private val functionBodyReader: FunctionBodyReader = FunctionBodyReader(context)
) {
    public fun readCodeSection(source: WasmSource, payloadSize: UInt, visitor: ModuleVisitor) {
        if (!context.options.isSkipCodeSection) {
            source.skip(payloadSize)
            return
        }

        val codeSize = source.readVarUInt32()
        if (codeSize != context.numberFunctions) {
            throw ParserException("Invalid function section size: $codeSize must be equal to functionSize of: ${context.numberFunctions}")
        }

        val codeVisitor = visitor.visitCodeSection()

        for (index in 0u until codeSize) {
            val functionIndex = context.numberFunctionImports + index

            val functionBodyVisitor = codeVisitor.visitFunctionBody(functionIndex)

            val bodySize = source.readVarUInt32()
            if (bodySize == 0u) {
                throw ParserException("Empty function size")
            }

            if (bodySize > WasmBinary.MAX_FUNCTION_SIZE) {
                throw ParserException("Function body size $bodySize exceed the maximum of ${WasmBinary.MAX_FUNCTION_SIZE}")
            }

            val startAvailable = source.position

            var totalLocals = 0u
            val localsSize = source.readVarUInt32()

            if (localsSize > WasmBinary.MAX_FUNCTION_LOCALS) {
                throw ParserException("Number of function locals $localsSize exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS}")
            }

            for (localIndex in 0u until localsSize) {
                val numberLocalTypes = source.readVarUInt32()

                val localType = source.readType()
                if (!localType.isValueType()) {
                    throw ParserException("Invalid local type: %#$localType")
                }

                totalLocals += numberLocalTypes

                if (numberLocalTypes > WasmBinary.MAX_FUNCTION_LOCALS_TOTAL) {
                    throw ParserException("Number of total function locals $totalLocals exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS_TOTAL}")
                }

                functionBodyVisitor.visitLocalVariable(localIndex, numberLocalTypes, localType)
            }

            val remainingSize = bodySize - (source.position - startAvailable)
            functionBodyReader.readFunctionBody(source, remainingSize, functionBodyVisitor)

            if (bodySize != source.position - startAvailable) {
                throw ParserException("Binary offset at function exit not at expected location.")
            }

            functionBodyVisitor.visitEnd()
        }

        codeVisitor.visitEnd()
    }
}
