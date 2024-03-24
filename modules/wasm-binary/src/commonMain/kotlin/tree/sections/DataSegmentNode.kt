package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.visitors.DataSegmentVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class DataSegmentNode : DataSegmentVisitor {
    public var mode: UInt? = null
    public var memoryIndex: UInt? = null
    public var segmentIndex: UInt? = null
    public var initializer: InitializerExpressionNode? = null
    public var data: ByteArray = ByteArray(0)

    public fun accept(dataSegmentVisitor: DataSegmentVisitor) {
        dataSegmentVisitor.visitMode(mode!!)

        when (mode) {
            0u -> {
                val initializerExpressionVisitor = dataSegmentVisitor.visitInitializerExpression()
                initializer?.accept(initializerExpressionVisitor)
                initializerExpressionVisitor.visitEnd()

                dataSegmentVisitor.visitData(data)
            }

            1u -> {
                dataSegmentVisitor.visitData(data)
            }

            2u -> {
                val initializerExpressionVisitor = dataSegmentVisitor.visitInitializerExpression()
                initializer?.accept(initializerExpressionVisitor)
                initializerExpressionVisitor.visitEnd()

                dataSegmentVisitor.visitMemoryData(memoryIndex!!, data)
            }
        }

        dataSegmentVisitor.visitEnd()
    }

    public override fun visitMode(mode: UInt) {
        this.mode = mode
    }

    public override fun visitMemoryData(memoryIndex: UInt, data: ByteArray) {
        this.memoryIndex = memoryIndex
        this.data = data
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
