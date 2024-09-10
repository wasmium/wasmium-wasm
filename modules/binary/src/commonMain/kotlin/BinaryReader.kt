package org.wasmium.wasm.binary

public interface BinaryReader {
    public val position: UInt

    public fun request(size: UInt): Boolean

    public fun exhausted(): Boolean

    public fun skip(size: UInt)

    public fun readByte(): Byte

    public fun readTo(bytes: ByteArray, offset: Int, size: Int)
}
