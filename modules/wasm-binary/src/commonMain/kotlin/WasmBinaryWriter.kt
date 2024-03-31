@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.LinkingKind
import org.wasmium.wasm.binary.tree.NameKind
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.ResizableLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import kotlin.experimental.or


public class WasmBinaryWriter(public val writer: BinaryWriter) {

    public fun writeUInt8(value: UInt): Unit = writer.writeByte(value.toByte())

    public fun writeUInt32(value: UInt) {
        for (i in 0..3) {
            writer.writeByte((value shr (8 * i)).toByte())
        }
    }

    public fun writeUInt64(value: ULong) {
        for (i in 0..7) {
            writer.writeByte((value shr (8 * i) and 0xFFu).toByte())
        }
    }

    public fun writeVarUInt1(value: UInt) {
        val b = (value and 0b1u)

        writer.writeByte(b.toByte())
    }

    public fun writeVarUInt7(value: UInt) {
        val b = (value and 0x7Fu)

        writer.writeByte(b.toByte())
    }

    public fun writeVarUInt7(value: Int) {
        val b = (value and 0x7F)

        writer.writeByte(b.toByte())
    }

    public fun writeVarUInt32(value: UInt, isCanonical: Boolean): Int {
        return if (isCanonical) {
            writeFixedVarUInt32(value)
        } else {
            writeVarUInt32(value)
        }
    }

    public fun writeFixedVarUInt32(value: UInt): Int {
        writer.writeByte(((value and 0x7fu).toInt() or 0x80).toByte())
        writer.writeByte((((value shr 7) and 0x7fu).toByte().toInt() or 0x80).toByte())
        writer.writeByte((((value shr 14) and 0x7fu).toByte().toInt() or 0x80).toByte())
        writer.writeByte((((value shr 21) and 0x7fu).toByte().toInt() or 0x80).toByte())
        writer.writeByte(((value shr 28).toByte().toInt() and 0x0f).toByte())

        // write 5 bytes
        return 5
    }

    public fun writeVarUInt32(value: UInt): Int {
        var remaining = value
        var count = 0
        do {
            var byte = (remaining and 0x7fu).toByte()
            remaining = remaining shr 7

            if (remaining != 0u) {
                byte = byte or 0x80.toByte()
            }

            writer.writeByte(byte)
            count++
        } while (remaining != 0u)

        return count
    }

    public fun writeVarInt32(value: Int, isCanonical: Boolean = false, padding: Int = 5): Int {
        var reminder = value
        var count = 0
        var hasMore = true
        while (hasMore) {
            var byte = reminder and 0x7F
            reminder = reminder shr 7

            if ((reminder == 0 && ((byte and 0x40) == 0)) || (reminder == -1 && ((byte and 0x40) == 0x40))) {
                hasMore = false
            } else {
                byte = (byte or 0x80)
            }

            writer.writeByte(byte.toByte())
            count++
        }

        while (isCanonical && count < padding) {
            writer.writeByte(0x80.toByte())
            count++
        }

        return count
    }

    public fun writeVarInt64(value: Long, isCanonical: Boolean = false, padding: Int = 5): Int {
        var reminder = value
        var count = 0
        var hasMore = true
        while (hasMore) {
            var byte = (reminder and 0x7F)
            reminder = reminder ushr 7

            if ((reminder == 0L && ((byte and 0x40L) == 0L)) || (reminder == -1L && ((byte and 0x40L) == 0x40L))) {
                hasMore = false
            } else {
                byte = (byte or 0x80)
            }

            writer.writeByte(byte.toByte())
            count++
        }

//        while (isCanonical && count < padding) {
//            writer.writeByte(0x80.toByte())
//            count++
//        }

        return count
    }

    public fun writeVarint64_(value: Long) {
        @Suppress("NAME_SHADOWING")
        var value = value
        while (value and 0x7fL.inv() != 0L) {
            writer.writeByte(((value.toInt() and 0x7f).toLong() or 0x80L).toByte())
            value = value ushr 7
        }
        writer.writeByte(value.toByte())
    }

    public fun writeSectionKind(sectionKind: SectionKind): Unit = writeVarUInt7(sectionKind.sectionKindId)

    public fun writeExternalKind(externalKind: ExternalKind): Unit = writeVarUInt7(externalKind.externalKindId)

    public fun writeRelocationKind(kind: RelocationKind): Unit = writeVarUInt7(kind.relocationKindId)

    public fun writeType(type: WasmType): Unit = writeVarUInt7(type.wasmTypeId)

    public fun writeIndex(index: UInt): Unit {
        writeVarUInt32(index)
    }

    public fun writeResizableLimits(limits: ResizableLimits) {
        writeVarUInt32(limits.flags)
        writeVarUInt32(limits.initial)

        if (limits.maximum != null) {
            writeVarUInt32(limits.maximum)
        }
    }

    public fun writeOpcode(opcode: Opcode) {
        if (opcode.hasPrefix()) {
            writeUInt8(opcode.prefix.toUInt())
        }

        writeUInt8(opcode.opcode.toUInt())
    }

    public fun writeString(value: String) {
        if (value.length > WasmBinary.MAX_STRING_SIZE.toInt()) {
            throw ParserException("Size of string ${value.length} exceed the maximum of ${WasmBinary.MAX_STRING_SIZE}")
        }

        writeVarUInt32(value.length.toUInt())

        val chars = value.toCharArray()

        for (char in chars) {
            writer.writeByte(char.code.toByte())
        }
    }

    public fun writeNameKind(nameKind: NameKind): Unit = writeVarUInt7(nameKind.nameKindId)

    public fun writeLinkingKind(kind: LinkingKind): Unit = writeVarUInt7(kind.linkingKindId)

    public fun writeFloat32(value: Float) {
        val result = value.toRawBits().toUInt()

        writeUInt32(result)
    }

    public fun writeFloat64(value: Double) {
        val result = value.toRawBits().toULong().reverseBytes()

        writeUInt64(result)
    }

    public fun writeV128(value: V128Value) {
        for (i in 0..3) {
            writeUInt32(value.value[i])
        }
    }

    public fun sizeOfVarUInt(value: UInt): Int {
        var reminder = value
        var count = 0
        do {
            reminder = reminder shr 7

            count++
        } while (reminder != 0u)

        return count
    }

    public fun writeByteArray(byteArray: ByteArray) {
        writer.writeTo(byteArray, 0, byteArray.size)
    }

    private fun writeByteArray(byteArray: ByteArray, offset: Int, length: Int) {
        writer.writeTo(byteArray, offset, length)
    }

    public fun writeSection(sectionKind: SectionKind, canonical: Boolean, data: ByteArray) {
        writeSectionKind(sectionKind)

        writeVarUInt32(data.size.toUInt(), canonical)
        writeByteArray(data, 0, data.size)
    }
}
