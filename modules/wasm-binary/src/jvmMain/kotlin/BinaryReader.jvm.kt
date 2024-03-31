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

public class ByteArrayBinaryReader(private val buffer: ByteArray) : BinaryReader {

    public override var position: UInt = 0u

    public override fun request(size: UInt): Boolean = true

    public override fun exhausted(): Boolean {
        return position < buffer.size.toUInt()
    }

    public override fun skip(size: UInt) {
        TODO()
    }

    public override fun readByte(): Byte {
        position += 1u

        return buffer[(position - 1u).toInt()]
    }

    public override fun readTo(bytes: ByteArray, offset: Int, size: Int): Unit {
        TODO()
    }
}
