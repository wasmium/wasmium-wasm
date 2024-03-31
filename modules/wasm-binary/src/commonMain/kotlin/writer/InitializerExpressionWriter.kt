package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class InitializerExpressionWriter(
    private val context: WriterContext,
    private val body: ByteBuffer,
) : InitializerExpressionVisitor {
    public override fun visitInitExprI32ConstExpr(value: Int) {
        WasmBinaryWriter(body).writeOpcode(Opcode.I32_CONST)
        WasmBinaryWriter(body).writeVarInt32(value)
    }

    public override fun visitInitExprI64ConstExpr(value: Long) {
        WasmBinaryWriter(body).writeOpcode(Opcode.I64_CONST)
        WasmBinaryWriter(body).writeVarInt64(value)
    }

    public override fun visitInitExprF32ConstExpr(value: Float) {
        WasmBinaryWriter(body).writeOpcode(Opcode.F32_CONST)
        WasmBinaryWriter(body).writeFloat32(value)
    }

    public override fun visitInitExprF64ConstExpr(value: Double) {
        WasmBinaryWriter(body).writeOpcode(Opcode.F64_CONST)
        WasmBinaryWriter(body).writeFloat64(value)
    }

    public override fun visitInitExprGetGlobalExpr(globalIndex: UInt) {
        WasmBinaryWriter(body).writeOpcode(Opcode.GET_GLOBAL)
        WasmBinaryWriter(body).writeIndex(globalIndex)
    }

    public override fun visitInitExprV128ConstExpr(value: V128Value) {
        WasmBinaryWriter(body).writeOpcode(Opcode.V128_CONST)
        WasmBinaryWriter(body).writeV128(value)
    }

    public override fun visitInitExprEnd() {
        WasmBinaryWriter(body).writeOpcode(Opcode.END)
    }

    public override fun visitEnd() {
        // empty
    }
}
