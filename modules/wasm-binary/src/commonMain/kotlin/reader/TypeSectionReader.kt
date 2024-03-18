package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ModuleVisitor
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionReader(
    private val context: WasmBinaryContext,
) {
    public fun readTypeSection(source: WasmSource, visitor: ModuleVisitor) {
        context.numberSignatures = source.readVarUInt32()

        if (context.numberSignatures > WasmBinary.MAX_TYPES) {
            throw ParserException("Number of types ${context.numberSignatures} exceed the maximum of ${WasmBinary.MAX_TYPES}")
        }

        val typeVisitor: TypeSectionVisitor = visitor.visitTypeSection()
        for (typeIndex in 0u until context.numberSignatures) {
            val form = source.readType()

            if (form != WasmType.FUNC) {
                throw ParserException("Invalid signature form with type: $form")
            }

            val parameterCount = source.readVarUInt32()
            if (parameterCount > WasmBinary.MAX_FUNCTION_PARAMS) {
                throw ParserException("Number of function parameters $parameterCount exceed the maximum of ${WasmBinary.MAX_FUNCTION_PARAMS}")
            }

            val parameters = Array(parameterCount.toInt()) { WasmType.NONE }
            for (paramIndex in 0u until parameterCount) {
                val type = source.readType()

                if (!type.isValueType()) {
                    throw ParserException("Expected valid param type but got: ${type.name}")
                }

                parameters[paramIndex.toInt()] = type
            }

            val resultCount = source.readVarUInt1()

            if (resultCount > WasmBinary.MAX_FUNCTION_RESULTS) {
                throw ParserException("Number of function results $resultCount exceed the maximum of ${WasmBinary.MAX_FUNCTION_RESULTS}")
            }

            if (resultCount != 0u && resultCount != 1u) {
                throw ParserException("Result size must be 0 or 1 but got: $resultCount")
            }

            var resultType: Array<WasmType>
            if (resultCount == 0u) {
                resultType = arrayOf()
            } else {
                if (!context.options.features.isMultiValueEnabled && (resultCount > 1u)) {
                    throw ParserException("Function with multi-value result not allowed.")
                }

                resultType = Array(resultCount.toInt()) { WasmType.NONE }
                for (rtype in 0 until resultCount.toInt()) {
                    val type: WasmType = source.readType()

                    if (!type.isValueType()) {
                        throw ParserException("Expected valid param value type but got: ${type.name}")
                    }

                    resultType[rtype] = type
                }
            }

            typeVisitor.visitType(typeIndex, parameters, resultType)
        }

        typeVisitor.visitEnd()
    }
}
