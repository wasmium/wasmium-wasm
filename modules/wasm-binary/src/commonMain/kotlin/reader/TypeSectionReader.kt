package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class TypeSectionReader(
    private val context: ReaderContext,
) {
    public fun readTypeSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfSignatures = source.readVarUInt32()

        if (context.numberOfSignatures > WasmBinary.MAX_TYPES) {
            throw ParserException("Number of types ${context.numberOfSignatures} exceed the maximum of ${WasmBinary.MAX_TYPES}")
        }

        val typeVisitor = visitor.visitTypeSection()
        for (signatureIndex in 0u until context.numberOfSignatures) {
            val form = source.readType()

            // TODO allow other types
            if (form != WasmType.FUNC) {
                throw ParserException("Invalid signature form with type: $form")
            }

            val parameterCount = source.readVarUInt32()
            if (parameterCount > WasmBinary.MAX_FUNCTION_PARAMS) {
                throw ParserException("Number of function parameters $parameterCount exceed the maximum of ${WasmBinary.MAX_FUNCTION_PARAMS}")
            }

            val parameters = mutableListOf<WasmType>()
            for (paramIndex in 0u until parameterCount) {
                val type = source.readType()

                if (!type.isValueType()) {
                    throw ParserException("Expected valid param type but got: ${type.name}")
                }

                parameters.add(type)
            }

            val resultCount = source.readVarUInt1()
            if (resultCount > WasmBinary.MAX_FUNCTION_RESULTS) {
                throw ParserException("Number of function results $resultCount exceed the maximum of ${WasmBinary.MAX_FUNCTION_RESULTS}")
            }

            if (resultCount != 0u && resultCount != 1u) {
                throw ParserException("Result size must be 0 or 1 but got: $resultCount")
            }

            val resultType = mutableListOf<WasmType>()
            if (resultCount != 0u) {
                if (!context.options.features.isMultiValueEnabled && (resultCount > 1u)) {
                    throw ParserException("Function with multi-value result not allowed.")
                }

                for (typeIndex in 0 until resultCount.toInt()) {
                    val type = source.readType()

                    if (!type.isValueType()) {
                        throw ParserException("Expected valid param value type but got: ${type.name}")
                    }

                    resultType.add(type)
                }
            }

            typeVisitor?.visitType(parameters, resultType)
        }

        typeVisitor?.visitEnd()
    }
}
