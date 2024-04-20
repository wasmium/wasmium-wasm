package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.verifier.VerifierContext
import org.wasmium.wasm.binary.verifier.VerifierException
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ConstantExpressionValidator(private val context: VerifierContext) : ExpressionVisitor {

    private fun notConstant() {
        throw VerifierException("Expression is not constant")
    }

    override fun visitEnd() {
        // empty
    }

    override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitBrTableInstruction(targets: List<UInt>, defaultTarget: UInt) {
        notConstant()
    }

    override fun visitCompareInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitConvertInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitEndInstruction() {
        // constant
    }

    override fun visitConstFloat32Instruction(value: Float) {
        // constant
    }

    override fun visitConstFloat64Instruction(value: Double) {
        // constant
    }

    override fun visitConstInt32Instruction(value: Int) {
        // constant
    }

    override fun visitConstInt64Instruction(value: Long) {
        // constant
    }

    override fun visitSimdConstInstruction(value: V128Value) {
        // constant
    }

    override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value) {
        notConstant()
    }

    override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitWrapInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitExtendInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitDemoteInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitPromoteInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitReinterpretInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitUnreachableInstruction() {
        notConstant()
    }

    override fun visitNopInstruction() {
        notConstant()
    }

    override fun visitIfInstruction(types: List<WasmType>) {
        notConstant()
    }

    override fun visitLoopInstruction(types: List<WasmType>) {
        notConstant()
    }

    override fun visitBlockInstruction(types: List<WasmType>) {
        notConstant()
    }

    override fun visitElseInstruction() {
        notConstant()
    }

    override fun visitTryInstruction(types: List<WasmType>) {
        notConstant()
    }

    override fun visitCatchInstruction() {
        notConstant()
    }

    override fun visitThrowRefInstruction() {
        notConstant()
    }

    override fun visitThrowInstruction(exceptionIndex: UInt) {
        notConstant()
    }

    override fun visitRethrowInstruction() {
        notConstant()
    }

    override fun visitBrInstruction(depth: UInt) {
        notConstant()
    }

    override fun visitBrIfInstruction(depth: UInt) {
        notConstant()
    }

    override fun visitReturnInstruction() {
        notConstant()
    }

    override fun visitCallInstruction(functionIndex: UInt) {
        notConstant()
    }

    override fun visitCallIndirectInstruction(signatureIndex: UInt, reserved: Boolean) {
        notConstant()
    }

    override fun visitDropInstruction() {
        notConstant()
    }

    override fun visitSelectInstruction() {
        notConstant()
    }

    override fun visitGetGlobalInstruction(globalIndex: UInt) {
        val globalType = context.globals.getOrElse(globalIndex.toInt()) {
            throw VerifierException("Global index $globalIndex is out of bounds")
        }

        if (globalType.isMutable) {
            notConstant()
        }
    }

    override fun visitSetLocalInstruction(localIndex: UInt) {
        notConstant()
    }

    override fun visitTeeLocalInstruction(localIndex: UInt) {
        notConstant()
    }

    override fun visitGetLocalInstruction(localIndex: UInt) {
        notConstant()
    }

    override fun visitSetGlobalInstruction(globalIndex: UInt) {
        notConstant()
    }

    override fun visitMemorySizeInstruction(reserved: Boolean) {
        notConstant()
    }

    override fun visitMemoryGrowInstruction(reserved: Boolean) {
        notConstant()
    }

    override fun visitEqualZeroInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitEqualInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitNotEqualInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitLessThanInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitLessEqualInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitGreaterThanInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitGreaterEqualInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitPopulationCountInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitAddInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSubtractInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitMultiplyInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitDivideInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitRemainderInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitAndInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitOrInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdXorInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitShiftLeftInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitRotateLeftInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitRotateRightInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitAbsoluteInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitNegativeInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitCeilingInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitFloorInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitTruncateInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitNearestInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSqrtInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitMinInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitMaxInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitAtomicRmwAddInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitAtomicRmwAndInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitAtomicRmwOrInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitAtomicRmwXorInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        notConstant()
    }

    override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt) {
        notConstant()
    }

    override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        notConstant()
    }

    override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt) {
        notConstant()
    }

    override fun visitSimdAddInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdSubtractInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdNegativeInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdAndInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdOrInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdNotInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdEqualInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdLessThanInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdMinInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdMaxInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdDivideInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdSqrtInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdConvertInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdTruncateInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitCopySignInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitXorInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitSimdAbsInstruction(opcode: Opcode) {
        notConstant()
    }

    override fun visitMemoryFillInstruction(memoryIndex: UInt) {
        notConstant()
    }

    override fun visitMemoryCopyInstruction(targetIndex: UInt, sourceIndex: UInt) {
        notConstant()
    }

    override fun visitMemoryInitInstruction(memoryIndex: UInt, segmentIndex: UInt) {
        notConstant()
    }

    override fun visitTableInitInstruction(segmentIndex: UInt, tableIndex: UInt) {
        notConstant()
    }

    override fun visitDataDropInstruction(segmentIndex: UInt) {
        notConstant()
    }

    override fun visitTableSizeInstruction(tableIndex: UInt) {
        notConstant()
    }

    override fun visitTableGrowInstruction(tableIndex: UInt, value: UInt, delta: UInt) {
        notConstant()
    }

    override fun visitTableFillInstruction(tableIndex: UInt) {
        notConstant()
    }

    override fun visitTableCopyInstruction(targetTableIndex: UInt, sourceTableIndex: UInt) {
        notConstant()
    }

    override fun visitElementDropInstruction(segmentIndex: UInt) {
        notConstant()
    }

    override fun visitAtomicFenceInstruction(reserved: Boolean) {
        notConstant()
    }

    override fun visitReferenceEqualInstruction() {
        // constant
    }

    override fun visitReferenceFunctionInstruction(functionIndex: UInt) {
        // constant
    }

    override fun visitReferenceIsNullInstruction() {
        // constant
    }

    override fun visitReferenceNullInstruction(type: WasmType) {
        // constant
    }

    override fun visitShiftRightInstruction(opcode: Opcode) {
        notConstant()
    }
}
