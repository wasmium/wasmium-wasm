package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitors.DataSegmentVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class DataSegmentNode : DataSegmentVisitor {
    public var memoryIndex: UInt? = null
    public var initializer: InitializerExpressionNode? = null
    public var data: ByteArray? = null

    public fun accept(dataSegmentVisitor: DataSegmentVisitor) {
        val initializerExpressionVisitor = dataSegmentVisitor.visitActive(memoryIndex!!)
        initializer?.accept(initializerExpressionVisitor)

        dataSegmentVisitor.visitData(data!!)

        dataSegmentVisitor.visitEnd()
    }

    public override fun visitActive(memoryIndex: UInt): InitializerExpressionVisitor {
        return InitializerExpressionNode().also { initializer = it }
    }

    public override fun visitData(data: ByteArray) {
        this.data = data
    }

    public override fun visitEnd() {
        // empty
    }
}
