package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType

public interface FunctionBodyVisitor {
    public fun visitLocalVariable(localIndex: UInt, count: UInt, localType: WasmType)

    public fun visitCode()

    public fun visitEnd()

    public fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitAtomicRmwInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitBrTableInstruction(targets: Array<UInt>, defaultTarget: UInt)

    public fun visitCompareInstruction(opcode: Opcode)

    public fun visitConvertInstruction(opcode: Opcode)

    public fun visitEndInstruction()

    public fun visitEndFunctionInstruction()

    public fun visitConstFloat32Instruction(value: Float)

    public fun visitConstFloat64Instruction(value: Double)

    public fun visitConstInt32Instruction(value: Int)

    public fun visitConstInt64Instruction(value: Long)

    public fun visitSimdConstInstruction(value: V128Value)

    public fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value)

    public fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt)

    public fun visitWrapInstruction(opcode: Opcode)

    public fun visitExtendInstruction(opcode: Opcode)

    public fun visitDemoteInstruction(opcode: Opcode)

    public fun visitPromoteInstruction(opcode: Opcode)

    public fun visitReinterpretInstruction(opcode: Opcode)

    public fun visitUnreachableInstruction()

    public fun visitNopInstruction()

    public fun visitIfInstruction(types: Array<WasmType>)

    public fun visitLoopInstruction(types: Array<WasmType>)

    public fun visitBlockInstruction(types: Array<WasmType>)

    public fun visitElseInstruction()

    public fun visitTryInstruction(types: Array<WasmType>)

    public fun visitCatchInstruction()

    public fun visitThrowRefInstruction()

    public fun visitThrowInstruction(exceptionIndex: UInt)

    public fun visitRethrowInstruction()

    public fun visitBrInstruction(depth: UInt)

    public fun visitBrIfInstruction(depth: UInt)

    public fun visitReturnInstruction()

    public fun visitCallInstruction(functionIndex: UInt)

    public fun visitCallIndirectInstruction(signatureIndex: UInt, reserved: Boolean)

    public fun visitDropInstruction()

    public fun visitSelectInstruction()

    public fun visitGetGlobalInstruction(globalIndex: UInt)

    public fun visitSetLocalInstruction(localIndex: UInt)

    public fun visitTeeLocalInstruction(localIndex: UInt)

    public fun visitGetLocalInstruction(localIndex: UInt)

    public fun visitSetGlobalInstruction(globalIndex: UInt)

    public fun visitMemorySizeInstruction(reserved: Boolean)

    public fun visitMemoryGrowInstruction(reserved: Boolean)

    public fun visitEqualZeroInstruction(opcode: Opcode)

    public fun visitEqualInstruction(opcode: Opcode)

    public fun visitNotEqualInstruction(opcode: Opcode)

    public fun visitLessThanInstruction(opcode: Opcode)

    public fun visitLessEqualInstruction(opcode: Opcode)

    public fun visitGreaterThanInstruction(opcode: Opcode)

    public fun visitGreaterEqualInstruction(opcode: Opcode)

    public fun visitCountLeadingZerosInstruction(opcode: Opcode)

    public fun visitCountTrailingZerosInstruction(opcode: Opcode)

    public fun visitPopulationCountInstruction(opcode: Opcode)

    public fun visitAddInstruction(opcode: Opcode)

    public fun visitSubtractInstruction(opcode: Opcode)

    public fun visitMultiplyInstruction(opcode: Opcode)

    public fun visitDivideInstruction(opcode: Opcode)

    public fun visitRemainderInstruction(opcode: Opcode)

    public fun visitAndInstruction(opcode: Opcode)

    public fun visitOrInstruction(opcode: Opcode)

    public fun visitSimdXorInstruction(opcode: Opcode)

    public fun visitShiftLeftInstruction(opcode: Opcode)

    public fun visitRotateLeftInstruction(opcode: Opcode)

    public fun visitRotateRightInstruction(opcode: Opcode)

    public fun visitAbsoluteInstruction(opcode: Opcode)

    public fun visitNegativeInstruction(opcode: Opcode)

    public fun visitCeilingInstruction(opcode: Opcode)

    public fun visitFloorInstruction(opcode: Opcode)

    public fun visitTruncateInstruction(opcode: Opcode)

    public fun visitNearestInstruction(opcode: Opcode)

    public fun visitSqrtInstruction(opcode: Opcode)

    public fun visitMinInstruction(opcode: Opcode)

    public fun visitMaxInstruction(opcode: Opcode)

    public fun visitAtomicRmwAddInstruction(opcode: Opcode, align: UInt, offset: UInt)

    public fun visitAtomicRmwSubtractInstruction(opcode: Opcode, align: UInt, offset: UInt)

    public fun visitAtomicRmwAndInstruction(opcode: Opcode, align: UInt, offset: UInt)

    public fun visitAtomicRmwOrInstruction(opcode: Opcode, align: UInt, offset: UInt)

    public fun visitAtomicRmwXorInstruction(opcode: Opcode, align: UInt, offset: UInt)

    public fun visitAtomicRmwExchangeInstruction(opcode: Opcode, align: UInt, offset: UInt)

    public fun visitSimdSplatInstruction(opcode: Opcode, value: UInt)

    public fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt)

    public fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt)

    public fun visitSimdAddInstruction(opcode: Opcode)

    public fun visitSimdSubtractInstruction(opcode: Opcode)

    public fun visitSimdMultiplyInstruction(opcode: Opcode)

    public fun visitSimdNegativeInstruction(opcode: Opcode)

    public fun visitSimdAddSaturateInstruction(opcode: Opcode)

    public fun visitSimdSubtractSaturateInstruction(opcode: Opcode)

    public fun visitSimdShiftLeftInstruction(opcode: Opcode)

    public fun visitSimdAndInstruction(opcode: Opcode)

    public fun visitSimdOrInstruction(opcode: Opcode)

    public fun visitSimdNotInstruction(opcode: Opcode)

    public fun visitSimdBitSelectInstruction(opcode: Opcode)

    public fun visitSimdAllTrueInstruction(opcode: Opcode)

    public fun visitSimdEqualInstruction(opcode: Opcode)

    public fun visitSimdNotEqualInstruction(opcode: Opcode)

    public fun visitSimdLessThanInstruction(opcode: Opcode)

    public fun visitSimdLessEqualInstruction(opcode: Opcode)

    public fun visitSimdGreaterThanInstruction(opcode: Opcode)

    public fun visitSimdGreaterEqualInstruction(opcode: Opcode)

    public fun visitSimdMinInstruction(opcode: Opcode)

    public fun visitSimdMaxInstruction(opcode: Opcode)

    public fun visitSimdDivideInstruction(opcode: Opcode)

    public fun visitSimdSqrtInstruction(opcode: Opcode)

    public fun visitSimdConvertInstruction(opcode: Opcode)

    public fun visitSimdTruncateInstruction(opcode: Opcode)

    public fun visitCopySignInstruction(opcode: Opcode)

    public fun visitXorInstruction(opcode: Opcode)

    public fun visitSimdAbsInstruction(opcode: Opcode)
}
