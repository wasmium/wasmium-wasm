package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitor.CodeSectionVisitor
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class CodeSectionValidator(private val delegate: CodeSectionVisitor? = null, private val context: ValidatorContext) : CodeSectionVisitor {
    private var numberOfCodes: UInt = 0u

    override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor {
        val functionType = context.functions.getOrElse(context.numberOfImportFunctions.toInt() + numberOfCodes.toInt()) {
            throw IllegalStateException("Function type not found")
        }

        val localInitials = mutableListOf<WasmType>()
        localInitials.addAll(functionType.parameters)
        for (local in locals) {
            for (i in 0u until local.count) {
                localInitials.add(local.type)
            }
        }

        val localContext = context.createLocalContext(localInitials, functionType.results)

        numberOfCodes++

        // TODO parameter functionType.results is already in the context
        return ExpressionValidator(delegate?.visitCode(locals), localContext, functionType.results)
    }

    override fun visitEnd() {
        delegate?.visitEnd()
    }
}
