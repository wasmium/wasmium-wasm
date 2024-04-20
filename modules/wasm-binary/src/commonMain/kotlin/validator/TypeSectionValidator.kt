package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.sections.FunctionType
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor

public class TypeSectionValidator(private val delegate: TypeSectionVisitor, private val context: ValidatorContext) : TypeSectionVisitor {
    override fun visitType(parameters: List<WasmType>, results: List<WasmType>) {
        checkValueTypes(parameters)
        checkValueTypes(results)

        context.types.add(FunctionType(parameters, results))

        delegate.visitType(parameters, results)
    }

    override fun visitEnd() {
        delegate.visitEnd()
    }

    private fun checkValueTypes(types: List<WasmType>) {
        for (type in types) {
            if (!type.isValueType()) {
                throw ParserException("Type $type is not value type")
            }
        }
    }
}
