@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary

import kotlinx.io.Sink
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

public class WasmSink(public val sink: Sink) {

    public fun writeUInt8(value: UInt): Unit = sink.writeByte(value.toByte())

    public fun writeUInt32(value: UInt) {
        for (i in 0..3) {
            sink.writeByte((value shr (8 * i)).toByte())
        }
    }

    public fun writeUInt64(value: ULong) {
        for (i in 0..7) {
            sink.writeByte((value shr (8 * i)).toByte())
        }
    }

    public fun writeVarUInt1(value: UInt) {
        val b = (value and 0b1u)

        sink.writeByte(b.toByte())
    }

    public fun writeVarUInt7(value: UInt) {
        val b = (value and 0x7Fu)

        sink.writeByte(b.toByte())
    }

    public fun writeVarUInt7(value: Int) {
        val b = (value and 0x7F)

        sink.writeByte(b.toByte())
    }

    public fun writeVarUInt32(value: UInt, isCanonical: Boolean): Int {
        return if (isCanonical) {
            writeFixedVarUInt32(value)
        } else {
            writeVarUInt32(value)
        }
    }

    public fun writeFixedVarUInt32(value: UInt): Int {
        sink.writeByte(((value and 0x7fu).toInt() or 0x80).toByte())
        sink.writeByte((((value shr 7) and 0x7fu).toByte().toInt() or 0x80).toByte())
        sink.writeByte((((value shr 14) and 0x7fu).toByte().toInt() or 0x80).toByte())
        sink.writeByte((((value shr 21) and 0x7fu).toByte().toInt() or 0x80).toByte())
        sink.writeByte(((value shr 28).toByte().toInt() and 0x0f).toByte())

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

            sink.writeByte(byte)
            count++
        } while (remaining != 0u)

        return count
    }

    public fun writeVarInt32(value: Int): Int = writeVarInt64(value.toLong())

    public fun writeVarInt64(value: Long): Int {
        var reminder = value
        var count = 0
        var more = true
        while (more) {
            var byte = (reminder and 0x7FL).toByte()
            reminder = reminder shr 7

            if ((reminder == 0L && ((byte.toInt() and 0x40) == 0)) || (reminder == -1L && ((byte.toInt() and 0x40) == 0x40))) {
                more = false
            } else {
                byte = (byte.toInt() or 0x80).toByte()
            }

            sink.writeByte(byte)
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

    public fun writeResizableLimits(limits: ResizableLimits) {
        writeVarUInt32(limits.flags)
        writeVarUInt32(limits.initial)

        if (limits.maximum != null) {
            writeVarUInt32(limits.maximum)
        }
    }

    public fun writeOpcode(opcode: Opcode) {
        writeUInt8(opcode.opcode.toUInt())

        if (opcode.hasPrefix()) {
            writeUInt8(opcode.prefix.toUInt())
        }
    }

    public fun writeString(value: String) {
        if (value.length > WasmBinary.MAX_STRING_SIZE.toInt()) {
            throw ParserException("Size of string ${value.length} exceed the maximum of ${WasmBinary.MAX_STRING_SIZE}")
        }

        writeVarUInt32(value.length.toLong().toUInt())

        val bytes = value.toCharArray()

        for (c in bytes) {
            sink.writeByte(c.code.toByte())
        }
    }

    public fun writeNameKind(nameKind: NameKind): Unit = writeVarUInt7(nameKind.nameKindId)

    public fun writeLinkingKind(kind: LinkingKind): Unit = writeVarUInt7(kind.linkingKindId)

    public fun writeFloat32(value: Float) {
        val f = value.toBits()

        writeUInt32(f.toUInt())
    }

    public fun writeFloat64(value: Double) {
        val d = value.toBits()

        writeUInt64(d.toULong())
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

    public fun write(value: ByteArray) {
        sink.write(value)
    }

    public fun writeByteArray(value: ByteArray, offset: Int, length: Int) {
        sink.write(value, offset, length)
    }
}
