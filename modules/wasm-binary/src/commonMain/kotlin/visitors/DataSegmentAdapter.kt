package org.wasmium.wasm.binary.visitors

public open class DataSegmentAdapter(protected val delegate: DataSegmentVisitor? = null) : DataSegmentVisitor {

    override fun visitActive(memoryIndex: UInt): ExpressionVisitor = ExpressionAdapter(delegate?.visitActive(memoryIndex))

    override fun visitData(data: ByteArray): Unit = delegate?.visitData(data) ?: Unit

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
