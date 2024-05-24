@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary

import org.wasmium.wasm.binary.tree.BlockType
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.LinkingKind
import org.wasmium.wasm.binary.tree.NameKind
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.GlobalType.Mutability
import kotlin.experimental.or

private const val LOW_7_BITS: Byte = 0x7F
private const val CONTINUATION_BIT = 0x80
private const val SIGN_BIT: Byte = 0x40

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
        val b = (value and LOW_7_BITS.toUInt())

        writer.writeByte(b.toByte())
    }

    public fun writeVarUInt7(value: Int) {
        val b = (value and LOW_7_BITS.toInt())

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
        writer.writeByte(((value and LOW_7_BITS.toUInt()).toInt() or CONTINUATION_BIT).toByte())
        writer.writeByte((((value shr 7) and LOW_7_BITS.toUInt()).toByte().toInt() or CONTINUATION_BIT).toByte())
        writer.writeByte((((value shr 14) and LOW_7_BITS.toUInt()).toByte().toInt() or CONTINUATION_BIT).toByte())
        writer.writeByte((((value shr 21) and LOW_7_BITS.toUInt()).toByte().toInt() or CONTINUATION_BIT).toByte())
        writer.writeByte(((value shr 28).toByte().toInt() and 0x0f).toByte())

        // write 5 bytes
        return 5
    }

    public fun writeVarUInt32(value: UInt): Int {
        var remaining = value
        var count = 0
        do {
            var byte = (remaining and LOW_7_BITS.toUInt()).toByte()
            remaining = remaining shr 7

            if (remaining != 0u) {
                byte = byte or CONTINUATION_BIT.toByte()
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
            var byte = reminder and LOW_7_BITS.toInt()
            reminder = reminder shr 7

            if ((reminder == 0 && ((byte and SIGN_BIT.toInt()) == 0)) || (reminder == -1 && ((byte and SIGN_BIT.toInt()) == SIGN_BIT.toInt()))) {
                hasMore = false
            } else {
                byte = (byte or CONTINUATION_BIT)
            }

            writer.writeByte(byte.toByte())
            count++
        }

        while (isCanonical && count < padding) {
            writer.writeByte(CONTINUATION_BIT.toByte())
            count++
        }

        return count
    }

    public fun writeVarInt64(value: Long): Int {
        var reminder = value
        var count = 0
        var hasMore = true
        while (hasMore) {
            var byte = (reminder and LOW_7_BITS.toLong())
            reminder = reminder ushr 7

            if ((reminder == 0L && ((byte and SIGN_BIT.toLong()) == 0L)) || (reminder == -1L && ((byte and SIGN_BIT.toLong()) == SIGN_BIT.toLong()))) {
                hasMore = false
            } else {
                byte = (byte or CONTINUATION_BIT.toLong())
            }

            writer.writeByte(byte.toByte())
            count++
        }

        return count
    }

    public fun writeSectionKind(sectionKind: SectionKind): Unit = writeVarUInt7(sectionKind.sectionKindId)

    public fun writeExternalKind(externalKind: ExternalKind): Unit = writeVarUInt7(externalKind.externalKindId)

    public fun writeRelocationKind(kind: RelocationKind): Unit = writeVarUInt7(kind.relocationKindId)

    public fun writeType(type: WasmType): Unit = writeVarUInt7(type.wasmTypeId)

    public fun writeIndex(index: UInt): Unit {
        writeVarUInt32(index)
    }

    public fun writeMemoryLimits(limits: MemoryLimits) {
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

    public fun writeBlockType(blockType: BlockType) {
        writeUInt8(blockType.value.toUInt())

        if (blockType.isFunctionType()) {
            writeVarInt32(blockType.value)
        }
    }

    public fun writeTagType(tagType: TagType) {
        writeUInt8(tagType.attribute.attributeId)
        writeIndex(tagType.index)
    }

    public fun writeByteArray(byteArray: ByteArray): Unit = writer.writeTo(byteArray, 0, byteArray.size)

    private fun writeByteArray(byteArray: ByteArray, offset: Int, length: Int) = writer.writeTo(byteArray, offset, length)

    public fun writeSection(sectionKind: SectionKind, data: ByteArray) {
        writeSectionKind(sectionKind)

        writeVarUInt32(data.size.toUInt())
        writeByteArray(data, 0, data.size)
    }

    public fun writeMutability(mutability: Mutability): Unit = writeVarUInt1(if (mutability == Mutability.IMMUTABLE) 0u else 1u)

}
