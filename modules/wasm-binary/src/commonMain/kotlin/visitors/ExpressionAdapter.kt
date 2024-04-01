package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType

public open class ExpressionAdapter(protected val delegate: ExpressionVisitor? = null) : ExpressionVisitor {

    public override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit

    public override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicLoadInstruction(opcode, alignment, offset)
    }

    public override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicStoreInstruction(opcode, alignment, offset)
    }

    public override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
    }

    public override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicWaitInstruction(opcode, alignment, offset)
    }

    public override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicWakeInstruction(opcode, alignment, offset)
    }

    public override fun visitBrTableInstruction(targets: Array<UInt>, defaultTarget: UInt) {
        delegate?.visitBrTableInstruction(targets, defaultTarget)
    }

    public override fun visitCompareInstruction(opcode: Opcode) {
        delegate?.visitCompareInstruction(opcode)
    }

    public override fun visitConvertInstruction(opcode: Opcode) {
        delegate?.visitConvertInstruction(opcode)
    }

    public override fun visitEndInstruction() {
        delegate?.visitEndInstruction()
    }

    public override fun visitEndFunctionInstruction() {
        delegate?.visitEndInstruction()
    }

    public override fun visitConstFloat32Instruction(value: Float) {
        delegate?.visitConstFloat32Instruction(value)
    }

    public override fun visitConstFloat64Instruction(value: Double) {
        delegate?.visitConstFloat64Instruction(value)
    }

    public override fun visitConstInt32Instruction(value: Int) {
        delegate?.visitConstInt32Instruction(value)
    }

    public override fun visitConstInt64Instruction(value: Long) {
        delegate?.visitConstInt64Instruction(value)
    }

    public override fun visitSimdConstInstruction(value: V128Value) {
        delegate?.visitSimdConstInstruction(value)
    }

    override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value) {
        delegate?.visitSimdShuffleInstruction(opcode, value)
    }

    public override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitLoadInstruction(opcode, alignment, offset)
    }

    public override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitSimdLoadInstruction(opcode, alignment, offset)
    }

    public override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitStoreInstruction(opcode, alignment, offset)
    }

    public override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitSimdStoreInstruction(opcode, alignment, offset)
    }

    public override fun visitWrapInstruction(opcode: Opcode) {
        delegate?.visitWrapInstruction(opcode)
    }

    public override fun visitExtendInstruction(opcode: Opcode) {
        delegate?.visitExtendInstruction(opcode)
    }

    public override fun visitDemoteInstruction(opcode: Opcode) {
        delegate?.visitDemoteInstruction(opcode)
    }

    public override fun visitPromoteInstruction(opcode: Opcode) {
        delegate?.visitPromoteInstruction(opcode)
    }

    public override fun visitReinterpretInstruction(opcode: Opcode) {
        delegate?.visitReinterpretInstruction(opcode)
    }

    public override fun visitUnreachableInstruction() {
        delegate?.visitUnreachableInstruction()
    }

    public override fun visitNopInstruction() {
        delegate?.visitNopInstruction()
    }

    public override fun visitIfInstruction(types: Array<WasmType>) {
        delegate?.visitIfInstruction(types)
    }

    public override fun visitLoopInstruction(types: Array<WasmType>) {
        delegate?.visitLoopInstruction(types)
    }

    public override fun visitBlockInstruction(types: Array<WasmType>) {
        delegate?.visitBlockInstruction(types)
    }

    public override fun visitElseInstruction() {
        delegate?.visitElseInstruction()
    }

    public override fun visitTryInstruction(types: Array<WasmType>) {
        delegate?.visitTryInstruction(types)
    }

    public override fun visitCatchInstruction() {
        delegate?.visitCatchInstruction()
    }

    public override fun visitThrowRefInstruction() {
        delegate?.visitThrowRefInstruction()
    }

    public override fun visitThrowInstruction(exceptionIndex: UInt) {
        delegate?.visitThrowInstruction(exceptionIndex)
    }

    public override fun visitRethrowInstruction() {
        delegate?.visitRethrowInstruction()
    }

    public override fun visitBrInstruction(depth: UInt) {
        delegate?.visitBrInstruction(depth)
    }

    public override fun visitBrIfInstruction(depth: UInt) {
        delegate?.visitBrIfInstruction(depth)
    }

    public override fun visitReturnInstruction() {
        delegate?.visitReturnInstruction()
    }

    public override fun visitCallInstruction(functionIndex: UInt) {
        delegate?.visitCallInstruction(functionIndex)
    }

    public override fun visitCallIndirectInstruction(signatureIndex: UInt, reserved: Boolean) {
        delegate?.visitCallIndirectInstruction(signatureIndex, reserved)
    }

    public override fun visitDropInstruction() {
        delegate?.visitDropInstruction()
    }

    public override fun visitSelectInstruction() {
        delegate?.visitSelectInstruction()
    }

    public override fun visitGetGlobalInstruction(globalIndex: UInt) {
        delegate?.visitGetGlobalInstruction(globalIndex)
    }

    public override fun visitSetLocalInstruction(localIndex: UInt) {
        delegate?.visitSetLocalInstruction(localIndex)
    }

    public override fun visitTeeLocalInstruction(localIndex: UInt) {
        delegate?.visitTeeLocalInstruction(localIndex)
    }

    public override fun visitGetLocalInstruction(localIndex: UInt) {
        delegate?.visitGetLocalInstruction(localIndex)
    }

    public override fun visitSetGlobalInstruction(globalIndex: UInt) {
        delegate?.visitSetGlobalInstruction(globalIndex)
    }

    public override fun visitMemorySizeInstruction(reserved: Boolean) {
        delegate?.visitMemorySizeInstruction(reserved)
    }

    public override fun visitMemoryGrowInstruction(reserved: Boolean) {
        delegate?.visitMemoryGrowInstruction(reserved)
    }

    public override fun visitEqualZeroInstruction(opcode: Opcode) {
        delegate?.visitEqualZeroInstruction(opcode)
    }

    public override fun visitEqualInstruction(opcode: Opcode) {
        delegate?.visitEqualInstruction(opcode)
    }

    public override fun visitNotEqualInstruction(opcode: Opcode) {
        delegate?.visitNotEqualInstruction(opcode)
    }

    public override fun visitLessThanInstruction(opcode: Opcode) {
        delegate?.visitLessThanInstruction(opcode)
    }

    public override fun visitLessEqualInstruction(opcode: Opcode) {
        delegate?.visitLessEqualInstruction(opcode)
    }

    public override fun visitGreaterThanInstruction(opcode: Opcode) {
        delegate?.visitGreaterThanInstruction(opcode)
    }

    public override fun visitGreaterEqualInstruction(opcode: Opcode) {
        delegate?.visitGreaterEqualInstruction(opcode)
    }

    public override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        delegate?.visitCountLeadingZerosInstruction(opcode)
    }

    public override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        delegate?.visitCountTrailingZerosInstruction(opcode)
    }

    public override fun visitPopulationCountInstruction(opcode: Opcode) {
        delegate?.visitPopulationCountInstruction(opcode)
    }

    public override fun visitAddInstruction(opcode: Opcode) {
        delegate?.visitAddInstruction(opcode)
    }

    public override fun visitSubtractInstruction(opcode: Opcode) {
        delegate?.visitSubtractInstruction(opcode)
    }

    public override fun visitMultiplyInstruction(opcode: Opcode) {
        delegate?.visitMultiplyInstruction(opcode)
    }

    public override fun visitDivideInstruction(opcode: Opcode) {
        delegate?.visitDivideInstruction(opcode)
    }

    public override fun visitRemainderInstruction(opcode: Opcode) {
        delegate?.visitRemainderInstruction(opcode)
    }

    public override fun visitAndInstruction(opcode: Opcode) {
        delegate?.visitAndInstruction(opcode)
    }

    public override fun visitOrInstruction(opcode: Opcode) {
        delegate?.visitOrInstruction(opcode)
    }

    public override fun visitSimdXorInstruction(opcode: Opcode) {
        delegate?.visitSimdXorInstruction(opcode)
    }

    public override fun visitShiftLeftInstruction(opcode: Opcode) {
        delegate?.visitShiftLeftInstruction(opcode)
    }

    public override fun visitRotateLeftInstruction(opcode: Opcode) {
        delegate?.visitRotateLeftInstruction(opcode)
    }

    public override fun visitRotateRightInstruction(opcode: Opcode) {
        delegate?.visitRotateRightInstruction(opcode)
    }

    public override fun visitAbsoluteInstruction(opcode: Opcode) {
        delegate?.visitAbsoluteInstruction(opcode)
    }

    public override fun visitNegativeInstruction(opcode: Opcode) {
        delegate?.visitNegativeInstruction(opcode)
    }

    public override fun visitCeilingInstruction(opcode: Opcode) {
        delegate?.visitCeilingInstruction(opcode)
    }

    public override fun visitFloorInstruction(opcode: Opcode) {
        delegate?.visitFloorInstruction(opcode)
    }

    public override fun visitTruncateInstruction(opcode: Opcode) {
        delegate?.visitTruncateInstruction(opcode)
    }

    public override fun visitNearestInstruction(opcode: Opcode) {
        delegate?.visitNearestInstruction(opcode)
    }

    public override fun visitSqrtInstruction(opcode: Opcode) {
        delegate?.visitSqrtInstruction(opcode)
    }

    public override fun visitMinInstruction(opcode: Opcode) {
        delegate?.visitMinInstruction(opcode)
    }

    public override fun visitMaxInstruction(opcode: Opcode) {
        delegate?.visitMaxInstruction(opcode)
    }

    public override fun visitAtomicRmwAddInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwAddInstruction(opcode, alignment, offset)
    }

    public override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)
    }

    public override fun visitAtomicRmwAndInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwAndInstruction(opcode, alignment, offset)
    }

    public override fun visitAtomicRmwOrInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwOrInstruction(opcode, alignment, offset)
    }

    public override fun visitAtomicRmwXorInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwXorInstruction(opcode, alignment, offset)
    }

    public override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)
    }

    public override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt) {
        delegate?.visitSimdSplatInstruction(opcode, value)
    }

    public override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        delegate?.visitSimdExtractLaneInstruction(opcode, index)
    }

    public override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt) {
        delegate?.visitSimdReplaceLaneInstruction(opcode, index)
    }

    public override fun visitSimdAddInstruction(opcode: Opcode) {
        delegate?.visitSimdAddInstruction(opcode)
    }

    public override fun visitSimdSubtractInstruction(opcode: Opcode) {
        delegate?.visitSimdSubtractInstruction(opcode)
    }

    public override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        delegate?.visitSimdMultiplyInstruction(opcode)
    }

    public override fun visitSimdNegativeInstruction(opcode: Opcode) {
        delegate?.visitSimdNegativeInstruction(opcode)
    }

    public override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        delegate?.visitSimdAddSaturateInstruction(opcode)
    }

    public override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        delegate?.visitSimdSubtractSaturateInstruction(opcode)
    }

    public override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        delegate?.visitSimdShiftLeftInstruction(opcode)
    }

    public override fun visitSimdAndInstruction(opcode: Opcode) {
        delegate?.visitSimdAndInstruction(opcode)
    }

    public override fun visitSimdOrInstruction(opcode: Opcode) {
        delegate?.visitSimdOrInstruction(opcode)
    }

    public override fun visitSimdNotInstruction(opcode: Opcode) {
        delegate?.visitSimdNotInstruction(opcode)
    }

    public override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        delegate?.visitSimdBitSelectInstruction(opcode)
    }

    public override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        delegate?.visitSimdAllTrueInstruction(opcode)
    }

    public override fun visitSimdEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdEqualInstruction(opcode)
    }

    public override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdNotEqualInstruction(opcode)
    }

    public override fun visitSimdLessThanInstruction(opcode: Opcode) {
        delegate?.visitSimdLessThanInstruction(opcode)
    }

    public override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdLessEqualInstruction(opcode)
    }

    public override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        delegate?.visitSimdGreaterThanInstruction(opcode)
    }

    public override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdGreaterEqualInstruction(opcode)
    }

    public override fun visitSimdMinInstruction(opcode: Opcode) {
        delegate?.visitSimdMinInstruction(opcode)
    }

    public override fun visitSimdMaxInstruction(opcode: Opcode) {
        delegate?.visitSimdMaxInstruction(opcode)
    }

    public override fun visitSimdDivideInstruction(opcode: Opcode) {
        delegate?.visitSimdDivideInstruction(opcode)
    }

    public override fun visitSimdSqrtInstruction(opcode: Opcode) {
        delegate?.visitSimdSqrtInstruction(opcode)
    }

    public override fun visitSimdConvertInstruction(opcode: Opcode) {
        delegate?.visitSimdConvertInstruction(opcode)
    }

    public override fun visitSimdTruncateInstruction(opcode: Opcode) {
        delegate?.visitSimdTruncateInstruction(opcode)
    }

    public override fun visitCopySignInstruction(opcode: Opcode) {
        delegate?.visitCopySignInstruction(opcode)
    }

    public override fun visitXorInstruction(opcode: Opcode) {
        delegate?.visitXorInstruction(opcode)
    }

    public override fun visitSimdAbsInstruction(opcode: Opcode) {
        delegate?.visitSimdAbsInstruction(opcode)
    }

    override fun visitMemoryFillInstruction(memoryIndex: UInt) {
        delegate?.visitMemoryFillInstruction(memoryIndex)
    }

    override fun visitMemoryCopyInstruction(targetIndex: UInt, sourceIndex: UInt) {
        delegate?.visitMemoryCopyInstruction(targetIndex, sourceIndex)
    }

    override fun visitMemoryInitInstruction(memoryIndex: UInt, segmentIndex: UInt) {
        delegate?.visitMemoryInitInstruction(memoryIndex, segmentIndex)
    }

    public override fun visitDataDropInstruction(segmentIndex: UInt) {
        delegate?.visitDataDropInstruction(segmentIndex)
    }

    public override fun visitTableSizeInstruction(tableIndex: UInt) {
        delegate?.visitTableSizeInstruction(tableIndex)
    }

    public override fun visitTableGrowInstruction(tableIndex: UInt, value: UInt, delta: UInt) {
        delegate?.visitTableGrowInstruction(tableIndex, value, delta)
    }

    public override fun visitTableFillInstruction(tableIndex: UInt) {
        delegate?.visitTableFillInstruction(tableIndex)
    }

    public override fun visitTableCopyInstruction(targetTableIndex: UInt, sourceTableIndex: UInt) {
        delegate?.visitTableCopyInstruction(targetTableIndex, sourceTableIndex)
    }

    public override fun visitTableInitInstruction(segmentIndex: UInt, tableIndex: UInt) {
        delegate?.visitTableInitInstruction(segmentIndex, tableIndex)
    }

    public override fun visitElementDropInstruction(segmentIndex: UInt) {
        delegate?.visitElementDropInstruction(segmentIndex)
    }
}
