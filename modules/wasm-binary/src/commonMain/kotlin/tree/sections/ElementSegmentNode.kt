package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitors.ElementSegmentVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class ElementSegmentNode : ElementSegmentVisitor {
    public var tableIndex: UInt? = null
    public var elementIndex: UInt? = null
    public var initializer: InitializerExpressionNode? = null
    public var functionIndexes: MutableList<FunctionIndexNode> = mutableListOf()

    public fun accept(elementSegmentVisitor: ElementSegmentVisitor) {
        elementSegmentVisitor.visitTableIndex(tableIndex!!)

        for (functionIndex in functionIndexes) {
            elementSegmentVisitor.visitFunctionIndex(functionIndex.index!!, functionIndex.functionIndex!!)
        }

        val initializerExpressionVisitor = elementSegmentVisitor.visitInitializerExpression()
        initializer?.accept(initializerExpressionVisitor)
        initializerExpressionVisitor.visitEnd()
    }

    override fun visitTableIndex(tableIndex: UInt) {
        this.tableIndex = tableIndex
    }

    public override fun visitInitializerExpression(): InitializerExpressionVisitor {
        return InitializerExpressionNode().also { initializer = it }
    }

    override fun visitFunctionIndex(index: UInt, functionIndex: UInt) {
        val functionIndexNode = FunctionIndexNode()
        functionIndexNode.index = index
        functionIndexNode.functionIndex = functionIndex
        functionIndexes.add(functionIndexNode)
    }

    public override fun visitEnd() {
        // empty
    }
}
