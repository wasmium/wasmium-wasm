package org.wasmium.wasm.binary

import java.nio.ByteBuffer

public class ByteBufferBinaryWriter(private val buffer: ByteBuffer) : BinaryWriter {

    public override fun writeByte(byte: Byte) {
        buffer.put(byte)
    }

    public override fun writeTo(bytes: ByteArray, offset: Int, size: Int) {
        buffer.put(bytes, offset, size)
    }
}
