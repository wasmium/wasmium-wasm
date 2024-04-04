package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ExpressionVerifier(private val delegate: ExpressionVisitor, private val context: VerifierContext) : ExpressionVisitor {
    private var done: Boolean = false
    private var numberOfInstructions: UInt = 0u

    override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicLoadInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicStoreInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicWaitInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicWakeInstruction(opcode, alignment, offset)
    }

    override fun visitBrTableInstruction(targets: Array<UInt>, defaultTarget: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitBrTableInstruction(targets, defaultTarget)
    }

    override fun visitCompareInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitCompareInstruction(opcode)
    }

    override fun visitConvertInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitConvertInstruction(opcode)
    }

    override fun visitEndInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitEndInstruction()
    }

    override fun visitConstFloat32Instruction(value: Float) {
        checkEnd()

        numberOfInstructions++

        delegate.visitConstFloat32Instruction(value)
    }

    override fun visitConstFloat64Instruction(value: Double) {
        checkEnd()

        numberOfInstructions++

        delegate.visitConstFloat64Instruction(value)
    }

    override fun visitConstInt32Instruction(value: Int) {
        checkEnd()

        numberOfInstructions++

        delegate.visitConstInt32Instruction(value)
    }

    override fun visitConstInt64Instruction(value: Long) {
        checkEnd()

        numberOfInstructions++

        delegate.visitConstInt64Instruction(value)
    }

    override fun visitSimdConstInstruction(value: V128Value) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdConstInstruction(value)
    }

    override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdShuffleInstruction(opcode, value)
    }

    override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitLoadInstruction(opcode, alignment, offset)
    }

    override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdLoadInstruction(opcode, alignment, offset)
    }

    override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitStoreInstruction(opcode, alignment, offset)
    }

    override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdStoreInstruction(opcode, alignment, offset)
    }

    override fun visitWrapInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitWrapInstruction(opcode)
    }

    override fun visitExtendInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitExtendInstruction(opcode)
    }

    override fun visitDemoteInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitDemoteInstruction(opcode)
    }

    override fun visitPromoteInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitPromoteInstruction(opcode)
    }

    override fun visitReinterpretInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitReinterpretInstruction(opcode)
    }

    override fun visitUnreachableInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitUnreachableInstruction()
    }

    override fun visitNopInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitNopInstruction()
    }

    override fun visitIfInstruction(types: Array<WasmType>) {
        checkEnd()

        numberOfInstructions++

        delegate.visitIfInstruction(types)
    }

    override fun visitLoopInstruction(types: Array<WasmType>) {
        checkEnd()

        numberOfInstructions++

        delegate.visitLoopInstruction(types)
    }

    override fun visitBlockInstruction(types: Array<WasmType>) {
        checkEnd()

        numberOfInstructions++

        delegate.visitBlockInstruction(types)
    }

    override fun visitElseInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitElseInstruction()
    }

    override fun visitTryInstruction(types: Array<WasmType>) {
        checkEnd()

        numberOfInstructions++

        delegate.visitTryInstruction(types)
    }

    override fun visitCatchInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitCatchInstruction()
    }

    override fun visitThrowRefInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitThrowRefInstruction()
    }

    override fun visitThrowInstruction(exceptionIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitThrowInstruction(exceptionIndex)
    }

    override fun visitRethrowInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitRethrowInstruction()
    }

    override fun visitBrInstruction(depth: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitBrInstruction(depth)
    }

    override fun visitBrIfInstruction(depth: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitBrIfInstruction(depth)
    }

    override fun visitReturnInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitReturnInstruction()
    }

    override fun visitCallInstruction(functionIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitCallInstruction(functionIndex)
    }

    override fun visitCallIndirectInstruction(signatureIndex: UInt, reserved: Boolean) {
        checkEnd()

        numberOfInstructions++

        delegate.visitCallIndirectInstruction(signatureIndex, reserved)
    }

    override fun visitDropInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitDropInstruction()
    }

    override fun visitSelectInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate.visitSelectInstruction()
    }

    override fun visitGetGlobalInstruction(globalIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitGetGlobalInstruction(globalIndex)
    }

    override fun visitSetLocalInstruction(localIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSetLocalInstruction(localIndex)
    }

    override fun visitTeeLocalInstruction(localIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitTeeLocalInstruction(localIndex)
    }

    override fun visitGetLocalInstruction(localIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitGetLocalInstruction(localIndex)
    }

    override fun visitSetGlobalInstruction(globalIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSetGlobalInstruction(globalIndex)
    }

    override fun visitMemorySizeInstruction(reserved: Boolean) {
        checkEnd()

        numberOfInstructions++

        delegate.visitMemorySizeInstruction(reserved)
    }

    override fun visitMemoryGrowInstruction(reserved: Boolean) {
        checkEnd()

        numberOfInstructions++

        delegate.visitMemoryGrowInstruction(reserved)
    }

    override fun visitEqualZeroInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitEqualZeroInstruction(opcode)
    }

    override fun visitEqualInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitEqualInstruction(opcode)
    }

    override fun visitNotEqualInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitNotEqualInstruction(opcode)
    }

    override fun visitLessThanInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitLessThanInstruction(opcode)
    }

    override fun visitLessEqualInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitLessEqualInstruction(opcode)
    }

    override fun visitGreaterThanInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitGreaterThanInstruction(opcode)
    }

    override fun visitGreaterEqualInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitGreaterEqualInstruction(opcode)
    }

    override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitCountLeadingZerosInstruction(opcode)
    }

    override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitCountTrailingZerosInstruction(opcode)
    }

    override fun visitPopulationCountInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitPopulationCountInstruction(opcode)
    }

    override fun visitAddInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAddInstruction(opcode)
    }

    override fun visitSubtractInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSubtractInstruction(opcode)
    }

    override fun visitMultiplyInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitMultiplyInstruction(opcode)
    }

    override fun visitDivideInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitDivideInstruction(opcode)
    }

    override fun visitRemainderInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitRemainderInstruction(opcode)
    }

    override fun visitAndInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAndInstruction(opcode)
    }

    override fun visitOrInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitOrInstruction(opcode)
    }

    override fun visitSimdXorInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdXorInstruction(opcode)
    }

    override fun visitShiftLeftInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitShiftLeftInstruction(opcode)
    }

    override fun visitRotateLeftInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitRotateLeftInstruction(opcode)
    }

    override fun visitRotateRightInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitRotateRightInstruction(opcode)
    }

    override fun visitAbsoluteInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAbsoluteInstruction(opcode)
    }

    override fun visitNegativeInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitNegativeInstruction(opcode)
    }

    override fun visitCeilingInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitCeilingInstruction(opcode)
    }

    override fun visitFloorInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitFloorInstruction(opcode)
    }

    override fun visitTruncateInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitTruncateInstruction(opcode)
    }

    override fun visitNearestInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitNearestInstruction(opcode)
    }

    override fun visitSqrtInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSqrtInstruction(opcode)
    }

    override fun visitMinInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitMinInstruction(opcode)
    }

    override fun visitMaxInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitMaxInstruction(opcode)
    }

    override fun visitAtomicRmwAddInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicRmwAddInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwAndInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicRmwAndInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwOrInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicRmwOrInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwXorInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicRmwXorInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)
    }

    override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdSplatInstruction(opcode, value)
    }

    override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdExtractLaneInstruction(opcode, index)
    }

    override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdReplaceLaneInstruction(opcode, index)
    }

    override fun visitSimdAddInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdAddInstruction(opcode)
    }

    override fun visitSimdSubtractInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdSubtractInstruction(opcode)
    }

    override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdMultiplyInstruction(opcode)
    }

    override fun visitSimdNegativeInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdNegativeInstruction(opcode)
    }

    override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdAddSaturateInstruction(opcode)
    }

    override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdSubtractSaturateInstruction(opcode)
    }

    override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdShiftLeftInstruction(opcode)
    }

    override fun visitSimdAndInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdAndInstruction(opcode)
    }

    override fun visitSimdOrInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdOrInstruction(opcode)
    }

    override fun visitSimdNotInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdNotInstruction(opcode)
    }

    override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdBitSelectInstruction(opcode)
    }

    override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdAllTrueInstruction(opcode)
    }

    override fun visitSimdEqualInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdEqualInstruction(opcode)
    }

    override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdNotEqualInstruction(opcode)
    }

    override fun visitSimdLessThanInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdLessThanInstruction(opcode)
    }

    override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdLessEqualInstruction(opcode)
    }

    override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdGreaterThanInstruction(opcode)
    }

    override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdGreaterEqualInstruction(opcode)
    }

    override fun visitSimdMinInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdMinInstruction(opcode)
    }

    override fun visitSimdMaxInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdMaxInstruction(opcode)
    }

    override fun visitSimdDivideInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdDivideInstruction(opcode)
    }

    override fun visitSimdSqrtInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdSqrtInstruction(opcode)
    }

    override fun visitSimdConvertInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdConvertInstruction(opcode)
    }

    override fun visitSimdTruncateInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdTruncateInstruction(opcode)
    }

    override fun visitCopySignInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitCopySignInstruction(opcode)
    }

    override fun visitXorInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitXorInstruction(opcode)
    }

    override fun visitSimdAbsInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate.visitSimdAbsInstruction(opcode)
    }

    override fun visitMemoryFillInstruction(memoryIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitMemoryFillInstruction(memoryIndex)
    }

    override fun visitMemoryCopyInstruction(targetIndex: UInt, sourceIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitMemoryCopyInstruction(targetIndex, sourceIndex)
    }

    override fun visitMemoryInitInstruction(memoryIndex: UInt, segmentIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitMemoryInitInstruction(memoryIndex, segmentIndex)
    }

    override fun visitTableInitInstruction(segmentIndex: UInt, tableIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitTableInitInstruction(segmentIndex, tableIndex)
    }

    override fun visitDataDropInstruction(segmentIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitDataDropInstruction(segmentIndex)
    }

    override fun visitTableSizeInstruction(tableIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitTableSizeInstruction(tableIndex)
    }

    override fun visitTableGrowInstruction(tableIndex: UInt, value: UInt, delta: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitTableGrowInstruction(tableIndex, value, delta)
    }

    override fun visitTableFillInstruction(tableIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitTableFillInstruction(tableIndex)
    }

    override fun visitTableCopyInstruction(targetTableIndex: UInt, sourceTableIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitTableCopyInstruction(targetTableIndex, sourceTableIndex)
    }

    override fun visitElementDropInstruction(segmentIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate.visitElementDropInstruction(segmentIndex)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfInstructions > WasmBinary.MAX_FUNCTION_INSTRUCTIONS) {
            throw VerifierException("Number of function locals $numberOfInstructions exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS}")
        }

        done = true
        delegate.visitEnd()
    }


    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
