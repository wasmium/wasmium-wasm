package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.BlockType
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.instructions.TryCatchArgument
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

@OptIn(ExperimentalUnsignedTypes::class)
public class ExpressionWriter(
    private val context: WriterContext,
    private val body: ByteBuffer,
) : ExpressionVisitor {
    private val instructionsBuffer = ByteBuffer()
    private val writer = WasmBinaryWriter(instructionsBuffer)

    public override fun visitEnd() {
        val sectionBuffer = ByteBuffer()
        val buffer = WasmBinaryWriter(sectionBuffer)

        buffer.writeByteArray(instructionsBuffer.toByteArray())

        // payload
        WasmBinaryWriter(body).writeByteArray(sectionBuffer.toByteArray())
    }

    override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }


    override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }


    override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }


    override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }


    override fun visitBrTableInstruction(targets: List<UInt>, defaultTarget: UInt): Unit = writer.run {
        writeOpcode(Opcode.BR_TABLE)
        writeVarUInt32(targets.size.toUInt())

        for (target in targets) {
            writeIndex(target)
        }

        writeIndex(defaultTarget)
    }

    override fun visitConvertInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitEndInstruction() {
        writer.writeOpcode(Opcode.END)
    }

    override fun visitConstFloat32Instruction(value: Float): Unit = writer.run {
        writeOpcode(Opcode.F32_CONST)
        writeFloat32(value)
    }

    override fun visitConstFloat64Instruction(value: Double): Unit = writer.run {
        writeOpcode(Opcode.F64_CONST)
        writeFloat64(value)
    }

    override fun visitConstInt32Instruction(value: Int): Unit = writer.run {
        writeOpcode(Opcode.I32_CONST)
        writeVarInt32(value)
    }

    override fun visitConstInt64Instruction(value: Long): Unit = writer.run {
        writeOpcode(Opcode.I64_CONST)
        writeVarInt64(value)
    }

    override fun visitSimdConstInstruction(value: V128Value): Unit = writer.run {
        writeOpcode(Opcode.V128_CONST)
        writeV128(value)
    }

    override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value): Unit = writer.run {
        writeOpcode(opcode)

        for (index in value.value) {
            writeVarUInt32(index)
        }
    }

    override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitWrapInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitExtendInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitDemoteInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitPromoteInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitReinterpretInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitUnreachableInstruction() {
        writer.writeOpcode(Opcode.UNREACHABLE)
    }

    override fun visitNopInstruction() {
        writer.writeOpcode(Opcode.NOP)
    }

    override fun visitIfInstruction(blockType: BlockType): Unit = writer.run {
        writeOpcode(Opcode.IF)

        writeBlockType(blockType)
    }

    override fun visitLoopInstruction(blockType: BlockType): Unit = writer.run {
        writeOpcode(Opcode.LOOP)

        writeBlockType(blockType)
    }

    override fun visitBlockInstruction(blockType: BlockType): Unit = writer.run {
        writeOpcode(Opcode.BLOCK)

        writeBlockType(blockType)
    }

    override fun visitElseInstruction() {
        writer.writeOpcode(Opcode.ELSE)
    }

    override fun visitTryInstruction(blockType: BlockType): Unit = writer.run {
        writeOpcode(Opcode.TRY)

        writeBlockType(blockType)
    }

    override fun visitTryTableInstruction(blockType: BlockType, handlers: List<TryCatchArgument>) {
        writer.writeBlockType(blockType)
        writer.writeVarUInt32(handlers.size.toUInt())

        for (handler in handlers) {
            writer.writeUInt8(handler.kind.kindId)
            writer.writeIndex(handler.label)

            handler.tag?.let {
                writer.writeIndex(handler.tag)
            }
        }
    }

    override fun visitCatchInstruction() {
        writer.writeOpcode(Opcode.CATCH)
    }

    override fun visitThrowRefInstruction() {
        writer.writeOpcode(Opcode.THROW_REF)
    }

    override fun visitThrowInstruction(exceptionIndex: UInt) {
        writer.writeOpcode(Opcode.THROW)
    }

    override fun visitRethrowInstruction() {
        writer.writeOpcode(Opcode.RETHROW)
    }

    override fun visitBrInstruction(depth: UInt): Unit = writer.run {
        writeOpcode(Opcode.BR)
        writeIndex(depth)
    }

    override fun visitBrIfInstruction(depth: UInt): Unit = writer.run {
        writeOpcode(Opcode.BR_IF)
        writeIndex(depth)
    }

    override fun visitReturnInstruction() {
        writer.writeOpcode(Opcode.RETURN)
    }

    override fun visitCallInstruction(functionIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.CALL)
        writeIndex(functionIndex)
    }

    override fun visitCallIndirectInstruction(typeIndex: UInt, reserved: UInt): Unit = writer.run {
        writeOpcode(Opcode.CALL_INDIRECT)
        writeIndex(typeIndex)
        writeVarUInt32(reserved)
    }

    override fun visitDropInstruction() {
        writer.writeOpcode(Opcode.DROP)
    }

    override fun visitSelectInstruction() {
        writer.writeOpcode(Opcode.SELECT)
    }

    override fun visitSelectTypedInstruction(types: List<WasmType>) {
        writer.writeVarUInt32(types.size.toUInt())

        for (type in types) {
            writer.writeType(type)
        }
    }

    override fun visitGetGlobalInstruction(globalIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.GET_GLOBAL)
        writeIndex(globalIndex)
    }

    override fun visitSetLocalInstruction(localIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.SET_LOCAL)
        writeIndex(localIndex)
    }

    override fun visitTeeLocalInstruction(localIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.TEE_LOCAL)
        writeIndex(localIndex)
    }

    override fun visitGetLocalInstruction(localIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.GET_LOCAL)
        writeIndex(localIndex)
    }

    override fun visitSetGlobalInstruction(globalIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.SET_GLOBAL)
        writeIndex(globalIndex)
    }

    override fun visitMemorySizeInstruction(reserved: UInt): Unit = writer.run {
        writeOpcode(Opcode.MEMORY_SIZE)
        writeVarUInt1(reserved)
    }

    override fun visitMemoryGrowInstruction(reserved: UInt): Unit = writer.run {
        writeOpcode(Opcode.MEMORY_GROW)
        writeVarUInt1(reserved)
    }

    override fun visitEqualZeroInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitEqualInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitNotEqualInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitLessThanInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitLessEqualInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitGreaterThanInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitGreaterEqualInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitPopulationCountInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitAddInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSubtractInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitMultiplyInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitDivideInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitRemainderInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitAndInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitOrInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdXorInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitShiftLeftInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitRotateLeftInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitRotateRightInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitAbsoluteInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitNegativeInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitCeilingInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitFloorInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitTruncateInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitNearestInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSqrtInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitMinInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitMaxInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitAtomicRmwAddInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitAtomicRmwAndInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitAtomicRmwOrInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitAtomicRmwXorInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(alignment)
        writeVarUInt32(offset)
    }

    override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeVarUInt32(value)
    }

    override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        writer.apply {
            writeOpcode(opcode)
            writeIndex(index)
        }
    }

    override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt): Unit = writer.run {
        writeOpcode(opcode)
        writeIndex(index)
    }

    override fun visitSimdAddInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdSubtractInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdNegativeInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdAndInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdOrInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdNotInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdEqualInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdLessThanInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdMinInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdMaxInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdDivideInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdSqrtInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdConvertInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdTruncateInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitCopySignInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitXorInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitSimdAbsInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitMemoryFillInstruction(memoryIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.MEMORY_FILL)
        writeVarUInt32(memoryIndex)
    }

    override fun visitMemoryCopyInstruction(targetIndex: UInt, sourceIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.MEMORY_COPY)
        writeVarUInt32(sourceIndex)
        writeVarUInt32(targetIndex)
    }

    override fun visitMemoryInitInstruction(memoryIndex: UInt, segmentIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.MEMORY_INIT)
        writeVarUInt32(memoryIndex)
        writeVarUInt32(segmentIndex)
    }

    override fun visitDataDropInstruction(segmentIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.DATA_DROP)
        writeVarUInt32(segmentIndex)
    }

    override fun visitTableSizeInstruction(tableIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.TABLE_SIZE)
        writeVarUInt32(tableIndex)
    }

    override fun visitTableGrowInstruction(tableIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.TABLE_GROW)
        writeVarUInt32(tableIndex)
    }

    override fun visitGetTableInstruction(tableIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.GET_TABLE)
        writeVarUInt32(tableIndex)
    }

    override fun visitSetTableInstruction(tableIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.SET_TABLE)
        writeVarUInt32(tableIndex)
    }

    override fun visitTableFillInstruction(tableIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.TABLE_FILL)
        writeVarUInt32(tableIndex)
    }

    override fun visitTableCopyInstruction(targetTableIndex: UInt, sourceTableIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.TABLE_COPY)
        writeVarUInt32(targetTableIndex)
        writeVarUInt32(sourceTableIndex)
    }

    override fun visitTableInitInstruction(segmentIndex: UInt, tableIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.TABLE_INIT)
        writeVarUInt32(segmentIndex)
        writeVarUInt32(tableIndex)
    }

    override fun visitElementDropInstruction(segmentIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.ELEMENT_DROP)
        writeVarUInt32(segmentIndex)
    }

    override fun visitAtomicFenceInstruction(reserved: UInt): Unit = writer.run {
        writeOpcode(Opcode.ATOMIC_FENCE)
        writeVarUInt32(reserved)
    }

    override fun visitReferenceEqualInstruction() {
        writer.writeOpcode(Opcode.REF_EQ)
    }

    override fun visitReferenceFunctionInstruction(functionIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.REF_FUNC)
        writeIndex(functionIndex)
    }

    override fun visitReferenceIsNullInstruction() {
        writer.writeOpcode(Opcode.REF_NULL)
    }

    override fun visitReferenceAsNonNullInstruction() {
        writer.writeOpcode(Opcode.REF_AS_NON_NULL)
    }

    override fun visitReferenceNullInstruction(type: WasmType): Unit = writer.run {
        writeOpcode(Opcode.REF_NULL)
        writeType(type)
    }

    override fun visitShiftRightInstruction(opcode: Opcode) {
        writer.writeOpcode(opcode)
    }

    override fun visitBrOnNonNullInstruction(labelIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.REF_NULL)
        writeIndex(labelIndex)
    }

    override fun visitCallRefInstruction(typeIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.REF_NULL)
        writeIndex(typeIndex)
    }

    override fun visitReturnCallRefInstruction(typeIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.REF_NULL)
        writeIndex(typeIndex)
    }

    override fun visitBrOnNullInstruction(labelIndex: UInt): Unit = writer.run {
        writeOpcode(Opcode.REF_NULL)
        writeIndex(labelIndex)
    }
}
