@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary

import kotlinx.io.*
import org.wasmium.wasm.binary.tree.*
import kotlin.experimental.and

public class WasmSource(
    protected val source: Source
) {
    /** Current position in the source */
    public var position: UInt = 0u
        private set

    public fun skip(byteCount: UInt): Unit {
        position += byteCount

        source.skip(byteCount.toLong())
    }

    public fun close(): Unit = source.close()

    public fun readTo(bytes: ByteArray, startIndex: UInt, endIndex: UInt): UInt {
        source.readTo(bytes, startIndex.toInt(), endIndex.toInt())

        val readBytes = endIndex - startIndex

        position += readBytes

        return readBytes
    }

    public fun require(byteCount: UInt): Boolean = try {
        source.require(byteCount.toLong())

        true
    } catch (e: EOFException) {
        false
    }

    private fun Int.toUnsignedLong(): Long = toLong() and 0xFFFFFFFFL

    public fun exhausted(): Boolean = source.exhausted()

    /**
     * Fast read of fixed size unsigned byte.
     */
    public fun readUInt8(): UInt {
        position += 1u

        return (source.readByte().toInt() and 0xFF).toUInt()
    }

    /**
     * Fast read of fixed size unsigned int of 4 bytes.
     */
    public fun readUInt32(): UInt {
        var result = 0.toUInt()
        for (i in 0..3) {
            result = result or ((source.readByte().toInt() and 0xFF) shl (8 * i)).toUInt()
        }

        position += 4u

        return result
    }

    /**
     * Fast read of fixed size unsigned long of 8 bytes.
     */
    public fun readUInt64(): ULong {
        var result = 0.toULong()
        for (i in 0..7) {
            result = result or ((source.readByte().toInt() and 0xFF) shl (8 * i)).toULong()
        }

        position += 8u

        return result
    }

    public fun readVarUInt1(): UInt {
        position += 1u

        return (source.readByte() and 0b1).toUInt()
    }

    public fun readVarUInt7(): UInt {
        position += 1u

        return (source.readByte() and 0x7F).toUInt()
    }

    public fun readVarUInt32(): UInt = readUnsignedLeb128().toUInt()

    public fun readVarInt7(): Int = readVarInt32()

    public fun readVarInt32(): Int = readSignedLeb128().toInt()

    public fun readVarInt64(): Long = readSignedLeb128()

    protected fun readUnsignedLeb128(maxCount: Int = 5): Long {
        var result = 0L
        var current: Int
        var count = 0
        do {
            current = source.readByte().toInt() and 0xff
            result = result or ((current and 0x7f).toLong() shl (count * 7))
            count++
        } while (current and 0x80 == 0x80 && count <= maxCount)

        // overflow
        if (current and 0x80 == 0x80) {
            throw Exception("Overflow: Number too large")
        }

        // underflow
        if (current != 0 && count > (count * 8) / 7) {
            throw Exception("Underflow: Too many bytes for value")
        }

        position += count.toUInt()

        return result
    }

    protected fun readSignedLeb128(maxCount: Int = 10): Long {
        var result = 0L
        var current: Int
        var count = 0
        do {
            current = source.readByte().toInt() and 0xff
            result = result or ((current and 0x7f).toLong() shl (count * 7))
            count++
        } while (current and 0x80 == 0x80 && count <= maxCount)

        // overflow
        if (current and 0x80 == 0x80) {
            throw Exception("Overflow: Number too large")
        }

        // underflow
        if (current != 0 && count > (count * 8) / 7) {
            throw Exception("Underflow: Too many bytes for value")
        }

        // sign extend if appropriate
        if ((current and 0x40) != 0) {
            result = result or (-(1 shl (count * 7))).toUnsignedLong()
        }

        position += count.toUInt()

        return result
    }

    public fun readSectionKind(): SectionKind {
        val sectionKind = readVarUInt7()

        return SectionKind.fromSectionKindId(sectionKind)
    }

    public fun readExternalKind(): ExternalKind {
        val externalKind = readVarUInt7()

        return ExternalKind.fromExternalKindId(externalKind)
    }

    public fun readRelocationKind(): RelocationKind {
        val relocationKind = readVarUInt7()

        return RelocationKind.fromRelocationKind(relocationKind)
    }

    public fun readType(): WasmType {
        val type = readVarUInt7()

        return WasmType.fromWasmTypeId(type)
    }

    public fun readIndex(): UInt = readVarUInt32()

    public fun readResizableLimits(): ResizableLimits {
        val flags = readVarUInt32()
        val hasMaximum = (flags and LimitFlags.HAS_MAX.flags) != 0u
        val initialPages = readVarUInt32()

        return if (hasMaximum) {
            val maximum = readVarUInt32()

            ResizableLimits(initial = initialPages, maximum = maximum, flags = flags)
        } else {
            ResizableLimits(initial = initialPages, flags = flags)
        }
    }

    public fun readOpcode(): Opcode {
        val value = readUInt8()

        if (Opcode.isPrefix(value)) {
            val code = readVarUInt32()

            return Opcode.fromPrefix(value, code)
        } else {
            return Opcode.fromCode(value)
        }
    }

    public fun readString(): String {
        val length = readVarUInt32()

        if (length > WasmBinary.MAX_STRING_SIZE) {
            throw ParserException("Size of string $length exceed the maximum of ${WasmBinary.MAX_STRING_SIZE}")
        }

        val b = ByteArray(length.toInt())
        source.readTo(b, 0, length.toInt())

        position += length

        return b.decodeToString()
    }

    public fun readNameKind(): NameKind {
        val nameKindId = readVarUInt7()

        return NameKind.fromNameKindId(nameKindId)
    }

    public fun readLinkingKind(): LinkingKind {
        val linkingKindId = readVarUInt7()

        return LinkingKind.fromLinkingKindId(linkingKindId)
    }

    public fun readFloat32(): Float {
        val bits: Long = readUInt32().toLong()

        return Float.fromBits(bits.toInt())
    }

    public fun readFloat64(): Double {
        val bits: Int = readUInt64().toInt()

        return Double.fromBits(bits.toLong())
    }

    public fun readV128(): V128Value {
        val value: UIntArray = uintArrayOf(0u, 0u, 0u, 0u)

        for (i in 0..3) {
            value[i] = readUInt32()
        }

        return V128Value(value)
    }
}
