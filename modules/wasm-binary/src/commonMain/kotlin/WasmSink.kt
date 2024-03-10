package org.wasmium.wasm.binary

import kotlinx.io.Sink
import kotlin.experimental.and
import kotlin.experimental.or

public class WasmSink(protected val sink: Sink) {

    public fun writeUInt8(value: Int): Unit = sink.writeByte(value.toByte())

    public fun writeUInt(value: Long) {
        for (i in 0..3) {
            sink.writeByte((value shr (8 * i)).toByte())
        }
    }

    public fun writeUInt64(value: Long) {
        for (i in 0..7) {
            sink.writeByte((value shr (8 * i)).toByte())
        }
    }

    public fun writeUnsignedLeb128(value: Long): Int {
        var remaining = value
        var count = 0
        do {
            var byte = (remaining and 0x7f).toByte()
            remaining = remaining ushr 7
            if (remaining != 0L) {
                byte = byte or 0x80.toByte()
            }
            sink.writeByte(byte)
            count++
        } while (remaining != 0L)

        return count
    }

    public fun writeSignedLeb128(value: Long): Int {
        var remaining = value
        var count = 0
        do {
            var byte = (remaining and 0x7f).toByte()

            remaining = remaining ushr 7

            val hasMore = !(remaining == 0L && ((byte and 0x40).toInt() == 0)) || (remaining == -1L && ((byte and 0x40).toInt() == 0x40))
            if (hasMore) {
                byte = byte or 0x80.toByte()
            }

            sink.writeByte(byte)

            count++
        } while (hasMore)

        return count
    }

}
