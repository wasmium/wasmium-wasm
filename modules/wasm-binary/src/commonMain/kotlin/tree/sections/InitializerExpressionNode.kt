package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.Opcode.END
import org.wasmium.wasm.binary.tree.Opcode.F32_CONST
import org.wasmium.wasm.binary.tree.Opcode.F64_CONST
import org.wasmium.wasm.binary.tree.Opcode.GET_GLOBAL
import org.wasmium.wasm.binary.tree.Opcode.I32_CONST
import org.wasmium.wasm.binary.tree.Opcode.I64_CONST
import org.wasmium.wasm.binary.tree.Opcode.V128_CONST
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.instructions.ConstFloat32Instruction
import org.wasmium.wasm.binary.tree.instructions.ConstFloat64Instruction
import org.wasmium.wasm.binary.tree.instructions.ConstInt32Instruction
import org.wasmium.wasm.binary.tree.instructions.ConstInt64Instruction
import org.wasmium.wasm.binary.tree.instructions.EndInstruction
import org.wasmium.wasm.binary.tree.instructions.GetGlobalInstruction
import org.wasmium.wasm.binary.tree.instructions.Instruction
import org.wasmium.wasm.binary.tree.instructions.SimdConstInstruction
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class InitializerExpressionNode : InitializerExpressionVisitor {
    public val instructions: MutableList<Instruction> = mutableListOf()

    public fun accept(initializerExpressionVisitor: InitializerExpressionVisitor) {
        for (instruction in instructions) {
            when (instruction.opcode) {
                I32_CONST -> {
                    val constInt32Instruction = instruction as ConstInt32Instruction
                    initializerExpressionVisitor.visitInitExprI32ConstExpr(constInt32Instruction.value)
                }

                I64_CONST -> {
                    val constInt64Instruction = instruction as ConstInt64Instruction
                    initializerExpressionVisitor.visitInitExprI64ConstExpr(constInt64Instruction.value)
                }

                F32_CONST -> {
                    val constFloat32Instruction = instruction as ConstFloat32Instruction
                    initializerExpressionVisitor.visitInitExprF32ConstExpr(constFloat32Instruction.value)
                }

                F64_CONST -> {
                    val constFloat64Instruction = instruction as ConstFloat64Instruction
                    initializerExpressionVisitor.visitInitExprF64ConstExpr(constFloat64Instruction.value)
                }

                V128_CONST -> {
                    val simdConstInstruction = instruction as SimdConstInstruction
                    initializerExpressionVisitor.visitInitExprV128ConstExpr(simdConstInstruction.value)
                }

                GET_GLOBAL -> {
                    val getGlobalInstruction = instruction as GetGlobalInstruction
                    initializerExpressionVisitor.visitInitExprGetGlobalExpr(getGlobalInstruction.index)
                }

                END -> initializerExpressionVisitor.visitInitExprEnd()

                else -> throw IllegalArgumentException()
            }
        }

        initializerExpressionVisitor.visitEnd()
    }

    public override fun visitInitExprI32ConstExpr(value: Int) {
        instructions.add(ConstInt32Instruction(value))
    }

    public override fun visitInitExprI64ConstExpr(value: Long) {
        instructions.add(ConstInt64Instruction(value))
    }

    public override fun visitInitExprF32ConstExpr(value: Float) {
        instructions.add(ConstFloat32Instruction(value))
    }

    public override fun visitInitExprF64ConstExpr(value: Double) {
        instructions.add(ConstFloat64Instruction(value))
    }

    public override fun visitInitExprGetGlobalExpr(globalIndex: UInt) {
        instructions.add(GetGlobalInstruction(globalIndex))
    }

    public override fun visitInitExprV128ConstExpr(value: V128Value) {
        instructions.add(SimdConstInstruction(value))
    }

    public override fun visitInitExprEnd() {
        instructions.add(EndInstruction())
    }

    public override fun visitEnd() {
        // empty
    }
}
