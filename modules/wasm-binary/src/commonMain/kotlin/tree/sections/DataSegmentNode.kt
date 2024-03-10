package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitors.DataSegmentVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class DataSegmentNode : DataSegmentVisitor {
    public var memoryIndex: UInt? = null
    public var segmentIndex: UInt? = null
    public var initializer: InitializerExpressionNode? = null
    public var data: ByteArray = ByteArray(0)

    public fun accept(dataSegmentVisitor: DataSegmentVisitor) {
        dataSegmentVisitor.visitData(data)
        dataSegmentVisitor.visitMemoryIndex(memoryIndex!!)

        val initializerExpressionVisitor = dataSegmentVisitor.visitInitializerExpression()
        initializer?.accept(initializerExpressionVisitor)
        initializerExpressionVisitor.visitEnd()
    }

    override fun visitMemoryIndex(memoryIndex: UInt) {
        this.memoryIndex = memoryIndex
    }

    public override fun visitInitializerExpression(): InitializerExpressionVisitor {
        return InitializerExpressionNode().also { initializer = it }
    }

    public override fun visitData(data: ByteArray) {
        this.data = data
    }

    public override fun visitEnd() {
        // empty
    }
}
