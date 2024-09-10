package org.wasmium.wasm.binary

import kotlin.math.max

/** Default capacity of the buffer.  */
private const val DEFAULT_CAPACITY: Int = 64

public class ByteBuffer : BinaryWriter {

    /** Buffer of bytes.  */
    protected var data: ByteArray

    /** The number of bytes written to the buffer.  */
    public var length: Int = 0
        protected set

    /**
     * Create a [ByteBuffer].
     */
    public constructor() {
        this.data = ByteArray(DEFAULT_CAPACITY)
    }

    /**
     * Create a [ByteBuffer] with a given capacity.
     */
    public constructor(capacity: Int) {
        this.data = ByteArray(capacity)
    }

    /**
     * Get an array of bytes written.
     *
     * @return The array of bytes written.
     */
    public fun toByteArray(): ByteArray = data.copyOf(length)

    /**
     * Expands the length of the buffer to ensure the given capacity.
     *
     * @param size The capacity of the buffer.
     */
    private fun ensure(size: Int) {
        if (this.length + size > data.size) {
            // grow 50%
            val length1 = data.size + (data.size shr 1)
            val length2 = length + size

            val newData = ByteArray(max(length1.toDouble(), length2.toDouble()).toInt())
            data.copyInto(newData, 0, 0, data.size)
            data = newData
        }
    }

    /**
     * Write a byte to the buffer.
     *
     * @param byte The byte to write.
     */
    public override fun writeByte(byte: Byte) {
        val length = this.length

        ensure(1)

        data[length] = byte
        this.length += 1
    }

    /**
     * Write a subset of a byte array to the buffer.
     *
     * @param bytes The byte array.
     * @param offset The offset in the byte array.
     * @param size The number of bytes to write.
     */
    public override fun writeTo(bytes: ByteArray, offset: Int, size: Int) {
        ensure(size)

        bytes.copyInto(data, this.length, offset, offset + size)

        this.length += size
    }
}
