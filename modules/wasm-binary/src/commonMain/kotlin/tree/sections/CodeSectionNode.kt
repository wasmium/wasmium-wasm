package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.LocalVariable
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class CodeSectionNode : SectionNode(SectionKind.CODE), CodeSectionVisitor {
    public val codes: MutableList<CodeType> = mutableListOf()

    public fun accept(codeSectionVisitor: CodeSectionVisitor) {
        for (code in codes) {
            val expressionVisitor = codeSectionVisitor.visitCode(code.locals)
            code.expression.accept(expressionVisitor)
        }

        codeSectionVisitor.visitEnd()
    }

    public override fun visitCode(locals: List<LocalVariable>): ExpressionVisitor {
        val expressionNode = ExpressionNode()
        codes.add(CodeType(locals, expressionNode))

        return expressionNode
    }

    public override fun visitEnd() {
        // empty
    }
}
