package org.wasmium.wasm.binary

import java.nio.ByteBuffer

public class ByteBufferBinaryReader(private val buffer: ByteBuffer) : BinaryReader {

    public override val position: UInt
        get() = buffer.position().toUInt()

    public override fun request(size: UInt): Boolean = true

    public override fun exhausted(): Boolean = buffer.hasRemaining()

    public override fun skip(size: UInt) {
        buffer.position(buffer.position() + size.toInt())
    }

    public override fun readByte(): Byte = buffer.get()

    public override fun readTo(bytes: ByteArray, offset: Int, size: Int): Unit {
        buffer.get(bytes, offset, size)
    }
}
