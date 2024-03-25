package org.wasmium.wasm.binary

public interface BinaryWriter {

    public fun writeByte(byte: Byte)

    public fun writeTo(bytes: ByteArray, offset: Int, size: Int)
}
