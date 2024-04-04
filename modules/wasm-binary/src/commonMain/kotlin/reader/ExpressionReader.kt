@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.tree.Opcode.*
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public class ExpressionReader(
    private val context: ReaderContext,
) {
    public fun readExpression(source: WasmBinaryReader, expressionVisitor: ExpressionVisitor) {
        var depth = 1u

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
                    --depth

                    if (depth <= 0u) {
                        expressionVisitor.visitEnd()
                        return
                    } else {
                        expressionVisitor.visitEndInstruction()
                    }
                }

                BLOCK -> {
                    val type = source.readType()
                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf(WasmType.NONE)
                    expressionVisitor.visitBlockInstruction(blockType)

                    ++depth
                }

                LOOP -> {
                    val type = source.readType()
                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf(WasmType.NONE)
                    expressionVisitor.visitLoopInstruction(blockType)

                    ++depth
                }

                IF -> {
                    val type = source.readType()
                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf(WasmType.NONE)
                    expressionVisitor.visitIfInstruction(blockType)

                    ++depth
                }

                ELSE -> {
                    expressionVisitor.visitElseInstruction()
                }

                TRY -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid try code: exceptions not enabled.")
                    }

                    val type = source.readType()
                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf(WasmType.NONE)
                    expressionVisitor.visitTryInstruction(blockType)
                }

                CATCH -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch code: exceptions not enabled.")
                    }

                    expressionVisitor.visitCatchInstruction()
                }

                THROW -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid throw code: exceptions not enabled.")
                    }

                    val exceptionIndex = source.readIndex()
                    if (exceptionIndex >= context.numberOfTotalExceptions) {
                        throw ParserException("invalid call exception index: %$exceptionIndex")
                    }

                    expressionVisitor.visitThrowInstruction(exceptionIndex)
                }

                RETHROW -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid rethrow code: exceptions not enabled.")
                    }

                    expressionVisitor.visitRethrowInstruction()
                }

                THROW_REF -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch code: exceptions not enabled.")
                    }

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
                    val targets = Array(numberTargets.toInt()) { 0u }

                    for (targetIndex in 0u until numberTargets) {
                        val depth = source.readIndex()

                        targets[targetIndex.toInt()] = depth
                    }

                    val defaultTarget = source.readIndex()

                    expressionVisitor.visitBrTableInstruction(targets, defaultTarget)
                }

                RETURN -> {
                    expressionVisitor.visitReturnInstruction()
                }

                CALL -> {
                    val funcIndex = source.readIndex()

                    if (funcIndex >= context.numberOfTotalFunctions) {
                        throw ParserException("Invalid call function index: %$funcIndex")
                    }

                    expressionVisitor.visitCallInstruction(funcIndex)
                }

                CALL_INDIRECT -> {
                    val signatureIndex = source.readIndex()
                    if (signatureIndex >= context.numberOfSignatures) {
                        throw ParserException("Invalid call_indirect signature index")
                    }

                    val reserved = source.readVarUInt32()
                    if (reserved != 0u) {
                        throw ParserException("Call_indirect reserved value must be 0")
                    }

                    expressionVisitor.visitCallIndirectInstruction(signatureIndex, reserved = false)
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
                        throw ParserException("MemorySize reserved value must be 0.")
                    }
                    expressionVisitor.visitMemorySizeInstruction(reserved = false)
                }

                MEMORY_GROW -> {
                    val reserved = source.readVarUInt1()
                    if (reserved != 0u) {
                        throw ParserException("MemoryGrow reserved value must be 0")
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

                I32_EQZ -> {
                    expressionVisitor.visitEqualZeroInstruction(opcode)
                }

                I32_EQ -> {
                    expressionVisitor.visitEqualInstruction(opcode)
                }

                I32_NE -> {
                    expressionVisitor.visitNotEqualInstruction(opcode)
                }

                I32_LT_S -> {
                    expressionVisitor.visitLessThanInstruction(opcode)
                }

                I32_LE_S -> {
                    expressionVisitor.visitLessEqualInstruction(opcode)
                }

                I32_LT_U -> {
                    expressionVisitor.visitLessThanInstruction(opcode)
                }

                I32_LE_U -> {
                    expressionVisitor.visitLessEqualInstruction(opcode)
                }

                I32_GT_S -> {
                    expressionVisitor.visitGreaterThanInstruction(opcode)
                }

                I32_GE_S -> {
                    expressionVisitor.visitGreaterEqualInstruction(opcode)
                }

                I32_GT_U -> {
                    expressionVisitor.visitGreaterThanInstruction(opcode)
                }

                I32_GE_U -> {
                    expressionVisitor.visitGreaterEqualInstruction(opcode)
                }

                I64_EQZ -> {
                    expressionVisitor.visitEqualZeroInstruction(opcode)
                }

                I64_EQ -> {
                    expressionVisitor.visitEqualInstruction(opcode)
                }

                I64_NE -> {
                    expressionVisitor.visitNotEqualInstruction(opcode)
                }

                I64_LT_S -> {
                    expressionVisitor.visitLessThanInstruction(opcode)
                }

                I64_LE_S -> {
                    expressionVisitor.visitLessEqualInstruction(opcode)
                }

                I64_LT_U -> {
                    expressionVisitor.visitLessThanInstruction(opcode)
                }

                I64_LE_U -> {
                    expressionVisitor.visitLessEqualInstruction(opcode)
                }

                I64_GT_S -> {
                    expressionVisitor.visitGreaterThanInstruction(opcode)
                }

                I64_GE_S -> {
                    expressionVisitor.visitGreaterEqualInstruction(opcode)
                }

                I64_GT_U -> {
                    expressionVisitor.visitGreaterThanInstruction(opcode)
                }

                I64_GE_U -> {
                    expressionVisitor.visitGreaterEqualInstruction(opcode)
                }

                F32_EQ -> {
                    expressionVisitor.visitEqualInstruction(opcode)
                }

                F32_NE -> {
                    expressionVisitor.visitNotEqualInstruction(opcode)
                }

                F32_LT -> {
                    expressionVisitor.visitLessThanInstruction(opcode)
                }

                F32_LE -> {
                    expressionVisitor.visitLessEqualInstruction(opcode)
                }

                F32_GT -> {
                    expressionVisitor.visitGreaterThanInstruction(opcode)
                }

                F32_GE -> {
                    expressionVisitor.visitGreaterEqualInstruction(opcode)
                }

                F64_EQ -> {
                    expressionVisitor.visitEqualInstruction(opcode)
                }

                F64_NE -> {
                    expressionVisitor.visitNotEqualInstruction(opcode)
                }

                F64_LT -> {
                    expressionVisitor.visitLessThanInstruction(opcode)
                }

                F64_LE -> {
                    expressionVisitor.visitLessEqualInstruction(opcode)
                }

                F64_GT -> {
                    expressionVisitor.visitGreaterThanInstruction(opcode)
                }

                F64_GE -> {
                    expressionVisitor.visitCompareInstruction(opcode)
                }

                I32_CLZ -> {
                    expressionVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                I32_CTZ -> {
                    expressionVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                I32_POPCNT -> {
                    expressionVisitor.visitPopulationCountInstruction(opcode)
                }

                I32_ADD -> {
                    expressionVisitor.visitAddInstruction(opcode)
                }

                I32_SUB -> {
                    expressionVisitor.visitSubtractInstruction(opcode)
                }

                I32_MUL -> {
                    expressionVisitor.visitMultiplyInstruction(opcode)
                }

                I32_DIV_S -> {
                    expressionVisitor.visitDivideInstruction(opcode)
                }

                I32_DIV_U -> {
                    expressionVisitor.visitDivideInstruction(opcode)
                }

                I32_REM_S -> {
                    expressionVisitor.visitRemainderInstruction(opcode)
                }

                I32_REM_U -> {
                    expressionVisitor.visitRemainderInstruction(opcode)
                }

                I32_AND -> {
                    expressionVisitor.visitAndInstruction(opcode)
                }

                I32_OR -> {
                    expressionVisitor.visitOrInstruction(opcode)
                }

                I32_XOR -> {
                    expressionVisitor.visitXorInstruction(opcode)
                }

                I32_SHL -> {
                    expressionVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_SHR_U -> {
                    expressionVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_SHR_S -> {
                    expressionVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_ROTL -> {
                    expressionVisitor.visitRotateLeftInstruction(opcode)
                }

                I32_ROTR -> {
                    expressionVisitor.visitRotateRightInstruction(opcode)
                }

                I64_CLZ -> {
                    expressionVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                I64_CTZ -> {
                    expressionVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                I64_POPCNT -> {
                    expressionVisitor.visitPopulationCountInstruction(opcode)
                }

                I64_ADD -> {
                    expressionVisitor.visitAddInstruction(opcode)
                }

                I64_SUB -> {
                    expressionVisitor.visitSubtractInstruction(opcode)
                }

                I64_MUL -> {
                    expressionVisitor.visitMultiplyInstruction(opcode)
                }

                I64_DIV_S -> {
                    expressionVisitor.visitDivideInstruction(opcode)
                }

                I64_DIV_U -> {
                    expressionVisitor.visitDivideInstruction(opcode)
                }

                I64_REM_S -> {
                    expressionVisitor.visitRemainderInstruction(opcode)
                }

                I64_REM_U -> {
                    expressionVisitor.visitRemainderInstruction(opcode)
                }

                I64_AND -> {
                    expressionVisitor.visitAndInstruction(opcode)
                }

                I64_OR -> {
                    expressionVisitor.visitOrInstruction(opcode)
                }

                I64_XOR -> {
                    expressionVisitor.visitXorInstruction(opcode)
                }

                I64_SHL -> {
                    expressionVisitor.visitShiftLeftInstruction(opcode)
                }

                I64_SHR_U -> {
                    expressionVisitor.visitShiftLeftInstruction(opcode)
                }

                I64_SHR_S -> {
                    expressionVisitor.visitShiftLeftInstruction(opcode)
                }

                I64_ROTL -> {
                    expressionVisitor.visitRotateLeftInstruction(opcode)
                }

                I64_ROTR -> {
                    expressionVisitor.visitRotateRightInstruction(opcode)
                }

                F32_ABS -> {
                    expressionVisitor.visitAbsoluteInstruction(opcode)
                }

                F32_NEG -> {
                    expressionVisitor.visitNegativeInstruction(opcode)
                }

                F32_CEIL -> {
                    expressionVisitor.visitCeilingInstruction(opcode)
                }

                F32_FLOOR -> {
                    expressionVisitor.visitFloorInstruction(opcode)
                }

                F32_TRUNC -> {
                    expressionVisitor.visitTruncateInstruction(opcode)
                }

                F32_NEAREST -> {
                    expressionVisitor.visitNearestInstruction(opcode)
                }

                F32_SQRT -> {
                    expressionVisitor.visitSqrtInstruction(opcode)
                }

                F32_ADD -> {
                    expressionVisitor.visitAddInstruction(opcode)
                }

                F32_SUB -> {
                    expressionVisitor.visitSubtractInstruction(opcode)
                }

                F32_MUL -> {
                    expressionVisitor.visitMultiplyInstruction(opcode)
                }

                F32_DIV -> {
                    expressionVisitor.visitDivideInstruction(opcode)
                }

                F32_MIN -> {
                    expressionVisitor.visitMinInstruction(opcode)
                }

                F32_MAX -> {
                    expressionVisitor.visitMaxInstruction(opcode)
                }

                F32_COPYSIGN -> {
                    expressionVisitor.visitCopySignInstruction(opcode)
                }

                F64_ABS -> {
                    expressionVisitor.visitAbsoluteInstruction(opcode)
                }

                F64_NEG -> {
                    expressionVisitor.visitNegativeInstruction(opcode)
                }

                F64_CEIL -> {
                    expressionVisitor.visitCeilingInstruction(opcode)
                }

                F64_FLOOR -> {
                    expressionVisitor.visitFloorInstruction(opcode)
                }

                F64_TRUNC -> {
                    expressionVisitor.visitTruncateInstruction(opcode)
                }

                F64_NEAREST -> {
                    expressionVisitor.visitNearestInstruction(opcode)
                }

                F64_SQRT -> {
                    expressionVisitor.visitSqrtInstruction(opcode)
                }

                F64_ADD -> {
                    expressionVisitor.visitAddInstruction(opcode)
                }

                F64_SUB -> {
                    expressionVisitor.visitSubtractInstruction(opcode)
                }

                F64_MUL -> {
                    expressionVisitor.visitMultiplyInstruction(opcode)
                }

                F64_DIV -> {
                    expressionVisitor.visitDivideInstruction(opcode)
                }

                F64_MIN -> {
                    expressionVisitor.visitMinInstruction(opcode)
                }

                F64_MAX -> {
                    expressionVisitor.visitMaxInstruction(opcode)
                }

                F64_COPYSIGN -> {
                    expressionVisitor.visitCopySignInstruction(opcode)
                }

                I32_WRAP_I64 -> {
                    expressionVisitor.visitWrapInstruction(opcode)
                }

                I32_TRUNC_SF32,
                I32_TRUNC_UF32,
                I32_TRUNC_SF64,
                I32_TRUNC_UF64 -> {
                    expressionVisitor.visitTruncateInstruction(opcode)
                }

                I64_EXTEND_SI32,
                I64_EXTEND_UI32 -> {
                    expressionVisitor.visitExtendInstruction(opcode)
                }

                I64_TRUNC_SF32,
                I64_TRUNC_UF32,
                I64_TRUNC_SF64,
                I64_TRUNC_UF64 -> {
                    expressionVisitor.visitTruncateInstruction(opcode)
                }

                F32_CONVERT_SI32,
                F32_CONVERT_UI32,
                F32_CONVERT_SI64,
                F32_CONVERT_UI64 -> {
                    expressionVisitor.visitConvertInstruction(opcode)
                }

                F32_DEMOTE_F64 -> {
                    expressionVisitor.visitDemoteInstruction(opcode)
                }

                F64_CONVERT_SI32,
                F64_CONVERT_UI32,
                F64_CONVERT_SI64,
                F64_CONVERT_UI64 -> {
                    expressionVisitor.visitConvertInstruction(opcode)
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
                I64_EXTEND32_S -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid $opcode code not enabled.")
                    }

                    expressionVisitor.visitExtendInstruction(opcode)
                }

                I32_TRUNC_S_SAT_F32,
                I32_TRUNC_U_SAT_F32,
                I32_TRUNC_S_SAT_F64,
                I32_TRUNC_U_SAT_F64,
                I64_TRUNC_S_SAT_F32,
                I64_TRUNC_U_SAT_F32,
                I64_TRUNC_S_SAT_F64,
                I64_TRUNC_U_SAT_F64 -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid $opcode code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitTruncateInstruction(opcode)
                }

                MEMORY_ATOMIC_NOTIFY -> {
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid $opcode code: threads not enabled.")
                    }

                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicWakeInstruction(opcode, alignment, offset)
                }

                MEMORY_ATOMIC_WAIT32,
                MEMORY_ATOMIC_WAIT64 -> {
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid $opcode code: threads not enabled.")
                    }

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
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid $opcode code: threads not enabled.")
                    }

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
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

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
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

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
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

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
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

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
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

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
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

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
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

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
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
                }

                V128_CONST -> {
                    if (!context.options.features.isSIMDEnabled) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val value: V128Value = source.readV128()

                    expressionVisitor.visitSimdConstInstruction(value)
                }

                V128_LOAD -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    expressionVisitor.visitSimdLoadInstruction(opcode, alignment, offset)
                }

                V128_STORE -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

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
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

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
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val index = source.readIndex()
                    expressionVisitor.visitSimdExtractLaneInstruction(opcode, index)
                }

                I8X16_REPLACE_LANE,
                I16X8_REPLACE_LANE,
                I32X4_REPLACE_LANE,
                I64X2_REPLACE_LANE,
                F32X4_REPLACE_LANE,
                F64X2_REPLACE_LANE -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val index = source.readIndex()
                    expressionVisitor.visitSimdReplaceLaneInstruction(opcode, index)
                }

                V8X16_SHUFFLE -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val lanesIndex = UIntArray(16) { 0u }

                    for (i in 0u until 16u) {
                        lanesIndex[i.toInt()] = source.readVarUInt32()
                    }

                    expressionVisitor.visitSimdShuffleInstruction(opcode, V128Value(lanesIndex))
                }

                I8X16_ADD,
                I16X8_ADD,
                I32X4_ADD,
                I64X2_ADD -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdAddInstruction(opcode)
                }

                I8X16_SUB,
                I16X8_SUB,
                I32X4_SUB,
                I64X2_SUB -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdSubtractInstruction(opcode)
                }

                I8X16_MUL,
                I16X8_MUL,
                I32X4_MUL -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdMultiplyInstruction(opcode)
                }

                I8X16_NEG,
                I16X8_NEG,
                I32X4_NEG,
                I64X2_NEG -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdNegativeInstruction(opcode)
                }

                I8X16_ADD_SATURATE_S,
                I8X16_ADD_SATURATE_U,
                I16X8_ADD_SATURATE_S,
                I16X8_ADD_SATURATE_U -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdAddSaturateInstruction(opcode)
                }

                I8X16_SUB_SATURATE_S,
                I8X16_SUB_SATURATE_U,
                I16X8_SUB_SATURATE_S,
                I16X8_SUB_SATURATE_U -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

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
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdShiftLeftInstruction(opcode)
                }

                V128_AND -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdAndInstruction(opcode)
                }

                V128_OR -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdOrInstruction(opcode)
                }

                V128_XOR -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdXorInstruction(opcode)
                }

                V128_NOT -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdNotInstruction(opcode)
                }

                V128_BITSELECT -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

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
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdAllTrueInstruction(opcode)
                }

                I8X16_EQ,
                I16X8_EQ,
                I32X4_EQ,
                F32X4_EQ,
                F64X2_EQ -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdEqualInstruction(opcode)
                }

                I8X16_NE,
                I16X8_NE,
                I32X4_NE,
                F32X4_NE,
                F64X2_NE -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

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
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

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
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

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
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

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
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdGreaterEqualInstruction(opcode)
                }

                F32X4_NEG,
                F64X2_NEG -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdNegativeInstruction(opcode)
                }

                F32X4_ABS,
                F64X2_ABS -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdAbsInstruction(opcode)
                }

                F32X4_MIN,
                F64X2_MIN -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdMinInstruction(opcode)
                }

                F32X4_MAX,
                F64X2_MAX -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdMaxInstruction(opcode)
                }

                F32X4_ADD,
                F64X2_ADD -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdAddInstruction(opcode)
                }

                F32X4_SUB,
                F64X2_SUB -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdSubtractInstruction(opcode)
                }

                F32X4_DIV,
                F64X2_DIV -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdDivideInstruction(opcode)
                }

                F32X4_MUL,
                F64X2_MUL -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdMultiplyInstruction(opcode)
                }

                F32X4_SQRT,
                F64X2_SQRT -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdSqrtInstruction(opcode)
                }

                F32X4_CONVERT_S_I32X4,
                F32X4_CONVERT_U_I32X4,
                F64X2_CONVERT_S_I64X2,
                F64X2_CONVERT_U_I64X2 -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdConvertInstruction(opcode)
                }

                I32X4_TRUNC_S_F32X4_SAT,
                I32X4_TRUNC_U_F32X4_SAT,
                I64X2_TRUNC_S_F64X2_SAT,
                I64X2_TRUNC_U_F64X2_SAT -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    expressionVisitor.visitSimdTruncateInstruction(opcode)
                }

                MEMORY_FILL -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid memory.fill code: bulk memory not enabled.")
                    }

                    val memoryIndex = source.readVarUInt32()

                    expressionVisitor.visitMemoryFillInstruction(memoryIndex)
                }

                MEMORY_COPY -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid memory.copy code: bulk memory not enabled.")
                    }

                    val targetIndex = source.readVarUInt32()
                    val sourceIndex = source.readVarUInt32()

                    expressionVisitor.visitMemoryCopyInstruction(targetIndex, sourceIndex)
                }

                MEMORY_INIT -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid memory.init code: bulk memory not enabled.")
                    }

                    val segmentIndex = source.readVarUInt32()
                    val memoryIndex = source.readVarUInt32()

                    expressionVisitor.visitMemoryInitInstruction(memoryIndex, segmentIndex)
                }

                TABLE_INIT -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid table.init code: bulk memory not enabled.")
                    }

                    val segmentIndex = source.readVarUInt32()
                    val tableIndex = source.readVarUInt32()

                    expressionVisitor.visitTableInitInstruction(segmentIndex, tableIndex)
                }

                DATA_DROP -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid data.drop code: bulk memory not enabled.")
                    }

                    val segmentIndex = source.readVarUInt32()

                    expressionVisitor.visitDataDropInstruction(segmentIndex)
                }

                ELEMENT_DROP -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid elem.drop code: bulk memory not enabled.")
                    }

                    val segmentIndex = source.readVarUInt32()

                    expressionVisitor.visitElementDropInstruction(segmentIndex)
                }

                TABLE_SIZE -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid table.size code: bulk memory not enabled.")
                    }

                    val tableIndex = source.readVarUInt32()
                    // TODO check table index

                    expressionVisitor.visitTableSizeInstruction(tableIndex)
                }

                TABLE_GROW -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid table.grow code: bulk memory not enabled.")
                    }

                    val tableIndex = source.readVarUInt32()
                    // TODO check table index

                    val value = source.readVarUInt32()
                    val delta = source.readVarUInt32()

                    expressionVisitor.visitTableGrowInstruction(tableIndex, value, delta)
                }

                TABLE_FILL -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid table.fill code: bulk memory not enabled.")
                    }

                    val tableIndex = source.readVarUInt32()
                    // TODO check table index

                    expressionVisitor.visitTableFillInstruction(tableIndex)
                }

                TABLE_COPY -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid table.copy code: bulk memory not enabled.")
                    }

                    val targetTableIndex = source.readVarUInt32()
                    // TODO check table index
                    val sourceTableIndex = source.readVarUInt32()
                    // TODO check table index

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

                    TODO()
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
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid ref_null code: reference types not enabled.")
                    }

                    val type = source.readType()
                    expressionVisitor.visitReferenceNullInstruction(type)
                }

                REF_IS_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid ref_is_null code: reference types not enabled.")
                    }

                    expressionVisitor.visitReferenceIsNullInstruction()
                }

                REF_FUNC -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid ref_func code: reference types not enabled.")
                    }

                    val functionIndex = source.readIndex()

                    expressionVisitor.visitReferenceFunctionInstruction(functionIndex)
                }

                REF_EQ -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid ref_eq code: reference types not enabled.")
                    }

                    expressionVisitor.visitReferenceEqualInstruction()
                }

                ATOMIC_FENCE -> {
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid atomic.fence code: threads not enabled.")
                    }

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
