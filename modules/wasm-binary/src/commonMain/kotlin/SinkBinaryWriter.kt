package org.wasmium.wasm.binary

import kotlinx.io.Sink

public class SinkBinaryWriter(private val sink: Sink) : BinaryWriter {

    override fun writeByte(byte: Byte): Unit = sink.writeByte(byte)

    override fun writeTo(bytes: ByteArray, offset: Int, size: Int): Unit = sink.write(bytes, offset, size)
}
