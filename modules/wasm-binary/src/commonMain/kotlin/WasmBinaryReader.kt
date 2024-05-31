@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary

import org.wasmium.wasm.binary.tree.BlockType
import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.tree.GlobalType
import org.wasmium.wasm.binary.tree.GlobalType.Mutability
import org.wasmium.wasm.binary.tree.GlobalType.Mutability.IMMUTABLE
import org.wasmium.wasm.binary.tree.GlobalType.Mutability.MUTABLE
import org.wasmium.wasm.binary.tree.LimitFlags
import org.wasmium.wasm.binary.tree.LinkingKind
import org.wasmium.wasm.binary.tree.LinkingSymbolType
import org.wasmium.wasm.binary.tree.MemoryLimits
import org.wasmium.wasm.binary.tree.NameKind
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.RelocationKind
import org.wasmium.wasm.binary.tree.SectionKind
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.MemoryType
import org.wasmium.wasm.binary.tree.TableType
import kotlin.experimental.and

private const val LOW_7_BITS: Byte = 0x7F
private const val CONTINUATION_BIT = 0x80
private const val SIGN_BIT: Byte = 0x40

public class WasmBinaryReader(protected val reader: BinaryReader) {
    /** Current position in the source */
    public var position: UInt = 0u
        private set

    private fun consume(byteCount: UInt) {
        position += byteCount
    }

    public fun skip(byteCount: UInt): Unit = reader.skip(byteCount).also { consume(byteCount) }

    public fun readTo(bytes: ByteArray, startIndex: UInt, endIndex: UInt): UInt {
        reader.readTo(bytes, startIndex.toInt(), endIndex.toInt())

        return (endIndex - startIndex).also { consume(it) }
    }

    public fun request(byteCount: UInt): Boolean = reader.request(byteCount)

    private fun Int.toUnsignedLong(): Long = toLong() and 0xFFFFFFFFL

    public fun exhausted(): Boolean = reader.exhausted()

    public fun readUInt8(): UInt = (reader.readByte().toInt() and 0xFF).toUInt().also { consume(1u) }

    public fun readUInt32(): UInt {
        var result = 0.toUInt()
        for (i in 0..3) {
            result = result or ((reader.readByte().toUInt() and 0xFFu) shl (8 * i))
        }

        return result.also { consume(4u) }
    }

    public fun readUInt64(): ULong {
        var result = 0.toULong()
        for (i in 0..7) {
            result = result or ((reader.readByte().toULong() and 0xFFu) shl (8 * i))
        }

        return result.also { consume(8u) }
    }

    public fun readVarUInt1(): UInt = (reader.readByte() and 0b1).toUInt().also { consume(1u) }

    public fun readVarUInt7(): UInt = (reader.readByte() and LOW_7_BITS).toUInt().also { consume(1u) }

    public fun readVarUInt32(): UInt = readVarUIntX(5)

    private fun readVarUIntX(maxCount: Int): UInt {
        var result = 0u
        var current: Int
        var count = 0
        do {
            current = reader.readByte().toInt() and 0xff
            result = result or ((current and LOW_7_BITS.toInt()).toLong() shl (count * 7)).toUInt()
            count++
        } while (current and CONTINUATION_BIT == CONTINUATION_BIT && count <= maxCount)

        if (current and CONTINUATION_BIT == CONTINUATION_BIT) {
            throw ParserException("Overflow: Number too large")
        }

        if (current != 0 && count > (count * ULong.SIZE_BYTES) / 7) {
            throw ParserException("Underflow: Too many bytes for value")
        }

        return result.also { consume(count.toUInt()) }
    }

    public fun readVarInt32(initial: Long = 0): Int {
        val maxCount = 5
        var result = initial
        var current: Int
        var count = if (initial != 0L) 1 else 0
        do {
            current = reader.readByte().toInt() and 0xff
            result = result or ((current.toLong() and LOW_7_BITS.toLong()) shl (count * 7))
            count++
        } while (current and CONTINUATION_BIT == CONTINUATION_BIT && count <= maxCount)

        if (current and CONTINUATION_BIT == CONTINUATION_BIT) {
            throw ParserException("Overflow: Number too large")
        }

        if (current != 0 && count > (count * 8) / 7) {
            throw ParserException("Underflow: Too many bytes for value")
        }

        // sign extend if appropriate
        if ((current and SIGN_BIT.toInt()) != 0) {
            result = result or (-(1 shl (count * 7))).toUnsignedLong()
        }

        return result.toInt().also { consume(count.toUInt()) }
    }

