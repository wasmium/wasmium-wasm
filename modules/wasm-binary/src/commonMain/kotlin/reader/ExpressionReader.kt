package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.Opcode.*
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.instructions.TryCatchImmediate
import org.wasmium.wasm.binary.tree.instructions.TryCatchImmediate.TryCatchKind
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

@OptIn(ExperimentalUnsignedTypes::class)
public class ExpressionReader(
    private val context: ReaderContext,
) {
    public fun readExpression(source: WasmBinaryReader, expressionVisitor: ExpressionVisitor) {
        var blockDepth = 1u

        while (true) {
            val opcode = source.readOpcode()
            when (opcode) {
                UNREACHABLE -> {
                    expressionVisitor.visitUnreachableInstruction()
                }

                NOP -> {
                    expressionVisitor.visitNopInstruction()
                }

                END -> {
                    expressionVisitor.visitEndInstruction()

                    --blockDepth

                    if (blockDepth <= 0u) {
                        return
                    }
                }

                BLOCK -> {
                    val blockType = source.readBlockType()

                    expressionVisitor.visitBlockInstruction(blockType)

                    ++blockDepth
                }

                LOOP -> {
                    val blockType = source.readBlockType()

                    expressionVisitor.visitLoopInstruction(blockType)

                    ++blockDepth
                }

                IF -> {
                    val blockType = source.readBlockType()

                    expressionVisitor.visitIfInstruction(blockType)

                    ++blockDepth
                }

                ELSE -> {
                    expressionVisitor.visitElseInstruction()
                }

                TRY -> {
                    val blockType = source.readBlockType()

                    expressionVisitor.visitTryInstruction(blockType)
                }

                CATCH -> {
                    expressionVisitor.visitCatchInstruction()
                }

                THROW -> {
                    val exceptionIndex = source.readIndex()

                    expressionVisitor.visitThrowInstruction(exceptionIndex)
                }

                RETHROW -> {
                    expressionVisitor.visitRethrowInstruction()
                }

                THROW_REF -> {
                    expressionVisitor.visitThrowRefInstruction()
                }

                BR -> {
                    val depth = source.readIndex()

                    expressionVisitor.visitBrInstruction(depth)
                }

                BR_IF -> {
                    val depth = source.readIndex()

                    expressionVisitor.visitBrIfInstruction(depth)
                }

                BR_TABLE -> {
                    val numberTargets = source.readVarUInt32()
                    val targets = mutableListOf<UInt>()

                    for (targetIndex in 0u until numberTargets) {
                        val depth = source.readIndex()

                        targets.add(depth)
                    }

                    val defaultTarget = source.readIndex()

                    expressionVisitor.visitBrTableInstruction(targets, defaultTarget)
                }

                RETURN -> {
                    expressionVisitor.visitReturnInstruction()
                }

                CALL -> {
                    val functionIndex = source.readIndex()

                    expressionVisitor.visitCallInstruction(functionIndex)
                }

                CALL_INDIRECT -> {
                    val typeIndex = source.readIndex()

                    val reserved = source.readVarUInt32()
                    if (reserved != 0u) {
                        throw ParserException("Call_indirect reserved value must be 0")
                    }

                    expressionVisitor.visitCallIndirectInstruction(typeIndex, reserved = false)
                }

                DROP -> {
                    expressionVisitor.visitDropInstruction()
                }

                SELECT -> {
                    expressionVisitor.visitSelectInstruction()
                }

                GET_GLOBAL -> {
                    val globalIndex = source.readIndex()

                    expressionVisitor.visitGetGlobalInstruction(globalIndex)
                }

                SET_LOCAL -> {
                    val localIndex = source.readIndex()

                    expressionVisitor.visitSetLocalInstruction(localIndex)
                }

                TEE_LOCAL -> {
                    val localIndex = source.readIndex()

                    expressionVisitor.visitTeeLocalInstruction(localIndex)
                }

                GET_LOCAL -> {
                    val localIndex = source.readIndex()

                    expressionVisitor.visitGetLocalInstruction(localIndex)
                }

                SET_GLOBAL -> {
                    val globalIndex = source.readIndex()

                    expressionVisitor.visitSetGlobalInstruction(globalIndex)
                }

                I32_LOAD,
                I64_LOAD,
                F32_LOAD,
                F64_LOAD,
                I32_LOAD8_S,
                I32_LOAD8_U,
                I32_LOAD16_S,
                I32_LOAD16_U,
                I64_LOAD8_S,
                I64_LOAD8_U,
                I64_LOAD16_S,
                I64_LOAD16_U,
                I64_LOAD32_S,
                I64_LOAD32_U -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitLoadInstruction(opcode, alignment, offset)
                }

                I32_STORE8,
                I32_STORE16,
                I64_STORE8,
                I64_STORE16,
                I64_STORE32,
                I32_STORE,
                I64_STORE,
                F32_STORE,
                F64_STORE -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitStoreInstruction(opcode, alignment, offset)
                }

                MEMORY_SIZE -> {
                    val reserved = source.readVarUInt1()
                    if (reserved != 0u) {
                        throw ParserException("$opcode reserved value must be 0")
                    }

                    expressionVisitor.visitMemorySizeInstruction(reserved = false)
                }

                MEMORY_GROW -> {
                    val reserved = source.readVarUInt1()
                    if (reserved != 0u) {
                        throw ParserException("$opcode reserved value must be 0")
                    }

                    expressionVisitor.visitMemoryGrowInstruction(reserved = false)
                }

                I32_CONST -> {
                    val value = source.readVarInt32()

                    expressionVisitor.visitConstInt32Instruction(value)
                }

                I64_CONST -> {
                    val value = source.readVarInt64()

                    expressionVisitor.visitConstInt64Instruction(value)
                }

                F32_CONST -> {
                    val value = source.readFloat32()

                    expressionVisitor.visitConstFloat32Instruction(value)
                }

                F64_CONST -> {
                    val value = source.readFloat64()

                    expressionVisitor.visitConstFloat64Instruction(value)
                }

                I32_EQZ,
                I64_EQZ -> {
                    expressionVisitor.visitEqualZeroInstruction(opcode)
                }

                F64_LT,
                I32_LT_S,
                I32_LT_U,
                F32_LT,
                I64_LT_S,
                I64_LT_U -> {
                    expressionVisitor.visitLessThanInstruction(opcode)
                }

                I32_EQ,
                I64_EQ,
                F32_EQ,
                F64_EQ -> {
                    expressionVisitor.visitEqualInstruction(opcode)
                }

                I32_NE,
                I64_NE,
                F32_NE,
                F64_NE -> {
                    expressionVisitor.visitNotEqualInstruction(opcode)
                }

                I32_LE_S,
                I32_LE_U,
                I64_LE_S,
                I64_LE_U,
                F32_LE,
                F64_LE -> {
                    expressionVisitor.visitLessEqualInstruction(opcode)
                }

                I32_GT_S,
                I32_GT_U,
                I64_GT_S,
                I64_GT_U,
                F32_GT,
                F64_GT -> {
                    expressionVisitor.visitGreaterThanInstruction(opcode)
                }

                I32_GE_S,
                I32_GE_U,
                I64_GE_S,
                I64_GE_U,
                F32_GE,
                F64_GE -> {
                    expressionVisitor.visitGreaterEqualInstruction(opcode)
                }

                I32_CLZ,
                I64_CLZ -> {
                    expressionVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                I32_CTZ,
                I64_CTZ -> {
                    expressionVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                I32_POPCNT,
                I64_POPCNT -> {
                    expressionVisitor.visitPopulationCountInstruction(opcode)
                }

                I32_SUB,
                I64_SUB,
                F32_SUB,
                F64_SUB -> {
                    expressionVisitor.visitSubtractInstruction(opcode)
                }

                F32_DIV,
                I32_DIV_S,
                I32_DIV_U,
                F64_DIV,
                I64_DIV_S,
                I64_DIV_U -> {
                    expressionVisitor.visitDivideInstruction(opcode)
                }

                I32_REM_S,
                I32_REM_U,
                I64_REM_S,
                I64_REM_U -> {
                    expressionVisitor.visitRemainderInstruction(opcode)
                }

                I32_AND,
                I64_AND -> {
                    expressionVisitor.visitAndInstruction(opcode)
                }

                I32_OR,
                I64_OR -> {
                    expressionVisitor.visitOrInstruction(opcode)
                }

                I32_XOR,
                I64_XOR -> {
                    expressionVisitor.visitXorInstruction(opcode)
                }

                I32_SHR_S,
                I32_SHR_U,
                I64_SHR_U,
                I64_SHR_S -> {
                    expressionVisitor.visitShiftRightInstruction(opcode)
                }

                I32_SHL,
                I64_SHL -> {
                    expressionVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_ROTL,
                I64_ROTL -> {
                    expressionVisitor.visitRotateLeftInstruction(opcode)
                }

                I32_ROTR,
                I64_ROTR -> {
                    expressionVisitor.visitRotateRightInstruction(opcode)
                }

                F32_ABS,
                F64_ABS -> {
                    expressionVisitor.visitAbsoluteInstruction(opcode)
                }

                F32_NEG,
                F64_NEG -> {
                    expressionVisitor.visitNegativeInstruction(opcode)
                }

                F32_CEIL,
                F64_CEIL -> {
                    expressionVisitor.visitCeilingInstruction(opcode)
                }

                F32_FLOOR,
                F64_FLOOR -> {
                    expressionVisitor.visitFloorInstruction(opcode)
                }

                F32_NEAREST,
                F64_NEAREST -> {
                    expressionVisitor.visitNearestInstruction(opcode)
                }

                F32_SQRT,
                F64_SQRT -> {
                    expressionVisitor.visitSqrtInstruction(opcode)
                }

                I32_ADD,
                I64_ADD,
                F32_ADD,
                F64_ADD -> {
                    expressionVisitor.visitAddInstruction(opcode)
                }

                I32_MUL,
                I64_MUL,
                F32_MUL,
                F64_MUL -> {
                    expressionVisitor.visitMultiplyInstruction(opcode)
                }

                F32_MIN,
                F64_MIN -> {
                    expressionVisitor.visitMinInstruction(opcode)
                }

                F32_MAX,
                F64_MAX -> {
                    expressionVisitor.visitMaxInstruction(opcode)
                }

                F32_COPYSIGN,
                F64_COPYSIGN -> {
                    expressionVisitor.visitCopySignInstruction(opcode)
                }

                I32_WRAP_I64 -> {
                    expressionVisitor.visitWrapInstruction(opcode)
                }

                F32_TRUNC,
                F64_TRUNC,
                I32_TRUNC_S_F32,
                I32_TRUNC_U_F32,
                I32_TRUNC_S_F64,
                I32_TRUNC_U_F64,
                I64_TRUNC_S_F32,
                I64_TRUNC_U_F32,
                I64_TRUNC_S_F64,
                I64_TRUNC_U_F64,
                I32_TRUNC_S_SAT_F32,
                I32_TRUNC_U_SAT_F32,
                I32_TRUNC_S_SAT_F64,
                I32_TRUNC_U_SAT_F64,
                I64_TRUNC_S_SAT_F32,
                I64_TRUNC_U_SAT_F32,
                I64_TRUNC_S_SAT_F64,
                I64_TRUNC_U_SAT_F64 -> {
                    expressionVisitor.visitTruncateInstruction(opcode)
                }

                F32_CONVERT_S_I32,
                F32_CONVERT_U_I32,
                F32_CONVERT_S_I64,
                F32_CONVERT_U_I64,
                F64_CONVERT_S_I32,
                F64_CONVERT_U_I32,
                F64_CONVERT_S_I64,
                F64_CONVERT_U_I64 -> {
                    expressionVisitor.visitConvertInstruction(opcode)
                }

                F32_DEMOTE_F64 -> {
                    expressionVisitor.visitDemoteInstruction(opcode)
                }

                F64_PROMOTE_F32 -> {
                    expressionVisitor.visitPromoteInstruction(opcode)
                }

                I32_REINTERPRET_F32,
                I64_REINTERPRET_F64,
                F32_REINTERPRET_I32,
                F64_REINTERPRET_I64 -> {
                    expressionVisitor.visitReinterpretInstruction(opcode)
                }

                I32_EXTEND8_S,
                I32_EXTEND16_S,
                I64_EXTEND8_S,
                I64_EXTEND16_S,
                I64_EXTEND32_S,
                I64_EXTEND_S_I32,
                I64_EXTEND_U_I32 -> {
                    expressionVisitor.visitExtendInstruction(opcode)
                }

                MEMORY_ATOMIC_NOTIFY -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicWakeInstruction(opcode, alignment, offset)
                }

                MEMORY_ATOMIC_WAIT32,
                MEMORY_ATOMIC_WAIT64 -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicWaitInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_LOAD,
                I64_ATOMIC_LOAD,
                I32_ATOMIC_LOAD8_U,
                I32_ATOMIC_LOAD16_U,
                I64_ATOMIC_LOAD8_U,
                I64_ATOMIC_LOAD16_U,
                I64_ATOMIC_LOAD32_U -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicLoadInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_STORE,
                I64_ATOMIC_STORE,
                I32_ATOMIC_STORE8,
                I32_ATOMIC_STORE16,
                I64_ATOMIC_STORE8,
                I64_ATOMIC_STORE16,
                I64_ATOMIC_STORE32 -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicStoreInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_ADD,
                I64_ATOMIC_RMW_ADD,
                I32_ATOMIC_RMW8_U_ADD,
                I32_ATOMIC_RMW16_U_ADD,
                I64_ATOMIC_RMW8_U_ADD,
                I64_ATOMIC_RMW16_U_ADD,
                I64_ATOMIC_RMW32_U_ADD -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwAddInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_SUB,
                I64_ATOMIC_RMW_SUB,
                I32_ATOMIC_RMW8_U_SUB,
                I32_ATOMIC_RMW16_U_SUB,
                I64_ATOMIC_RMW8_U_SUB,
                I64_ATOMIC_RMW16_U_SUB,
                I64_ATOMIC_RMW32_U_SUB -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_AND,
                I64_ATOMIC_RMW_AND,
                I32_ATOMIC_RMW8_U_AND,
                I32_ATOMIC_RMW16_U_AND,
                I64_ATOMIC_RMW8_U_AND,
                I64_ATOMIC_RMW16_U_AND,
                I64_ATOMIC_RMW32_U_AND -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwAndInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_OR,
                I64_ATOMIC_RMW_OR,
                I32_ATOMIC_RMW8_U_OR,
                I32_ATOMIC_RMW16_U_OR,
                I64_ATOMIC_RMW8_U_OR,
                I64_ATOMIC_RMW16_U_OR,
                I64_ATOMIC_RMW32_U_OR -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwOrInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_XOR,
                I64_ATOMIC_RMW_XOR,
                I32_ATOMIC_RMW8_U_XOR,
                I32_ATOMIC_RMW16_U_XOR,
                I64_ATOMIC_RMW8_U_XOR,
                I64_ATOMIC_RMW16_U_XOR,
                I64_ATOMIC_RMW32_U_XOR -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwXorInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_XCHG,
                I64_ATOMIC_RMW_XCHG,
                I32_ATOMIC_RMW8_U_XCHG,
                I32_ATOMIC_RMW16_U_XCHG,
                I64_ATOMIC_RMW8_U_XCHG,
                I64_ATOMIC_RMW16_U_XCHG,
                I64_ATOMIC_RMW32_U_XCHG -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_RMW_CMPXCHG,
                I64_ATOMIC_RMW_CMPXCHG,
                I32_ATOMIC_RMW8_U_CMPXCHG,
                I32_ATOMIC_RMW16_U_CMPXCHG,
                I64_ATOMIC_RMW8_U_CMPXCHG,
                I64_ATOMIC_RMW16_U_CMPXCHG,
                I64_ATOMIC_RMW32_U_CMPXCHG -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
                }

                V128_CONST -> {
                    val value: V128Value = source.readV128()

                    expressionVisitor.visitSimdConstInstruction(value)
                }

                V128_LOAD -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitSimdLoadInstruction(opcode, alignment, offset)
                }

                V128_STORE -> {
                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitSimdStoreInstruction(opcode, alignment, offset)
                }

                I8X16_SPLAT,
                I16X8_SPLAT,
                I32X4_SPLAT,
                I64X2_SPLAT,
                F32X4_SPLAT,
                F64X2_SPLAT -> {
                    val value = source.readVarUInt32()

                    expressionVisitor.visitSimdSplatInstruction(opcode, value)
                }

                I8X16_EXTRACT_LANE_S,
                I8X16_EXTRACT_LANE_U,
                I16X8_EXTRACT_LANE_S,
                I16X8_EXTRACT_LANE_U,
                I32X4_EXTRACT_LANE,
                I64X2_EXTRACT_LANE,
                F32X4_EXTRACT_LANE,
                F64X2_EXTRACT_LANE -> {
                    val index = source.readIndex()

                    expressionVisitor.visitSimdExtractLaneInstruction(opcode, index)
                }

                I8X16_REPLACE_LANE,
                I16X8_REPLACE_LANE,
                I32X4_REPLACE_LANE,
                I64X2_REPLACE_LANE,
                F32X4_REPLACE_LANE,
                F64X2_REPLACE_LANE -> {
                    val index = source.readIndex()

                    expressionVisitor.visitSimdReplaceLaneInstruction(opcode, index)
                }

                V8X16_SHUFFLE -> {
                    val lanesIndex = UIntArray(16) { 0u }

                    for (i in 0u until 16u) {
                        lanesIndex[i.toInt()] = source.readVarUInt32()
                    }

                    expressionVisitor.visitSimdShuffleInstruction(opcode, V128Value(lanesIndex))
                }

                I8X16_ADD_SATURATE_S,
                I8X16_ADD_SATURATE_U,
                I16X8_ADD_SATURATE_S,
                I16X8_ADD_SATURATE_U -> {
                    expressionVisitor.visitSimdAddSaturateInstruction(opcode)
                }

                I8X16_SUB_SATURATE_S,
                I8X16_SUB_SATURATE_U,
                I16X8_SUB_SATURATE_S,
                I16X8_SUB_SATURATE_U -> {
                    expressionVisitor.visitSimdSubtractSaturateInstruction(opcode)
                }

                I8X16_SHL,
                I16X8_SHL,
                I32X4_SHL,
                I64X2_SHL,
                I8X16_SHL_S,
                I8X16_SHL_U,
                I16X8_SHL_S,
                I16X8_SHL_U,
                I32X4_SHL_S,
                I32X4_SHL_U,
                I64X2_SHL_S,
                I64X2_SHL_U -> {
                    expressionVisitor.visitSimdShiftLeftInstruction(opcode)
                }

                V128_AND -> {
                    expressionVisitor.visitSimdAndInstruction(opcode)
                }

                V128_OR -> {
                    expressionVisitor.visitSimdOrInstruction(opcode)
                }

                V128_XOR -> {
                    expressionVisitor.visitSimdXorInstruction(opcode)
                }

                V128_NOT -> {
                    expressionVisitor.visitSimdNotInstruction(opcode)
                }

                V128_BITSELECT -> {
                    expressionVisitor.visitSimdBitSelectInstruction(opcode)
                }

                I8X16_ANY_TRUE,
                I16X8_ANY_TRUE,
                I32X4_ANY_TRUE,
                I64X2_ANY_TRUE,
                I8X16_ALL_TRUE,
                I16X8_ALL_TRUE,
                I32X4_ALL_TRUE,
                I64X2_ALL_TRUE -> {
                    expressionVisitor.visitSimdAllTrueInstruction(opcode)
                }

                I8X16_EQ,
                I16X8_EQ,
                I32X4_EQ,
                F32X4_EQ,
                F64X2_EQ -> {
                    expressionVisitor.visitSimdEqualInstruction(opcode)
                }

                I8X16_NE,
                I16X8_NE,
                I32X4_NE,
                F32X4_NE,
                F64X2_NE -> {
                    expressionVisitor.visitSimdNotEqualInstruction(opcode)
                }

                I8X16_LT_S,
                I8X16_LT_U,
                I16X8_LT_S,
                I16X8_LT_U,
                I32X4_LT_S,
                I32X4_LT_U,
                F32X4_LT,
                F64X2_LT -> {
                    expressionVisitor.visitSimdLessThanInstruction(opcode)
                }

                I8X16_LE_S,
                I8X16_LE_U,
                I16X8_LE_S,
                I16X8_LE_U,
                I32X4_LE_S,
                I32X4_LE_U,
                F32X4_LE,
                F64X2_LE -> {
                    expressionVisitor.visitSimdLessEqualInstruction(opcode)
                }

                I8X16_GT_S,
                I8X16_GT_U,
                I16X8_GT_S,
                I16X8_GT_U,
                I32X4_GT_S,
                I32X4_GT_U,
                F32X4_GT,
                F64X2_GT -> {
                    expressionVisitor.visitSimdGreaterThanInstruction(opcode)
                }

                I8X16_GE_S,
                I8X16_GE_U,
                I16X8_GE_S,
                I16X8_GE_U,
                I32X4_GE_S,
                I32X4_GE_U,
                F32X4_GE,
                F64X2_GE -> {
                    expressionVisitor.visitSimdGreaterEqualInstruction(opcode)
                }

                I8X16_NEG,
                I16X8_NEG,
                I32X4_NEG,
                I64X2_NEG,
                F32X4_NEG,
                F64X2_NEG -> {
                    expressionVisitor.visitSimdNegativeInstruction(opcode)
                }

                F32X4_ABS,
                F64X2_ABS -> {
                    expressionVisitor.visitSimdAbsInstruction(opcode)
                }

                F32X4_MIN,
                F64X2_MIN -> {
                    expressionVisitor.visitSimdMinInstruction(opcode)
                }

                F32X4_MAX,
                F64X2_MAX -> {
                    expressionVisitor.visitSimdMaxInstruction(opcode)
                }

                I8X16_ADD,
                I16X8_ADD,
                I32X4_ADD,
                I64X2_ADD,
                F32X4_ADD,
                F64X2_ADD -> {
                    expressionVisitor.visitSimdAddInstruction(opcode)
                }

                I8X16_SUB,
                I16X8_SUB,
                I32X4_SUB,
                I64X2_SUB,
                F32X4_SUB,
                F64X2_SUB -> {
                    expressionVisitor.visitSimdSubtractInstruction(opcode)
                }

                F32X4_DIV,
                F64X2_DIV -> {
                    expressionVisitor.visitSimdDivideInstruction(opcode)
                }

                I8X16_MUL,
                I16X8_MUL,
                I32X4_MUL,
                F32X4_MUL,
                F64X2_MUL -> {
                    expressionVisitor.visitSimdMultiplyInstruction(opcode)
                }

                F32X4_SQRT,
                F64X2_SQRT -> {
                    expressionVisitor.visitSimdSqrtInstruction(opcode)
                }

                F32X4_CONVERT_S_I32X4,
                F32X4_CONVERT_U_I32X4,
                F64X2_CONVERT_S_I64X2,
                F64X2_CONVERT_U_I64X2 -> {
                    expressionVisitor.visitSimdConvertInstruction(opcode)
                }

                I32X4_TRUNC_S_F32X4_SAT,
                I32X4_TRUNC_U_F32X4_SAT,
                I64X2_TRUNC_S_F64X2_SAT,
                I64X2_TRUNC_U_F64X2_SAT -> {
                    expressionVisitor.visitSimdTruncateInstruction(opcode)
                }

                MEMORY_FILL -> {
                    val memoryIndex = source.readVarUInt32()

                    expressionVisitor.visitMemoryFillInstruction(memoryIndex)
                }

                MEMORY_COPY -> {
                    val targetIndex = source.readVarUInt32()
                    val sourceIndex = source.readVarUInt32()

                    expressionVisitor.visitMemoryCopyInstruction(targetIndex, sourceIndex)
                }

                MEMORY_INIT -> {
                    val segmentIndex = source.readVarUInt32()
                    val memoryIndex = source.readVarUInt32()

                    expressionVisitor.visitMemoryInitInstruction(memoryIndex, segmentIndex)
                }

                TABLE_INIT -> {
                    val segmentIndex = source.readVarUInt32()
                    val tableIndex = source.readVarUInt32()

                    expressionVisitor.visitTableInitInstruction(segmentIndex, tableIndex)
                }

                DATA_DROP -> {
                    val segmentIndex = source.readVarUInt32()

                    expressionVisitor.visitDataDropInstruction(segmentIndex)
                }

                ELEMENT_DROP -> {
                    val segmentIndex = source.readVarUInt32()

                    expressionVisitor.visitElementDropInstruction(segmentIndex)
                }

                TABLE_SIZE -> {
                    val tableIndex = source.readVarUInt32()

                    expressionVisitor.visitTableSizeInstruction(tableIndex)
                }

                TABLE_GROW -> {
                    val tableIndex = source.readVarUInt32()
                    val value = source.readVarUInt32()
                    val delta = source.readVarUInt32()

                    expressionVisitor.visitTableGrowInstruction(tableIndex, value, delta)
                }

                TABLE_FILL -> {
                    val tableIndex = source.readVarUInt32()

                    expressionVisitor.visitTableFillInstruction(tableIndex)
                }

                TABLE_COPY -> {
                    val targetTableIndex = source.readVarUInt32()
                    val sourceTableIndex = source.readVarUInt32()

                    expressionVisitor.visitTableCopyInstruction(targetTableIndex, sourceTableIndex)
                }

                CALL_REF -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid call_ref code: reference types not enabled.")
                    }

                    TODO()
                }

                RETURN_CALL_REF -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid return_call_ref code: reference types not enabled.")
                    }

                    TODO()
                }

                REF_AS_NON_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid ref_as_non_null code: reference types not enabled.")
                    }

                    TODO()
                }

                BR_ON_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid br_on_null code: reference types not enabled.")
                    }

                    TODO()
                }

                BR_ON_NON_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid br_on_non_null code: reference types not enabled.")
                    }

                    TODO()
                }

                RETURN_CALL -> {
                    if (!context.options.features.isTailCallsEnabled) {
                        throw ParserException("Invalid return_call code: tail call not enabled.")
                    }

                    TODO()
                }

                RETURN_CALL_INDIRECT -> {
                    if (!context.options.features.isTailCallsEnabled) {
                        throw ParserException("Invalid return_call_indirect code: tail call not enabled.")
                    }

                    TODO()
                }

                DELEGATE -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid delegate code: exception handling not enabled.")
                    }

                    TODO()
                }

                CATCH_ALL -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch_call code: exception handling not enabled.")
                    }

                    TODO()
                }

                SELECT_T -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid select_t code: reference types not enabled.")
                    }

                    TODO()
                }

                TRY_TABLE -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid try_table code: exception handling not enabled.")
                    }

                    val blockType = source.readBlockType()
                    val numberOfCatches = source.readVarUInt32()

                    val handlers = mutableListOf<TryCatchImmediate>()
                    for(handler in 0u until numberOfCatches) {
                        val opcode = source.readUInt8()

                        when(opcode) {
                            TryCatchKind.CATCH.kindId -> {
                                val tagIndex = source.readVarUInt32()
                                val labelIndex = source.readVarUInt32()

                                handlers.add(TryCatchImmediate(TryCatchKind.CATCH, tagIndex, labelIndex))
                            }
                            TryCatchKind.CATCH_REF.kindId -> {
                                val tagIndex = source.readVarUInt32()
                                val labelIndex = source.readVarUInt32()

                                handlers.add(TryCatchImmediate(TryCatchKind.CATCH, tagIndex, labelIndex))
                            }
                            TryCatchKind.CATCH_ALL.kindId -> {
                                val labelIndex = source.readVarUInt32()

                                handlers.add(TryCatchImmediate(TryCatchKind.CATCH, tag  = null, label = labelIndex))
                            }
                            TryCatchKind.CATCH_ALL_REF.kindId -> {
                                val labelIndex = source.readVarUInt32()

                                handlers.add(TryCatchImmediate(TryCatchKind.CATCH, tag = null, label = labelIndex))
                            }
                            else -> {
                                throw ParserException("Invalid try_table handler opcode: $opcode")
                            }
                        }
                    }

                    expressionVisitor.visitTryTableInstruction(blockType, handlers)
                }

                GET_TABLE -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid table.get code: reference types not enabled.")
                    }

                    TODO()
                }

                SET_TABLE -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid table.set code: reference types not enabled.")
                    }

                    TODO()
                }

                REF_NULL -> {
                    val type = source.readType()

                    expressionVisitor.visitReferenceNullInstruction(type)
                }

                REF_IS_NULL -> {
                    expressionVisitor.visitReferenceIsNullInstruction()
                }

                REF_FUNC -> {
                    val functionIndex = source.readIndex()

                    expressionVisitor.visitReferenceFunctionInstruction(functionIndex)
                }

                REF_EQ -> {
                    expressionVisitor.visitReferenceEqualInstruction()
                }

                ATOMIC_FENCE -> {
                    val reserved = source.readVarUInt32()
                    if (reserved != 0u) {
                        throw ParserException("$opcode reserved value must be 0")
                    }

                    expressionVisitor.visitAtomicFenceInstruction(reserved = false)
                }
            }
        }
    }
}
