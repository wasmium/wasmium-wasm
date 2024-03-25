package org.wasmium.wasm.binary

import kotlinx.io.Source
import kotlinx.io.readTo

public class SourceBinaryReader(private val source: Source) : BinaryReader {
    override var position: UInt = 0u
        private set

    override fun request(size: UInt): Boolean = source.request(size.toLong())

    override fun exhausted(): Boolean = source.exhausted()

    override fun skip(size: UInt): Unit {
        position += size

        return source.skip(size.toLong())
    }

    override fun readByte(): Byte {
        position++

        return source.readByte()
    }

    override fun readTo(bytes: ByteArray, offset: Int, size: Int): Unit {
        position += size.toUInt()

        return source.readTo(bytes, offset, size)
    }
}
