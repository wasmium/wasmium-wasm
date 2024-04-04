@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ExpressionWriter(
    private val context: WriterContext,
    private val body: ByteBuffer,
) : ExpressionVisitor {
    private val instructionsBuffer = ByteBuffer()

    public override fun visitEnd() {
        val sectionBuffer = ByteBuffer()
        val buffer = WasmBinaryWriter(sectionBuffer)

        buffer.writeByteArray(instructionsBuffer.toByteArray())

        // payload
        WasmBinaryWriter(body).writeByteArray(sectionBuffer.toByteArray())
    }

    override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitBrTableInstruction(targets: Array<UInt>, defaultTarget: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.BR_TABLE)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(targets.size.toUInt())

        for (target in targets) {
            WasmBinaryWriter(instructionsBuffer).writeIndex(target)
        }

        WasmBinaryWriter(instructionsBuffer).writeIndex(defaultTarget)
    }

    override fun visitCompareInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitConvertInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitEndInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.END)
    }

    override fun visitConstFloat32Instruction(value: Float) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.F32_CONST)
        WasmBinaryWriter(instructionsBuffer).writeFloat32(value)
    }

    override fun visitConstFloat64Instruction(value: Double) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.F64_CONST)
        WasmBinaryWriter(instructionsBuffer).writeFloat64(value)
    }

    override fun visitConstInt32Instruction(value: Int) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.I32_CONST)
        WasmBinaryWriter(instructionsBuffer).writeVarInt32(value)
    }

    override fun visitConstInt64Instruction(value: Long) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.I64_CONST)
        WasmBinaryWriter(instructionsBuffer).writeVarInt64(value)
    }

    override fun visitSimdConstInstruction(value: V128Value) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.V128_CONST)
        WasmBinaryWriter(instructionsBuffer).writeV128(value)
    }

    override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)

        for (index in value.value) {
            WasmBinaryWriter(instructionsBuffer).writeVarUInt32(index)
        }
    }

    override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitWrapInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitExtendInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitDemoteInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitPromoteInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitReinterpretInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitUnreachableInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.UNREACHABLE)
    }

    override fun visitNopInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.NOP)
    }

    override fun visitIfInstruction(types: Array<WasmType>) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.IF)

        for (type in types) {
            WasmBinaryWriter(instructionsBuffer).writeType(type)
        }
    }

    override fun visitLoopInstruction(types: Array<WasmType>) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.LOOP)

        for (type in types) {
            WasmBinaryWriter(instructionsBuffer).writeType(type)
        }
    }

    override fun visitBlockInstruction(types: Array<WasmType>) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.BLOCK)

        for (type in types) {
            WasmBinaryWriter(instructionsBuffer).writeType(type)
        }
    }

    override fun visitElseInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.ELSE)
    }

    override fun visitTryInstruction(types: Array<WasmType>) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.TRY)

        for (type in types) {
            WasmBinaryWriter(instructionsBuffer).writeType(type)
        }
    }

    override fun visitCatchInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.CATCH)
    }

    override fun visitThrowRefInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.THROW_REF)
    }

    override fun visitThrowInstruction(exceptionIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.THROW)
    }

    override fun visitRethrowInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.RETHROW)
    }

    override fun visitBrInstruction(depth: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.BR)
        WasmBinaryWriter(instructionsBuffer).writeIndex(depth)
    }

    override fun visitBrIfInstruction(depth: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.BR_IF)
        WasmBinaryWriter(instructionsBuffer).writeIndex(depth)
    }

    override fun visitReturnInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.RETURN)
    }

    override fun visitCallInstruction(functionIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.CALL)
        WasmBinaryWriter(instructionsBuffer).writeIndex(functionIndex)
    }

    override fun visitCallIndirectInstruction(signatureIndex: UInt, reserved: Boolean) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.CALL_INDIRECT)
        WasmBinaryWriter(instructionsBuffer).writeIndex(signatureIndex)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(0u)
    }

    override fun visitDropInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.DROP)
    }

    override fun visitSelectInstruction() {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.SELECT)
    }

    override fun visitGetGlobalInstruction(globalIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.GET_GLOBAL)
        WasmBinaryWriter(instructionsBuffer).writeIndex(globalIndex)
    }

    override fun visitSetLocalInstruction(localIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.SET_LOCAL)
        WasmBinaryWriter(instructionsBuffer).writeIndex(localIndex)
    }

    override fun visitTeeLocalInstruction(localIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.TEE_LOCAL)
        WasmBinaryWriter(instructionsBuffer).writeIndex(localIndex)
    }

    override fun visitGetLocalInstruction(localIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.GET_LOCAL)
        WasmBinaryWriter(instructionsBuffer).writeIndex(localIndex)
    }

    override fun visitSetGlobalInstruction(globalIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.SET_GLOBAL)
        WasmBinaryWriter(instructionsBuffer).writeIndex(globalIndex)
    }

    override fun visitMemorySizeInstruction(reserved: Boolean) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.MEMORY_SIZE)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt1(0u)
    }

    override fun visitMemoryGrowInstruction(reserved: Boolean) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.MEMORY_GROW)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt1(0u)
    }

    override fun visitEqualZeroInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitEqualInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitNotEqualInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitLessThanInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitLessEqualInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitGreaterThanInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitGreaterEqualInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitPopulationCountInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitAddInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSubtractInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitMultiplyInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitDivideInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitRemainderInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitAndInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitOrInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdXorInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitShiftLeftInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitRotateLeftInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitRotateRightInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitAbsoluteInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitNegativeInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitCeilingInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitFloorInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitTruncateInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitNearestInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSqrtInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitMinInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitMaxInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitAtomicRmwAddInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitAtomicRmwAndInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitAtomicRmwOrInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitAtomicRmwXorInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(alignment)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(offset)
    }

    override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(value)
    }

    override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeIndex(index)
    }

    override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
        WasmBinaryWriter(instructionsBuffer).writeIndex(index)
    }

    override fun visitSimdAddInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdSubtractInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdNegativeInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdAndInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdOrInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdNotInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdEqualInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdLessThanInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdMinInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdMaxInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdDivideInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdSqrtInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdConvertInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdTruncateInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitCopySignInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitXorInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitSimdAbsInstruction(opcode: Opcode) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(opcode)
    }

    override fun visitMemoryFillInstruction(memoryIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.MEMORY_FILL)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(memoryIndex)
    }

    override fun visitMemoryCopyInstruction(targetIndex: UInt, sourceIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.MEMORY_COPY)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(sourceIndex)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(targetIndex)
    }

    override fun visitMemoryInitInstruction(memoryIndex: UInt, segmentIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.MEMORY_INIT)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(memoryIndex)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(segmentIndex)
    }

    override fun visitDataDropInstruction(segmentIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.DATA_DROP)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(segmentIndex)
    }

    override fun visitTableSizeInstruction(tableIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.TABLE_SIZE)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(tableIndex)
    }

    override fun visitTableGrowInstruction(tableIndex: UInt, value: UInt, delta: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.TABLE_GROW)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(tableIndex)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(value)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(delta)
    }

    override fun visitTableFillInstruction(tableIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.TABLE_FILL)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(tableIndex)
    }

    override fun visitTableCopyInstruction(targetTableIndex: UInt, sourceTableIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.TABLE_COPY)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(targetTableIndex)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(sourceTableIndex)
    }

    override fun visitTableInitInstruction(segmentIndex: UInt, tableIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.TABLE_INIT)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(segmentIndex)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(tableIndex)
    }

    override fun visitElementDropInstruction(segmentIndex: UInt) {
        WasmBinaryWriter(instructionsBuffer).writeOpcode(Opcode.ELEMENT_DROP)
        WasmBinaryWriter(instructionsBuffer).writeVarUInt32(segmentIndex)
    }
}
