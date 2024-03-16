@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary

import kotlinx.io.EOFException
import kotlinx.io.Source
import kotlinx.io.readTo
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.LimitFlags
import org.wasmium.wasm.binary.tree.LinkingKind
import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.tree.NameKind
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import kotlin.experimental.and

public class WasmSource(
    protected val source: Source
) {
    /** Current position in the source */
    public var position: UInt = 0u
        private set

    private fun consume(byteCount: UInt) {
        position += byteCount
    }

    public fun skip(byteCount: UInt): Unit = source.skip(byteCount.toLong()).also { consume(byteCount) }

    public fun close(): Unit = source.close()

    public fun readTo(bytes: ByteArray, startIndex: UInt, endIndex: UInt): UInt {
        source.readTo(bytes, startIndex.toInt(), endIndex.toInt())

        return (endIndex - startIndex).also { consume(it) }
    }

    public fun require(byteCount: UInt): Boolean = try {
        source.require(byteCount.toLong())

        true
    } catch (e: EOFException) {
        false
    }

    private fun Int.toUnsignedLong(): Long = toLong() and 0xFFFFFFFFL

    public fun exhausted(): Boolean = source.exhausted()

    public fun readUInt8(): UInt = (source.readByte().toInt() and 0xFF).toUInt().also { consume(1u) }

    public fun readUInt32(): UInt {
        var result = 0.toUInt()
        for (i in 0..3) {
            result = result or ((source.readByte().toInt() and 0xFF) shl (8 * i)).toUInt()
        }

        return result.also { consume(4u) }
    }

    public fun readUInt64(): ULong {
        var result = 0.toULong()
        for (i in 0..7) {
            result = result or ((source.readByte().toInt() and 0xFF) shl (8 * i)).toULong()
        }

        return result.also { consume(8u) }
    }

    public fun readVarUInt1(): UInt = (source.readByte() and 0b1).toUInt().also { consume(1u) }

    public fun readVarUInt7(): UInt = (source.readByte() and 0x7F).toUInt().also { consume(1u) }

    public fun readVarInt7(): Int = readVarInt32()

    public fun readVarUInt32(maxCount: Int = 5): UInt {
        var result = 0u
        var current: Int
        var count = 0
        do {
            current = source.readByte().toInt() and 0xff
            result = result or ((current and 0x7f).toLong() shl (count * 7)).toUInt()
            count++
        } while (current and 0x80 == 0x80 && count <= maxCount)

        if (current and 0x80 == 0x80) {
            throw ParserException("Overflow: Number too large")
        }

        if (current != 0 && count > (count * 8) / 7) {
            throw ParserException("Underflow: Too many bytes for value")
        }

        return result.also { consume(count.toUInt()) }
    }

    public fun readVarInt32(maxCount: Int = 10): Int {
        var result = 0L
        var current: Int
        var count = 0
        do {
            current = source.readByte().toInt() and 0xff
            result = result or ((current and 0x7f).toLong() shl (count * 7))
            count++
        } while (current and 0x80 == 0x80 && count <= maxCount)

        if (current and 0x80 == 0x80) {
            throw Exception("Overflow: Number too large")
        }

        if (current != 0 && count > (count * 8) / 7) {
            throw Exception("Underflow: Too many bytes for value")
        }

        // sign extend if appropriate
        if ((current and 0x40) != 0) {
            result = result or (-(1 shl (count * 7))).toUnsignedLong()
        }

        return result.toInt().also { consume(count.toUInt()) }
    }

    public fun readVarInt64(maxCount: Int = 10): Long {
        var result = 0L
        var current: Int
        var count = 0

        do {
            current = source.readByte().toInt() and 0xff
            result = result or ((current and 0x7f).toLong() shl (count * 7))
            count++
        } while (current and 0x80 == 0x80 && count <= maxCount)

        if (current and 0x80 == 0x80) {
            throw Exception("Overflow: Number too large")
        }

        if (current != 0 && count > (count * 8) / 7) {
            throw Exception("Underflow: Too many bytes for value")
        }

        // sign extend if appropriate
        val size = 64
        if (count * 7 < size && (current and 0x40) != 0) {
            result = result or (-(1 shl (count * 7))).toUnsignedLong()
        }

        return result.also { consume(count.toUInt()) }
    }

    public fun readSectionKind(): SectionKind {
        val sectionKindId = readVarUInt7()

        val sectionKind = SectionKind.fromSectionKindId(sectionKindId)
        return sectionKind ?: throw ParserException("Invalid section kind $sectionKindId")
    }

    public fun readExternalKind(): ExternalKind {
        val externalKindId = readVarUInt7()

        val externalKind = ExternalKind.fromExternalKindId(externalKindId)
        return externalKind ?: throw ParserException("Invalid external kind $externalKindId")
    }

    public fun readRelocationKind(): RelocationKind {
        val relocationKindId = readVarUInt7()

        val relocationKind = RelocationKind.fromRelocationKind(relocationKindId)
        return relocationKind ?: throw ParserException("Invalid relocation kind $relocationKindId")
    }

    public fun readType(): WasmType {
        val wasmTypeId = readVarUInt7()

        val wasmType = WasmType.fromWasmTypeId(wasmTypeId)
        return wasmType ?: throw ParserException("Invalid wasm type 0x${wasmTypeId.toHexString()}")
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

        return if (Opcode.isPrefix(value)) {
            val code = readVarUInt32()

            val opcode = Opcode.fromPrefix(value, code)
            opcode ?: throw ParserException("Invalid opcode prefix $value")
        } else {
            val opcode = Opcode.fromCode(value)
            opcode ?: throw ParserException("Invalid opcode 0x${value.toHexString()}")
        }
    }

    public fun readString(): String {
        val length = readVarUInt32()

        if (length > WasmBinary.MAX_STRING_SIZE) {
            throw ParserException("Size of string $length exceed the maximum of ${WasmBinary.MAX_STRING_SIZE}")
        }

        val buffer = ByteArray(length.toInt())
        // TODO Change the exception to be raised here
        source.readTo(buffer, 0, length.toInt())

        val result = buffer.decodeToString()
        return result.also { consume(length) }
    }

    public fun readNameKind(): NameKind {
        val nameKindId = readVarUInt7()

        val nameKind = NameKind.fromNameKindId(nameKindId)
        return nameKind ?: throw ParserException("Invalid name kind $nameKindId")
    }

    public fun readLinkingKind(): LinkingKind {
        val linkingKindId = readVarUInt7()

        val linkingKind = LinkingKind.fromLinkingKindId(linkingKindId)
        return linkingKind ?: throw ParserException("Invalid linking kind $linkingKindId")
    }

    public fun readLinkingSymbolType(): LinkingSymbolType {
        val linkingSymbolTypeId = readVarUInt7()

        val linkingSymbolType = LinkingSymbolType.fromLinkingSymbolTypeId(linkingSymbolTypeId)
        return linkingSymbolType ?: throw ParserException("Invalid linking symbol type $linkingSymbolTypeId")
    }

    public fun readFloat32(): Float {
        val bits = readUInt32().toInt()

        return Float.fromBits(bits)
    }

    public fun readFloat64(): Double {
        val bits = readUInt64().toLong()

        return Double.fromBits(bits)
    }

    public fun readV128(): V128Value {
        val value = uintArrayOf(0u, 0u, 0u, 0u)

        for (i in 0..3) {
            value[i] = readUInt32()
        }

        return V128Value(value)
    }
}
