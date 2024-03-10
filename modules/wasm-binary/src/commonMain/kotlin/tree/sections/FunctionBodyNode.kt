package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.instructions.*
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor
import org.wasmium.wasm.binary.tree.Opcode

public class FunctionBodyNode : FunctionBodyVisitor {
    public var functionIndex: UInt? = null
    public val locals: MutableList<LocalNode> = mutableListOf<LocalNode>()
    public val instructions: MutableList<Instruction> = mutableListOf<Instruction>()

    public fun accept(functionBodyVisitor: FunctionBodyVisitor) {
        for (local in locals) {
            functionBodyVisitor.visitLocalVariable(local.localIndex!!, local.count!!, local.type!!)
        }

        for (instruction in instructions) {
            instruction.accept(functionBodyVisitor)
        }
    }

    public override fun visitLocalVariable(localIndex: UInt, count: UInt, localType: WasmType) {
        val localNode: LocalNode = LocalNode()
        localNode.count = count
        localNode.type = localType

        locals.add(localNode)
    }

    public override fun visitCode() {
        // empty
    }

    public override fun visitEnd() {
        // empty
    }

    override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicLoadInstruction(opcode, alignment, offset))
    }

    override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicStoreInstruction(opcode, alignment, offset))
    }

    override fun visitAtomicRmwInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicRmwInstruction(opcode, alignment, offset))
    }

    override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicRmwCompareExchangeInstruction(opcode, alignment, offset))
    }

    override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicWaitInstruction(opcode, alignment, offset))
    }

    override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(AtomicWakeInstruction(opcode, alignment, offset))
    }

    public override fun visitBrTableInstruction(targets: Array<UInt>, defaultTarget: UInt) {
        instructions.add(BrTableInstruction(targets, defaultTarget))
    }

    override fun visitCompareInstruction(opcode: Opcode) {
        instructions.add(CompareInstruction(opcode))
    }

    override fun visitConvertInstruction(opcode: Opcode) {
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

    override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(LoadInstruction(opcode, alignment, offset))
    }

    override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(StoreInstruction(opcode, alignment, offset))
    }

    public override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value) {
        instructions.add(SimdShuffleInstruction(opcode, value))
    }

    override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(SimdStoreInstruction(opcode, alignment, offset))
    }

    override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        instructions.add(SimdLoadInstruction(opcode, alignment, offset))
    }

    public override fun visitSimdConstInstruction(value: V128Value) {
        instructions.add(SimdConstInstruction(value))
    }

    override fun visitWrapInstruction(opcode: Opcode) {
        instructions.add(WrapInstruction(opcode))
    }

    override fun visitExtendInstruction(opcode: Opcode) {
        instructions.add(ExtendInstruction(opcode))
    }

    override fun visitDemoteInstruction(opcode: Opcode) {
        instructions.add(DemoteInstruction(opcode))
    }

    override fun visitPromoteInstruction(opcode: Opcode) {
        instructions.add(PromoteInstruction(opcode))
    }

    override fun visitReinterpretInstruction(opcode: Opcode) {
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

    override fun visitThrowInstruction(exceptionIndex: UInt) {
        instructions.add(ThrowInstruction(exceptionIndex))
    }

    public override fun visitRethrowInstruction() {
        instructions.add(RethrowInstruction())
    }

    override fun visitBrInstruction(depth: UInt) {
        instructions.add(BrInstruction(depth))
    }

    override fun visitBrIfInstruction(depth: UInt) {
        instructions.add(BrIfInstruction(depth))
    }

    public override fun visitReturnInstruction() {
        instructions.add(ReturnInstruction())
    }

    override fun visitCallInstruction(functionIndex: UInt) {
        instructions.add(CallInstruction(functionIndex))
    }

    override fun visitCallIndirectInstruction(signatureIndex: UInt, reserved: Boolean) {
        instructions.add(CallIndirectInstruction(signatureIndex))
    }

    public override fun visitDropInstruction() {
        instructions.add(DropInstruction())
    }

    public override fun visitSelectInstruction() {
        instructions.add(SelectInstruction())
    }

    override fun visitGetGlobalInstruction(globalIndex: UInt) {
        instructions.add(GetGlobalInstruction(globalIndex))
    }

    override fun visitSetLocalInstruction(localIndex: UInt) {
        instructions.add(SetLocalInstruction(localIndex))
    }

    override fun visitTeeLocalInstruction(localIndex: UInt) {
        instructions.add(TeeLocalInstruction(localIndex))
    }

    override fun visitGetLocalInstruction(localIndex: UInt) {
        instructions.add(GetLocalInstruction(localIndex))
    }

    override fun visitSetGlobalInstruction(globalIndex: UInt) {
        instructions.add(SetGlobalInstruction(globalIndex))
    }

    public override fun visitMemorySizeInstruction(reserved: Boolean) {
        instructions.add(CurrentMemoryInstruction(reserved))
    }

    public override fun visitMemoryGrowInstruction(reserved: Boolean) {
        instructions.add(GrowMemoryInstruction(reserved))
    }

    override fun visitEqualZeroInstruction(opcode: Opcode) {
        instructions.add(EqualZeroInstruction(opcode))
    }

    override fun visitEqualInstruction(opcode: Opcode) {
        instructions.add(EqualInstruction(opcode))
    }

    override fun visitNotEqualInstruction(opcode: Opcode) {
        instructions.add(NotEqualInstruction(opcode))
    }

    override fun visitLessThanInstruction(opcode: Opcode) {
        instructions.add(LessThanInstruction(opcode))
    }

    override fun visitLessEqualInstruction(opcode: Opcode) {
        instructions.add(LessEqualInstruction(opcode))
    }

    override fun visitGreaterThanInstruction(opcode: Opcode) {
        instructions.add(GreaterThanInstruction(opcode))
    }

    override fun visitGreaterEqualInstruction(opcode: Opcode) {
        instructions.add(GreaterEqualInstruction(opcode))
    }

    override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        instructions.add(CountLeadingZerosInstruction(opcode))
    }

    override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        instructions.add(CountTrailingZerosInstruction(opcode))
    }

    override fun visitPopulationCountInstruction(opcode: Opcode) {
        instructions.add(PopulationCountInstruction(opcode))
    }

    override fun visitAddInstruction(opcode: Opcode) {
        instructions.add(AddInstruction(opcode))
    }

    override fun visitSubtractInstruction(opcode: Opcode) {
        instructions.add(SubtractInstruction(opcode))
    }

    override fun visitMultiplyInstruction(opcode: Opcode) {
        instructions.add(MultiplyInstruction(opcode))
    }

    override fun visitDivideInstruction(opcode: Opcode) {
        instructions.add(DivideInstruction(opcode))
    }

    override fun visitRemainderInstruction(opcode: Opcode) {
        instructions.add(RemainderInstruction(opcode))
    }

    override fun visitAndInstruction(opcode: Opcode) {
        instructions.add(AndInstruction(opcode))
    }

    override fun visitOrInstruction(opcode: Opcode) {
        instructions.add(OrInstruction(opcode))
    }

    override fun visitSimdXorInstruction(opcode: Opcode) {
        instructions.add(SimdXorInstruction(opcode))
    }

    override fun visitShiftLeftInstruction(opcode: Opcode) {
        instructions.add(ShiftLeftInstruction(opcode))
    }

    override fun visitRotateLeftInstruction(opcode: Opcode) {
        instructions.add(RotateLeftInstruction(opcode))
    }

    override fun visitRotateRightInstruction(opcode: Opcode) {
        instructions.add(RotateRightInstruction(opcode))
    }

    override fun visitAbsoluteInstruction(opcode: Opcode) {
        instructions.add(AbsoluteInstruction(opcode))
    }

    override fun visitNegativeInstruction(opcode: Opcode) {
        instructions.add(NegativeInstruction(opcode))
    }

    override fun visitCeilingInstruction(opcode: Opcode) {
        instructions.add(CeilingInstruction(opcode))
    }

    override fun visitFloorInstruction(opcode: Opcode) {
        instructions.add(FloorInstruction(opcode))
    }

    override fun visitTruncateInstruction(opcode: Opcode) {
        instructions.add(TruncateInstruction(opcode))
    }

    override fun visitNearestInstruction(opcode: Opcode) {
        instructions.add(NearestInstruction(opcode))
    }

    override fun visitSqrtInstruction(opcode: Opcode) {
        instructions.add(SqrtInstruction(opcode))
    }

    override fun visitMinInstruction(opcode: Opcode) {
        instructions.add(MinInstruction(opcode))
    }

    override fun visitMaxInstruction(opcode: Opcode) {
        instructions.add(MaxInstruction(opcode))
    }

    override fun visitAtomicRmwAddInstruction(opcode: Opcode, align: UInt, offset: UInt) {
        instructions.add(AtomicRmwAddInstruction(opcode, align, offset))
    }

    override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, align: UInt, offset: UInt) {
        instructions.add(AtomicRmwAddInstruction(opcode, align, offset))
    }

    override fun visitAtomicRmwAndInstruction(opcode: Opcode, align: UInt, offset: UInt) {
        instructions.add(AtomicRmwAndInstruction(opcode, align, offset))
    }

    override fun visitAtomicRmwOrInstruction(opcode: Opcode, align: UInt, offset: UInt) {
        instructions.add(AtomicRmwOrInstruction(opcode, align, offset))
    }

    override fun visitAtomicRmwXorInstruction(opcode: Opcode, align: UInt, offset: UInt) {
        instructions.add(AtomicRmwXorInstruction(opcode, align, offset))
    }

    override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, align: UInt, offset: UInt) {
        instructions.add(AtomicRmwExchangeInstruction(opcode, align, offset))
    }

    override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt) {
        instructions.add(SimdSplatInstruction(opcode, value))
    }

    override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        instructions.add(SimdExtractLaneInstruction(opcode, index))
    }

    override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt) {
        instructions.add(SimdReplaceLaneInstruction(opcode, index))
    }

    override fun visitSimdAddInstruction(opcode: Opcode) {
        instructions.add(SimdAddInstruction(opcode))
    }

    override fun visitSimdSubtractInstruction(opcode: Opcode) {
        instructions.add(SimdSubtractInstruction(opcode))
    }

    override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        instructions.add(SimdMultiplyInstruction(opcode))
    }

    override fun visitSimdNegativeInstruction(opcode: Opcode) {
        instructions.add(SimdNegativeInstruction(opcode))
    }

    override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        instructions.add(SimdAddSaturateInstruction(opcode))
    }

    override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        instructions.add(SimdSubtractSaturateInstruction(opcode))
    }

    override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        instructions.add(SimdShiftLeftInstruction(opcode))
    }

    override fun visitSimdAndInstruction(opcode: Opcode) {
        instructions.add(SimdAndInstruction(opcode))
    }

    override fun visitSimdOrInstruction(opcode: Opcode) {
        instructions.add(SimdOrInstruction(opcode))
    }

    override fun visitSimdNotInstruction(opcode: Opcode) {
        instructions.add(SimdNotInstruction(opcode))
    }

    override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        instructions.add(SimdBitSelectInstruction(opcode))
    }

    override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        instructions.add(SimdAllTrueInstruction(opcode))
    }

    override fun visitSimdEqualInstruction(opcode: Opcode) {
        instructions.add(SimdEqualInstruction(opcode))
    }

    override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        instructions.add(SimdNotEqualInstruction(opcode))
    }

    override fun visitSimdLessThanInstruction(opcode: Opcode) {
        instructions.add(SimdLessThanInstruction(opcode))
    }

    override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        instructions.add(SimdLessEqualInstruction(opcode))
    }

    override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        instructions.add(SimdGreaterThanInstruction(opcode))
    }

    override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        instructions.add(SimdGreaterEqualInstruction(opcode))
    }

    override fun visitSimdMinInstruction(opcode: Opcode) {
        instructions.add(SimdMinInstruction(opcode))
    }

    override fun visitSimdMaxInstruction(opcode: Opcode) {
        instructions.add(SimdMaxInstruction(opcode))
    }

    override fun visitSimdDivideInstruction(opcode: Opcode) {
        instructions.add(SimdDivideInstruction(opcode))
    }

    override fun visitSimdSqrtInstruction(opcode: Opcode) {
        instructions.add(SimdSqrtInstruction(opcode))
    }

    override fun visitSimdConvertInstruction(opcode: Opcode) {
        instructions.add(SimdConvertInstruction(opcode))
    }

    override fun visitSimdTruncateInstruction(opcode: Opcode) {
        instructions.add(SimdTruncateInstruction(opcode))
    }

    override fun visitCopySignInstruction(opcode: Opcode) {
        instructions.add(CopySignInstruction(opcode))
    }

    override fun visitXorInstruction(opcode: Opcode) {
        instructions.add(XorInstruction(opcode))
    }

    override fun visitSimdAbsInstruction(opcode: Opcode) {
        instructions.add(SimdAbsInstruction(opcode))
    }

}
