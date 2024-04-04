package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitors.DataSegmentVisitor
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class DataSegmentNode : DataSegmentVisitor {
    public var memoryIndex: UInt? = null
    public var initializer: ExpressionNode? = null
    public var data: ByteArray? = null

    public fun accept(dataSegmentVisitor: DataSegmentVisitor) {
        if(memoryIndex != null) {
            val expressionVisitor = dataSegmentVisitor.visitActive(memoryIndex!!)
            initializer?.accept(expressionVisitor)
        }

        dataSegmentVisitor.visitData(data!!)

        dataSegmentVisitor.visitEnd()
    }

    public override fun visitActive(memoryIndex: UInt): ExpressionVisitor {
        return ExpressionNode().also { initializer = it }
    }

    public override fun visitData(data: ByteArray) {
        this.data = data
    }

    public override fun visitEnd() {
        // empty
    }
}
