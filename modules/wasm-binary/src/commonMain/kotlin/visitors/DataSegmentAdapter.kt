package org.wasmium.wasm.binary.visitors

public open class DataSegmentAdapter(protected val delegate: DataSegmentVisitor? = null) : DataSegmentVisitor {

    public override fun visitActive(memoryIndex: UInt): InitializerExpressionVisitor = delegate?.visitActive(memoryIndex) ?: InitializerExpressionAdapter()

    public override fun visitData(data: ByteArray): Unit = delegate?.visitData(data) ?: Unit

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit
}
