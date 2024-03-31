package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.instructions.*
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ExpressionNode : ExpressionVisitor {
    public val instructions: MutableList<Instruction> = mutableListOf()

    public fun accept(expressionVisitor: ExpressionVisitor) {
        for (instruction in instructions) {
            instruction.accept(expressionVisitor)
        }

        expressionVisitor.visitEnd()
    }

    override fun visitEnd() {
        // empty
    }

    public override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicLoadInstruction(opcode, alignment, offset))
    }

    public override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicStoreInstruction(opcode, alignment, offset))
    }

    public override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicRmwCompareExchangeInstruction(opcode, alignment, offset))
    }

    public override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicWaitInstruction(opcode, alignment, offset))
    }

    public override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicWakeInstruction(opcode, alignment, offset))
    }

    public override fun visitBrTableInstruction(targets: Array<UInt>, defaultTarget: UInt) {
        instructions.add(BrTableInstruction(targets, defaultTarget))
    }

    public override fun visitCompareInstruction(opcode: Opcode) {
        instructions.add(CompareInstruction(opcode))
    }

    public override fun visitConvertInstruction(opcode: Opcode) {
        instructions.add(ConvertInstruction(opcode))
    }

    public override fun visitEndInstruction() {
        instructions.add(EndInstruction())
    }

    public override fun visitEndFunctionInstruction() {
        instructions.add(EndInstruction())
    }

    public override fun visitConstFloat32Instruction(value: Float) {
        instructions.add(ConstFloat32Instruction(value))
    }

    public override fun visitConstFloat64Instruction(value: Double) {
        instructions.add(ConstFloat64Instruction(value))
    }

    public override fun visitConstInt32Instruction(value: Int) {
        instructions.add(ConstInt32Instruction(value))
    }

    public override fun visitConstInt64Instruction(value: Long) {
        instructions.add(ConstInt64Instruction(value))
    }

    public override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(LoadInstruction(opcode, alignment, offset))
    }

    public override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(StoreInstruction(opcode, alignment, offset))
    }

    public override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value) {
        instructions.add(SimdShuffleInstruction(opcode, value))
    }

    public override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(SimdStoreInstruction(opcode, alignment, offset))
    }

    public override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(SimdLoadInstruction(opcode, alignment, offset))
    }

    public override fun visitSimdConstInstruction(value: V128Value) {
        instructions.add(SimdConstInstruction(value))
    }

    public override fun visitWrapInstruction(opcode: Opcode) {
        instructions.add(WrapInstruction(opcode))
    }

    public override fun visitExtendInstruction(opcode: Opcode) {
        instructions.add(ExtendInstruction(opcode))
    }

    public override fun visitDemoteInstruction(opcode: Opcode) {
        instructions.add(DemoteInstruction(opcode))
    }

    public override fun visitPromoteInstruction(opcode: Opcode) {
        instructions.add(PromoteInstruction(opcode))
    }

    public override fun visitReinterpretInstruction(opcode: Opcode) {
        instructions.add(ReinterpretInstruction(opcode))
    }

    public override fun visitUnreachableInstruction() {
        instructions.add(UnreachableInstruction())
    }

    public override fun visitNopInstruction() {
        instructions.add(NopInstruction())
    }

    public override fun visitIfInstruction(types: Array<WasmType>) {
        instructions.add(IfInstruction(types))
    }

    public override fun visitLoopInstruction(types: Array<WasmType>) {
        instructions.add(LoopInstruction(types))
    }

    public override fun visitBlockInstruction(types: Array<WasmType>) {
        instructions.add(BlockInstruction(types))
    }

    public override fun visitElseInstruction() {
        instructions.add(ElseInstruction())
    }

    public override fun visitTryInstruction(types: Array<WasmType>) {
        instructions.add(TryInstruction(types))
    }

    public override fun visitCatchInstruction() {
        instructions.add(CatchInstruction())
    }

    public override fun visitThrowRefInstruction() {
        instructions.add(ThrowRefInstruction())
    }

    public override fun visitThrowInstruction(exceptionIndex: UInt) {
        instructions.add(ThrowInstruction(exceptionIndex))
    }

    public override fun visitRethrowInstruction() {
        instructions.add(RethrowInstruction())
    }

    public override fun visitBrInstruction(depth: UInt) {
        instructions.add(BrInstruction(depth))
    }

    public override fun visitBrIfInstruction(depth: UInt) {
        instructions.add(BrIfInstruction(depth))
    }

    public override fun visitReturnInstruction() {
        instructions.add(ReturnInstruction())
    }

    public override fun visitCallInstruction(functionIndex: UInt) {
        instructions.add(CallInstruction(functionIndex))
    }

    public override fun visitCallIndirectInstruction(signatureIndex: UInt, reserved: Boolean) {
        instructions.add(CallIndirectInstruction(signatureIndex, reserved))
    }

    public override fun visitDropInstruction() {
        instructions.add(DropInstruction())
    }

    public override fun visitSelectInstruction() {
        instructions.add(SelectInstruction())
    }

    public override fun visitGetGlobalInstruction(globalIndex: UInt) {
        instructions.add(GetGlobalInstruction(globalIndex))
    }

    public override fun visitSetLocalInstruction(localIndex: UInt) {
        instructions.add(SetLocalInstruction(localIndex))
    }

    public override fun visitTeeLocalInstruction(localIndex: UInt) {
        instructions.add(TeeLocalInstruction(localIndex))
    }

    public override fun visitGetLocalInstruction(localIndex: UInt) {
        instructions.add(GetLocalInstruction(localIndex))
    }

    public override fun visitSetGlobalInstruction(globalIndex: UInt) {
        instructions.add(SetGlobalInstruction(globalIndex))
    }

    public override fun visitMemorySizeInstruction(reserved: Boolean) {
        instructions.add(CurrentMemoryInstruction(reserved))
    }

    public override fun visitMemoryGrowInstruction(reserved: Boolean) {
        instructions.add(GrowMemoryInstruction(reserved))
    }

    public override fun visitEqualZeroInstruction(opcode: Opcode) {
        instructions.add(EqualZeroInstruction(opcode))
    }

    public override fun visitEqualInstruction(opcode: Opcode) {
        instructions.add(EqualInstruction(opcode))
    }

    public override fun visitNotEqualInstruction(opcode: Opcode) {
        instructions.add(NotEqualInstruction(opcode))
    }

    public override fun visitLessThanInstruction(opcode: Opcode) {
        instructions.add(LessThanInstruction(opcode))
    }

    public override fun visitLessEqualInstruction(opcode: Opcode) {
        instructions.add(LessEqualInstruction(opcode))
    }

    public override fun visitGreaterThanInstruction(opcode: Opcode) {
        instructions.add(GreaterThanInstruction(opcode))
    }

    public override fun visitGreaterEqualInstruction(opcode: Opcode) {
        instructions.add(GreaterEqualInstruction(opcode))
    }

    public override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        instructions.add(CountLeadingZerosInstruction(opcode))
    }

    public override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        instructions.add(CountTrailingZerosInstruction(opcode))
    }

    public override fun visitPopulationCountInstruction(opcode: Opcode) {
        instructions.add(PopulationCountInstruction(opcode))
    }

    public override fun visitAddInstruction(opcode: Opcode) {
        instructions.add(AddInstruction(opcode))
    }

    public override fun visitSubtractInstruction(opcode: Opcode) {
        instructions.add(SubtractInstruction(opcode))
    }

    public override fun visitMultiplyInstruction(opcode: Opcode) {
        instructions.add(MultiplyInstruction(opcode))
    }

    public override fun visitDivideInstruction(opcode: Opcode) {
        instructions.add(DivideInstruction(opcode))
    }

    public override fun visitRemainderInstruction(opcode: Opcode) {
        instructions.add(RemainderInstruction(opcode))
    }

    public override fun visitAndInstruction(opcode: Opcode) {
        instructions.add(AndInstruction(opcode))
    }

    public override fun visitOrInstruction(opcode: Opcode) {
        instructions.add(OrInstruction(opcode))
    }

    public override fun visitSimdXorInstruction(opcode: Opcode) {
        instructions.add(SimdXorInstruction(opcode))
    }

    public override fun visitShiftLeftInstruction(opcode: Opcode) {
        instructions.add(ShiftLeftInstruction(opcode))
    }

    public override fun visitRotateLeftInstruction(opcode: Opcode) {
        instructions.add(RotateLeftInstruction(opcode))
    }

    public override fun visitRotateRightInstruction(opcode: Opcode) {
        instructions.add(RotateRightInstruction(opcode))
    }

    public override fun visitAbsoluteInstruction(opcode: Opcode) {
        instructions.add(AbsoluteInstruction(opcode))
    }

    public override fun visitNegativeInstruction(opcode: Opcode) {
        instructions.add(NegativeInstruction(opcode))
    }

    public override fun visitCeilingInstruction(opcode: Opcode) {
        instructions.add(CeilingInstruction(opcode))
    }

    public override fun visitFloorInstruction(opcode: Opcode) {
        instructions.add(FloorInstruction(opcode))
    }

    public override fun visitTruncateInstruction(opcode: Opcode) {
        instructions.add(TruncateInstruction(opcode))
    }

    public override fun visitNearestInstruction(opcode: Opcode) {
        instructions.add(NearestInstruction(opcode))
    }

    public override fun visitSqrtInstruction(opcode: Opcode) {
        instructions.add(SqrtInstruction(opcode))
    }

    public override fun visitMinInstruction(opcode: Opcode) {
        instructions.add(MinInstruction(opcode))
    }

    public override fun visitMaxInstruction(opcode: Opcode) {
        instructions.add(MaxInstruction(opcode))
    }

    public override fun visitAtomicRmwAddInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicRmwAddInstruction(opcode, alignment, offset))
    }

    public override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicRmwAddInstruction(opcode, alignment, offset))
    }

    public override fun visitAtomicRmwAndInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicRmwAndInstruction(opcode, alignment, offset))
    }

    public override fun visitAtomicRmwOrInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicRmwOrInstruction(opcode, alignment, offset))
    }

    public override fun visitAtomicRmwXorInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicRmwXorInstruction(opcode, alignment, offset))
    }

    public override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicRmwExchangeInstruction(opcode, alignment, offset))
    }

    public override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt) {
        instructions.add(SimdSplatInstruction(opcode, value))
    }

    public override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        instructions.add(SimdExtractLaneInstruction(opcode, index))
    }

    public override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt) {
        instructions.add(SimdReplaceLaneInstruction(opcode, index))
    }

    public override fun visitSimdAddInstruction(opcode: Opcode) {
        instructions.add(SimdAddInstruction(opcode))
    }

    public override fun visitSimdSubtractInstruction(opcode: Opcode) {
        instructions.add(SimdSubtractInstruction(opcode))
    }

    public override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        instructions.add(SimdMultiplyInstruction(opcode))
    }

    public override fun visitSimdNegativeInstruction(opcode: Opcode) {
        instructions.add(SimdNegativeInstruction(opcode))
    }

    public override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        instructions.add(SimdAddSaturateInstruction(opcode))
    }

    public override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        instructions.add(SimdSubtractSaturateInstruction(opcode))
    }

    public override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        instructions.add(SimdShiftLeftInstruction(opcode))
    }

    public override fun visitSimdAndInstruction(opcode: Opcode) {
        instructions.add(SimdAndInstruction(opcode))
    }

    public override fun visitSimdOrInstruction(opcode: Opcode) {
        instructions.add(SimdOrInstruction(opcode))
    }

    public override fun visitSimdNotInstruction(opcode: Opcode) {
        instructions.add(SimdNotInstruction(opcode))
    }

    public override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        instructions.add(SimdBitSelectInstruction(opcode))
    }

    public override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        instructions.add(SimdAllTrueInstruction(opcode))
    }

    public override fun visitSimdEqualInstruction(opcode: Opcode) {
        instructions.add(SimdEqualInstruction(opcode))
    }

    public override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        instructions.add(SimdNotEqualInstruction(opcode))
    }

    public override fun visitSimdLessThanInstruction(opcode: Opcode) {
        instructions.add(SimdLessThanInstruction(opcode))
    }

    public override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        instructions.add(SimdLessEqualInstruction(opcode))
    }

    public override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        instructions.add(SimdGreaterThanInstruction(opcode))
    }

    public override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        instructions.add(SimdGreaterEqualInstruction(opcode))
    }

    public override fun visitSimdMinInstruction(opcode: Opcode) {
        instructions.add(SimdMinInstruction(opcode))
    }

    public override fun visitSimdMaxInstruction(opcode: Opcode) {
        instructions.add(SimdMaxInstruction(opcode))
    }

    public override fun visitSimdDivideInstruction(opcode: Opcode) {
        instructions.add(SimdDivideInstruction(opcode))
    }

    public override fun visitSimdSqrtInstruction(opcode: Opcode) {
        instructions.add(SimdSqrtInstruction(opcode))
    }

    public override fun visitSimdConvertInstruction(opcode: Opcode) {
        instructions.add(SimdConvertInstruction(opcode))
    }

    public override fun visitSimdTruncateInstruction(opcode: Opcode) {
        instructions.add(SimdTruncateInstruction(opcode))
    }

    public override fun visitCopySignInstruction(opcode: Opcode) {
        instructions.add(CopySignInstruction(opcode))
    }

    public override fun visitXorInstruction(opcode: Opcode) {
        instructions.add(XorInstruction(opcode))
    }

    public override fun visitSimdAbsInstruction(opcode: Opcode) {
        instructions.add(SimdAbsInstruction(opcode))
    }

    public override fun visitMemoryFillInstruction(memoryIndex: UInt, address: UInt, value: UInt, size: UInt) {
        instructions.add(MemoryFillInstruction(memoryIndex, address, value))
    }

    public override fun visitMemoryCopyInstruction(targetIndex: UInt, sourceIndex: UInt, targetOffset: UInt, sourceOffset: UInt, size: UInt) {
        instructions.add(MemoryCopyInstruction(targetIndex, sourceIndex, targetOffset, sourceOffset, size))
    }

    public override fun visitMemoryInitInstruction(memoryIndex: UInt, segmentIndex: UInt, target: UInt, address: UInt, size: UInt) {
        instructions.add(MemoryInitInstruction(memoryIndex, segmentIndex, target, address, size))
    }

    public override fun visitDataDropInstruction(segmentIndex: UInt) {
        instructions.add(DataDropInstruction(segmentIndex))
    }

    override fun visitTableSizeInstruction(tableIndex: UInt) {
        instructions.add(TableSizeInstruction(tableIndex))
    }

    override fun visitTableGrowInstruction(tableIndex: UInt, value: UInt, delta: UInt) {
        instructions.add(TableGrowInstruction(tableIndex, value, delta))
    }

    override fun visitTableFillInstruction(tableIndex: UInt, target: UInt, value: UInt, size: UInt) {
        instructions.add(TableFillInstruction(tableIndex, target, value, size))
    }

    override fun visitTableCopyInstruction(targetTableIndex: UInt, sourceTableIndex: UInt, target: UInt, value: UInt, size: UInt) {
        instructions.add(TableCopyInstruction(targetTableIndex, sourceTableIndex, target, value, size))
    }
}
