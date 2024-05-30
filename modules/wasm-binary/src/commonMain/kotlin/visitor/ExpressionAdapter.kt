package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.BlockType
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.instructions.TryCatchArgument

public open class ExpressionAdapter(protected val delegate: ExpressionVisitor? = null) : ExpressionVisitor {

    override fun visitEnd(): Unit = delegate?.visitEnd() ?: Unit

    override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicLoadInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicStoreInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicWaitInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicWakeInstruction(opcode, alignment, offset)
    }

    override fun visitBrTableInstruction(targets: List<UInt>, defaultTarget: UInt) {
        delegate?.visitBrTableInstruction(targets, defaultTarget)
    }

    override fun visitConvertInstruction(opcode: Opcode) {
        delegate?.visitConvertInstruction(opcode)
    }

    override fun visitEndInstruction() {
        delegate?.visitEndInstruction()
    }

    override fun visitConstFloat32Instruction(value: Float) {
        delegate?.visitConstFloat32Instruction(value)
    }

    override fun visitConstFloat64Instruction(value: Double) {
        delegate?.visitConstFloat64Instruction(value)
    }

    override fun visitConstInt32Instruction(value: Int) {
        delegate?.visitConstInt32Instruction(value)
    }

    override fun visitConstInt64Instruction(value: Long) {
        delegate?.visitConstInt64Instruction(value)
    }

    override fun visitSimdConstInstruction(value: V128Value) {
        delegate?.visitSimdConstInstruction(value)
    }

    override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value) {
        delegate?.visitSimdShuffleInstruction(opcode, value)
    }

    override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitLoadInstruction(opcode, alignment, offset)
    }

    override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitSimdLoadInstruction(opcode, alignment, offset)
    }

    override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitStoreInstruction(opcode, alignment, offset)
    }

    override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitSimdStoreInstruction(opcode, alignment, offset)
    }

    override fun visitWrapInstruction(opcode: Opcode) {
        delegate?.visitWrapInstruction(opcode)
    }

    override fun visitExtendInstruction(opcode: Opcode) {
        delegate?.visitExtendInstruction(opcode)
    }

    override fun visitDemoteInstruction(opcode: Opcode) {
        delegate?.visitDemoteInstruction(opcode)
    }

    override fun visitPromoteInstruction(opcode: Opcode) {
        delegate?.visitPromoteInstruction(opcode)
    }

    override fun visitReinterpretInstruction(opcode: Opcode) {
        delegate?.visitReinterpretInstruction(opcode)
    }

    override fun visitUnreachableInstruction() {
        delegate?.visitUnreachableInstruction()
    }

    override fun visitNopInstruction() {
        delegate?.visitNopInstruction()
    }

    override fun visitIfInstruction(blockType: BlockType) {
        delegate?.visitIfInstruction(blockType)
    }

    override fun visitLoopInstruction(blockType: BlockType) {
        delegate?.visitLoopInstruction(blockType)
    }

    override fun visitBlockInstruction(blockType: BlockType) {
        delegate?.visitBlockInstruction(blockType)
    }

    override fun visitElseInstruction() {
        delegate?.visitElseInstruction()
    }

    override fun visitTryInstruction(blockType: BlockType) {
        delegate?.visitTryInstruction(blockType)
    }

    override fun visitTryTableInstruction(blockType: BlockType, handlers: List<TryCatchArgument>){
        delegate?.visitTryTableInstruction(blockType, handlers)
    }

    override fun visitCatchInstruction() {
        delegate?.visitCatchInstruction()
    }

    override fun visitThrowRefInstruction() {
        delegate?.visitThrowRefInstruction()
    }

    override fun visitThrowInstruction(exceptionIndex: UInt) {
        delegate?.visitThrowInstruction(exceptionIndex)
    }

    override fun visitRethrowInstruction() {
        delegate?.visitRethrowInstruction()
    }

    override fun visitBrInstruction(depth: UInt) {
        delegate?.visitBrInstruction(depth)
    }

    override fun visitBrIfInstruction(depth: UInt) {
        delegate?.visitBrIfInstruction(depth)
    }

    override fun visitReturnInstruction() {
        delegate?.visitReturnInstruction()
    }

    override fun visitCallInstruction(functionIndex: UInt) {
        delegate?.visitCallInstruction(functionIndex)
    }

    override fun visitCallIndirectInstruction(typeIndex: UInt, reserved: UInt) {
        delegate?.visitCallIndirectInstruction(typeIndex, reserved)
    }

    override fun visitDropInstruction() {
        delegate?.visitDropInstruction()
    }

    override fun visitSelectInstruction() {
        delegate?.visitSelectInstruction()
    }

    override fun visitGetGlobalInstruction(globalIndex: UInt) {
        delegate?.visitGetGlobalInstruction(globalIndex)
    }

    override fun visitSetLocalInstruction(localIndex: UInt) {
        delegate?.visitSetLocalInstruction(localIndex)
    }

    override fun visitTeeLocalInstruction(localIndex: UInt) {
        delegate?.visitTeeLocalInstruction(localIndex)
    }

    override fun visitGetLocalInstruction(localIndex: UInt) {
        delegate?.visitGetLocalInstruction(localIndex)
    }

    override fun visitSetGlobalInstruction(globalIndex: UInt) {
        delegate?.visitSetGlobalInstruction(globalIndex)
    }

    override fun visitMemorySizeInstruction(reserved: UInt) {
        delegate?.visitMemorySizeInstruction(reserved)
    }

    override fun visitMemoryGrowInstruction(reserved: UInt) {
        delegate?.visitMemoryGrowInstruction(reserved)
    }

    override fun visitEqualZeroInstruction(opcode: Opcode) {
        delegate?.visitEqualZeroInstruction(opcode)
    }

    override fun visitEqualInstruction(opcode: Opcode) {
        delegate?.visitEqualInstruction(opcode)
    }

    override fun visitNotEqualInstruction(opcode: Opcode) {
        delegate?.visitNotEqualInstruction(opcode)
    }

    override fun visitLessThanInstruction(opcode: Opcode) {
        delegate?.visitLessThanInstruction(opcode)
    }

    override fun visitLessEqualInstruction(opcode: Opcode) {
        delegate?.visitLessEqualInstruction(opcode)
    }

    override fun visitGreaterThanInstruction(opcode: Opcode) {
        delegate?.visitGreaterThanInstruction(opcode)
    }

    override fun visitGreaterEqualInstruction(opcode: Opcode) {
        delegate?.visitGreaterEqualInstruction(opcode)
    }

    override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        delegate?.visitCountLeadingZerosInstruction(opcode)
    }

    override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        delegate?.visitCountTrailingZerosInstruction(opcode)
    }

    override fun visitPopulationCountInstruction(opcode: Opcode) {
        delegate?.visitPopulationCountInstruction(opcode)
    }

    override fun visitAddInstruction(opcode: Opcode) {
        delegate?.visitAddInstruction(opcode)
    }

    override fun visitSubtractInstruction(opcode: Opcode) {
        delegate?.visitSubtractInstruction(opcode)
    }

    override fun visitMultiplyInstruction(opcode: Opcode) {
        delegate?.visitMultiplyInstruction(opcode)
    }

    override fun visitDivideInstruction(opcode: Opcode) {
        delegate?.visitDivideInstruction(opcode)
    }

    override fun visitRemainderInstruction(opcode: Opcode) {
        delegate?.visitRemainderInstruction(opcode)
    }

    override fun visitAndInstruction(opcode: Opcode) {
        delegate?.visitAndInstruction(opcode)
    }

    override fun visitOrInstruction(opcode: Opcode) {
        delegate?.visitOrInstruction(opcode)
    }

    override fun visitSimdXorInstruction(opcode: Opcode) {
        delegate?.visitSimdXorInstruction(opcode)
    }

    override fun visitShiftLeftInstruction(opcode: Opcode) {
        delegate?.visitShiftLeftInstruction(opcode)
    }

    override fun visitRotateLeftInstruction(opcode: Opcode) {
        delegate?.visitRotateLeftInstruction(opcode)
    }

    override fun visitRotateRightInstruction(opcode: Opcode) {
        delegate?.visitRotateRightInstruction(opcode)
    }

    override fun visitAbsoluteInstruction(opcode: Opcode) {
        delegate?.visitAbsoluteInstruction(opcode)
    }

    override fun visitNegativeInstruction(opcode: Opcode) {
        delegate?.visitNegativeInstruction(opcode)
    }

    override fun visitCeilingInstruction(opcode: Opcode) {
        delegate?.visitCeilingInstruction(opcode)
    }

    override fun visitFloorInstruction(opcode: Opcode) {
        delegate?.visitFloorInstruction(opcode)
    }

    override fun visitTruncateInstruction(opcode: Opcode) {
        delegate?.visitTruncateInstruction(opcode)
    }

    override fun visitNearestInstruction(opcode: Opcode) {
        delegate?.visitNearestInstruction(opcode)
    }

    override fun visitSqrtInstruction(opcode: Opcode) {
        delegate?.visitSqrtInstruction(opcode)
    }

    override fun visitMinInstruction(opcode: Opcode) {
        delegate?.visitMinInstruction(opcode)
    }

    override fun visitMaxInstruction(opcode: Opcode) {
        delegate?.visitMaxInstruction(opcode)
    }

    override fun visitAtomicRmwAddInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwAddInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwAndInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwAndInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwOrInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwOrInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwXorInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwXorInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)
    }

    override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt) {
        delegate?.visitSimdSplatInstruction(opcode, value)
    }

    override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        delegate?.visitSimdExtractLaneInstruction(opcode, index)
    }

    override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt) {
        delegate?.visitSimdReplaceLaneInstruction(opcode, index)
    }

    override fun visitSimdAddInstruction(opcode: Opcode) {
        delegate?.visitSimdAddInstruction(opcode)
    }

    override fun visitSimdSubtractInstruction(opcode: Opcode) {
        delegate?.visitSimdSubtractInstruction(opcode)
    }

    override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        delegate?.visitSimdMultiplyInstruction(opcode)
    }

    override fun visitSimdNegativeInstruction(opcode: Opcode) {
        delegate?.visitSimdNegativeInstruction(opcode)
    }

    override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        delegate?.visitSimdAddSaturateInstruction(opcode)
    }

    override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        delegate?.visitSimdSubtractSaturateInstruction(opcode)
    }

    override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        delegate?.visitSimdShiftLeftInstruction(opcode)
    }

    override fun visitSimdAndInstruction(opcode: Opcode) {
        delegate?.visitSimdAndInstruction(opcode)
    }

    override fun visitSimdOrInstruction(opcode: Opcode) {
        delegate?.visitSimdOrInstruction(opcode)
    }

    override fun visitSimdNotInstruction(opcode: Opcode) {
        delegate?.visitSimdNotInstruction(opcode)
    }

    override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        delegate?.visitSimdBitSelectInstruction(opcode)
    }

    override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        delegate?.visitSimdAllTrueInstruction(opcode)
    }

    override fun visitSimdEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdEqualInstruction(opcode)
    }

    override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdNotEqualInstruction(opcode)
    }

    override fun visitSimdLessThanInstruction(opcode: Opcode) {
        delegate?.visitSimdLessThanInstruction(opcode)
    }

    override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdLessEqualInstruction(opcode)
    }

    override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        delegate?.visitSimdGreaterThanInstruction(opcode)
    }

    override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdGreaterEqualInstruction(opcode)
    }

    override fun visitSimdMinInstruction(opcode: Opcode) {
        delegate?.visitSimdMinInstruction(opcode)
    }

    override fun visitSimdMaxInstruction(opcode: Opcode) {
        delegate?.visitSimdMaxInstruction(opcode)
    }

    override fun visitSimdDivideInstruction(opcode: Opcode) {
        delegate?.visitSimdDivideInstruction(opcode)
    }

    override fun visitSimdSqrtInstruction(opcode: Opcode) {
        delegate?.visitSimdSqrtInstruction(opcode)
    }

    override fun visitSimdConvertInstruction(opcode: Opcode) {
        delegate?.visitSimdConvertInstruction(opcode)
    }

    override fun visitSimdTruncateInstruction(opcode: Opcode) {
        delegate?.visitSimdTruncateInstruction(opcode)
    }

    override fun visitCopySignInstruction(opcode: Opcode) {
        delegate?.visitCopySignInstruction(opcode)
    }

    override fun visitXorInstruction(opcode: Opcode) {
        delegate?.visitXorInstruction(opcode)
    }

    override fun visitSimdAbsInstruction(opcode: Opcode) {
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

    override fun visitDataDropInstruction(segmentIndex: UInt) {
        delegate?.visitDataDropInstruction(segmentIndex)
    }

    override fun visitTableSizeInstruction(tableIndex: UInt) {
        delegate?.visitTableSizeInstruction(tableIndex)
    }

    override fun visitTableGrowInstruction(tableIndex: UInt, value: UInt, delta: UInt) {
        delegate?.visitTableGrowInstruction(tableIndex, value, delta)
    }

    override fun visitTableFillInstruction(tableIndex: UInt) {
        delegate?.visitTableFillInstruction(tableIndex)
    }

    override fun visitTableCopyInstruction(targetTableIndex: UInt, sourceTableIndex: UInt) {
        delegate?.visitTableCopyInstruction(targetTableIndex, sourceTableIndex)
    }

    override fun visitTableInitInstruction(segmentIndex: UInt, tableIndex: UInt) {
        delegate?.visitTableInitInstruction(segmentIndex, tableIndex)
    }

    override fun visitElementDropInstruction(segmentIndex: UInt) {
        delegate?.visitElementDropInstruction(segmentIndex)
    }

    override fun visitAtomicFenceInstruction(reserved: UInt) {
        delegate?.visitAtomicFenceInstruction(reserved)
    }

    override fun visitReferenceEqualInstruction() {
        delegate?.visitReferenceEqualInstruction()
    }

    override fun visitReferenceFunctionInstruction(functionIndex: UInt) {
        delegate?.visitReferenceFunctionInstruction(functionIndex)
    }

    override fun visitReferenceIsNullInstruction() {
        delegate?.visitReferenceIsNullInstruction()
    }

    override fun visitReferenceNullInstruction(type: WasmType) {
        delegate?.visitReferenceNullInstruction(type)
    }

    override fun visitShiftRightInstruction(opcode: Opcode) {
        delegate?.visitShiftRightInstruction(opcode)
    }
}
