package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class TypeSectionReader(
    private val context: ReaderContext,
) {
    public fun readTypeSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        context.numberOfTypes = source.readVarUInt32()

        val typeVisitor = visitor.visitTypeSection()
        for (typeIndex in 0u until context.numberOfTypes) {
            val form = source.readType()
            if (form != WasmType.FUNC) {
                throw ParserException("Invalid signature form with type: $form")
            }

            val parameterCount = source.readVarUInt32()

            val parameters = mutableListOf<WasmType>()
            for (paramIndex in 0u until parameterCount) {
                val type = source.readType()

                if (!type.isValueType()) {
                    throw ParserException("Expected valid param type but got: ${type.name}")
                }

                parameters.add(type)
            }

            val resultCount = source.readVarUInt1()
            if (resultCount != 0u && resultCount != 1u) {
                throw ParserException("Result size must be 0 or 1 but got: $resultCount")
            }

            val resultType = mutableListOf<WasmType>()
            if (resultCount != 0u) {
                if (!context.options.features.isMultiValueEnabled && (resultCount > 1u)) {
                    throw ParserException("Function with multi-value result not allowed.")
                }

                for (index in 0 until resultCount.toInt()) {
                    val type = source.readType()

                    if (!type.isValueType()) {
                        throw ParserException("Expected valid param value type but got: ${type.name}")
                    }

                    resultType.add(type)
                }
            }

            typeVisitor.visitType(parameters, resultType)
        }

        typeVisitor.visitEnd()
    }
}
