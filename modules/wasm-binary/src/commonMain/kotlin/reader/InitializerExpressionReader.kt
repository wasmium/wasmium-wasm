package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.toHexString
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class InitializerExpressionReader(
    private val context: ReaderContext,
) {
    public fun readInitExpression(source: WasmSource, visitor: InitializerExpressionVisitor?, requireUInt: Boolean) {
        var opcode = source.readOpcode()
        when (opcode) {
            Opcode.I32_CONST -> {
                val value: Int = source.readVarInt32()

                visitor?.visitInitExprI32ConstExpr(value)
            }

            Opcode.I64_CONST -> {
                val value: Long = source.readVarInt64()

                visitor?.visitInitExprI64ConstExpr(value)
            }

            Opcode.F32_CONST -> {
                val value: Float = source.readFloat32()

                visitor?.visitInitExprF32ConstExpr(value)
            }

            Opcode.F64_CONST -> {
                val value: Double = source.readFloat64()

                visitor?.visitInitExprF64ConstExpr(value)
            }

            Opcode.V128_CONST -> {
                if (!context.options.features.isSIMDEnabled) {
                    throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                }

                val value: V128Value = source.readV128()

                visitor?.visitInitExprV128ConstExpr(value)
            }

            Opcode.GET_GLOBAL -> {
                val globaIndex = source.readIndex()

                if (globaIndex > context.numberTotalGlobals) {
                    throw ParserException("get_global index of $globaIndex exceed the maximum of ${context.numberTotalGlobals}")
                }

                if (globaIndex > context.numberGlobalImports) {
                    throw ParserException("get_global index of $globaIndex exceed the number of globals of ${context.numberGlobalImports}")
                }

                visitor?.visitInitExprGetGlobalExpr(globaIndex)
            }

            Opcode.END -> {
                visitor?.visitInitExprEnd()
                return
            }

            else -> throw ParserException("Unexpected opcode in initializer expression: %$opcode(0x${opcode.opcode.toHexString()})")
        }
        if (requireUInt && (opcode != Opcode.I32_CONST) && (opcode != Opcode.GET_GLOBAL)) {
            throw ParserException("Expected i32 init_expr")
        }

        opcode = source.readOpcode()
        if (opcode == Opcode.END) {
            visitor?.visitInitExprEnd()
        } else {
            throw ParserException("Expected END opcode after initializer expression: %$opcode")
        }
    }
}
