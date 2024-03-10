package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.GlobalVariableVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class GlobalVariableNode : GlobalVariableVisitor {
    public var globalIndex: UInt? = null
    public var globalType: GlobalTypeNode? = null
    public var initializer: InitializerExpressionNode? = null

    public fun accept(globalVariableVisitor: GlobalVariableVisitor) {
        globalVariableVisitor.visitGlobalVariable(globalType!!.contentType!!, globalType!!.isMutable)

        val initializerExpressionVisitor = globalVariableVisitor.visitInitializerExpression()
        initializer?.accept(initializerExpressionVisitor)
        initializerExpressionVisitor.visitEnd()
    }

    public override fun visitInitializerExpression(): InitializerExpressionVisitor {
        return InitializerExpressionNode().also { initializer = it }
    }

    public override fun visitGlobalVariable(type: WasmType, mutable: Boolean) {
        val globalType = GlobalTypeNode()
        globalType.contentType = type
        globalType.isMutable = mutable

        this.globalType = globalType
    }

    public override fun visitEnd() {
        // empty
    }
}