    public fun readVarInt64(): Long {
        val maxCount = 10
        var result = 0L
        var current: Int
        var count = 0
        do {
            current = reader.readByte().toInt() and 0xff
            result = result or ((current.toLong() and LOW_7_BITS.toLong()) shl (count * 7))
            count++
        } while (current and CONTINUATION_BIT == CONTINUATION_BIT && count <= maxCount)

        if (current and CONTINUATION_BIT == CONTINUATION_BIT) {
            throw ParserException("Overflow: Number too large")
        }

        if (current != 0 && count > (count * 8) / 7) {
            throw ParserException("Underflow: Too many bytes for value")
        }

        // sign extend if appropriate
        val size = 64
        if (count * 7 < size && (current and SIGN_BIT.toInt()) != 0) {
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

    public fun readWasmRefType(): WasmType {
        val wasmTypeId = readVarUInt7()

        val wasmType = WasmType.fromWasmTypeId(wasmTypeId)
        if (wasmType != WasmType.FUNC_REF && wasmType != WasmType.EXTERN_REF) {
            throw ParserException("Invalid wasm reference type 0x${wasmTypeId.toHexString()}")
        }
        return wasmType
    }

    public fun readIndex(): UInt = readVarUInt32()

    public fun readMemoryType(): MemoryType {
        val limits = readMemoryLimits()

        if (limits.isShared() && (limits.maximum == null)) {
            throw ParserException("Shared memory must have a max size")
        }

        if (limits.maximum != null) {
            if (limits.initial > limits.maximum) {
                throw ParserException("Initial memory size greater than maximum")
            }
        }

        return MemoryType(limits)
    }

    public fun readMemoryLimits(): MemoryLimits {
        val flags = readVarUInt32()
        val initialPages = readVarUInt32()

        val hasMaximum = (flags and LimitFlags.HAS_MAX.flag) != 0u
        return if (hasMaximum) {
            val maximum = readVarUInt32()

            MemoryLimits(initial = initialPages, maximum = maximum, flags = flags)
        } else {
            MemoryLimits(initial = initialPages, flags = flags)
        }
    }

    public fun readOpcode(): Opcode {
        val value = readUInt8()

        return if (Opcode.isPrefix(value.toInt())) {
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
        reader.readTo(buffer, 0, length.toInt())

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
        val bits = readUInt64().toLong().reverseBytes()

        return Double.fromBits(bits)
    }

    public fun readV128(): V128Value {
        val value = uintArrayOf(0u, 0u, 0u, 0u)

        for (i in 0..3) {
            value[i] = readUInt32()
        }

        return V128Value(value)
    }

    /**
     * The block type can be 0x40, a value-type or 32 integer index
     */
    public fun readBlockType(): BlockType {
        val value = readUInt8()
        val valueType = WasmType.fromWasmTypeId(value)

        return if (valueType != null) {
            return BlockType(BlockType.BlockTypeKind.VALUE_TYPE, value.toInt())
        } else {
            val index = readVarInt32(value.toLong())

            return BlockType(BlockType.BlockTypeKind.FUNCTION_TYPE, index)
        }
    }

    public fun readTagType(): TagType {
        val tagAttributeId = readUInt8()
        val tagAttribute = TagType.TagAttribute.fromTagAttributeId(tagAttributeId) ?: throw ParserException("Invalid tag attribute $tagAttributeId")

        val tagIndex = readIndex()

        return TagType(tagAttribute, tagIndex)
    }

    public fun readGlobalType(): GlobalType {
        val type = readType()
        if (!type.isValueType()) {
            throw ParserException("Invalid global type: %#$type")
        }

        val mutable = if (readVarUInt1() == 0u) IMMUTABLE else MUTABLE

        return GlobalType(type, mutable)
    }

    public fun readTableType(): TableType {
        val elementType = readType()

        if (!elementType.isReferenceType()) {
            throw ParserException("Imported table type is not a reference type.")
        }

        val limits = readMemoryLimits()
        if (limits.isShared()) {
            throw ParserException("Tables may not be shared")
        }

        return TableType(elementType, limits)
    }

    public fun readFunctionType(): FunctionType {
        val form = readType()
        if (form != WasmType.FUNC) {
            throw ParserException("Invalid signature form with type: $form")
        }

        val parameters = readValueTypes()

        val resultTypes = readValueTypes()
        if (resultTypes.isNotEmpty() && resultTypes.size != 1) {
            throw ParserException("Result size must be 0 or 1 but got: ${resultTypes.size}")
        }

        return FunctionType(parameters, resultTypes)
    }

    public fun readValueTypes(): List<WasmType> {
        val count = readVarUInt32()

        val resultType = mutableListOf<WasmType>()
        (0 until count.toInt()).forEach { _ ->
            val type = readType()

            if (!type.isValueType()) {
                throw ParserException("Expected valid param value type but got: ${type.name}")
            }

            resultType.add(type)
        }

        return resultType
    }

    public fun readMutability(): Mutability = if (readVarUInt1() == 0u) IMMUTABLE else MUTABLE
}

public fun Int.reverseBytes(): Int {
    return ((this and 0xFF) shl 24) or
        ((this and 0xFF00) shl 8) or
        ((this and 0xFF0000) ushr 8) or
        ((this shr 24) and 0xff)
}

public fun Long.reverseBytes(): Long {
    return ((this and 0xFF).toLong() shl 56) or
        ((this and 0xFF00).toLong() shl 40) or
        ((this and 0xFF0000).toLong() shl 24) or
        ((this and 0xFF000000).toLong() shl 8) or
        ((this and 0xFF00000000).toLong() ushr 8) or
        ((this and 0xFF0000000000).toLong() ushr 24) or
        ((this and 0xFF000000000000).toLong() ushr 40) or
        ((this.toULong() and 0xFF00000000000000u).toLong() ushr 56)
}

public fun UInt.reverseBytes(): UInt {
    return ((this and 0xFFu) shl 24) or
        ((this and 0xFF00u) shl 8) or
        ((this and 0xFF0000u) shr 8) or
        ((this shr 24) and 0xFFu)
}

public fun ULong.reverseBytes(): ULong {
    return ((this and 0xFFu) shl 56) or
        ((this and 0xFF00u) shl 40) or
        ((this and 0xFF0000u) shl 24) or
        ((this and 0xFF000000u) shl 8) or
        ((this and 0xFF00000000u) shr 8) or
        ((this and 0xFF0000000000u) shr 24) or
        ((this and 0xFF000000000000u) shr 40) or
        ((this shr 56) and 0xFFu)
}
