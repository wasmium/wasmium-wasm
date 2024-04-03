package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.toHexString
import org.wasmium.wasm.binary.tree.Opcode.END
import org.wasmium.wasm.binary.tree.Opcode.F32_CONST
import org.wasmium.wasm.binary.tree.Opcode.F64_CONST
import org.wasmium.wasm.binary.tree.Opcode.GET_GLOBAL
import org.wasmium.wasm.binary.tree.Opcode.I32_CONST
import org.wasmium.wasm.binary.tree.Opcode.I64_CONST
import org.wasmium.wasm.binary.tree.Opcode.V128_CONST
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class InitializerExpressionReader(
    private val context: ReaderContext,
) {
    public fun readInitExpression(source: WasmBinaryReader, visitor: InitializerExpressionVisitor?, requireUInt: Boolean) {
        var opcode = source.readOpcode()
        when (opcode) {
            I32_CONST -> {
                val value = source.readVarInt32()

                visitor?.visitInitExprI32ConstExpr(value)
            }

            I64_CONST -> {
                val value = source.readVarInt64()

                visitor?.visitInitExprI64ConstExpr(value)
            }

            F32_CONST -> {
                val value = source.readFloat32()

                visitor?.visitInitExprF32ConstExpr(value)
            }

            F64_CONST -> {
                val value = source.readFloat64()

                visitor?.visitInitExprF64ConstExpr(value)
            }

            V128_CONST -> {
                if (!context.options.features.isSIMDEnabled) {
                    throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                }

                val value = source.readV128()

                visitor?.visitInitExprV128ConstExpr(value)
            }

            GET_GLOBAL -> {
                val globaIndex = source.readIndex()

                visitor?.visitInitExprGetGlobalExpr(globaIndex)
            }

            END -> {
                visitor?.visitInitExprEnd()
                return
            }

            else -> throw ParserException("Unexpected opcode in initializer expression: %$opcode(0x${opcode.opcode.toHexString()})")
        }
        if (requireUInt && (opcode != I32_CONST) && (opcode != GET_GLOBAL)) {
            throw ParserException("Expected i32 init_expr")
        }

        opcode = source.readOpcode()
        if (opcode == END) {
            visitor?.visitInitExprEnd()
        } else {
            throw ParserException("Expected END opcode after initializer expression: %$opcode")
        }
    }
}
