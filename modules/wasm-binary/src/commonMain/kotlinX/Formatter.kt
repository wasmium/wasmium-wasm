package org.wasmium.wasm.binary

private fun String.padStartIfNeeded(padded: Boolean, padLength: Int) = if (padded) padStart(padLength, '0') else this

public fun UByte.toHexString(padded: Boolean = false): String = toString(16).padStartIfNeeded(padded, UByte.SIZE_BITS / 4)

public fun Byte.toHexString(padded: Boolean = false): String = toUByte().toHexString(padded)

public fun UShort.toHexString(padded: Boolean = false): String = toString(16).padStartIfNeeded(padded, UShort.SIZE_BITS / 4)

public fun Short.toHexString(padded: Boolean = false): String = toUShort().toHexString(padded)

public fun UInt.toHexString(padded: Boolean = false): String = toString(16).padStartIfNeeded(padded, UInt.SIZE_BITS / 4)

public fun Int.toHexString(padded: Boolean = false): String = toUInt().toHexString(padded)

public fun ULong.toHexString(padded: Boolean = false): String = toString(16).padStartIfNeeded(padded, ULong.SIZE_BITS / 4)

public fun Long.toHexString(padded: Boolean = false): String = toULong().toHexString(padded)
