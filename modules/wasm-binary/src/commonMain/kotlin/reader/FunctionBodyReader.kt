@file:OptIn(ExperimentalUnsignedTypes::class)

package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.WasmSource
import org.wasmium.wasm.binary.toHexString
import org.wasmium.wasm.binary.tree.Opcode.*
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.FunctionBodyVisitor

public class FunctionBodyReader(
    private val context: ReaderContext,
) {
    public fun readFunctionBody(source: WasmSource, bodySize: UInt, functionBodyVisitor: FunctionBodyVisitor) {
        val endBodyPosition = source.position + bodySize

        var seenEndOpcode = false
        functionBodyVisitor.visitCode()

        var numberOfInstructions = 0u
        while (endBodyPosition - source.position > 0u) {
            val opcode = source.readOpcode()

            numberOfInstructions++
            seenEndOpcode = false

            when (opcode) {
                UNREACHABLE -> {
                    functionBodyVisitor.visitUnreachableInstruction()
                }

                NOP -> {
                    functionBodyVisitor.visitNopInstruction()
                }

                BLOCK -> {
                    val type = source.readType()

                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitBlockInstruction(blockType)
                }

                LOOP -> {
                    val type = source.readType()

                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitLoopInstruction(blockType)
                }

                IF -> {
                    val type = source.readType()

                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()
                    functionBodyVisitor.visitIfInstruction(blockType)
                }

                ELSE -> {
                    functionBodyVisitor.visitElseInstruction()
                }

                TRY -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid try code: exceptions not enabled.")
                    }

                    val type = source.readType()
                    if (!type.isInlineType()) {
                        throw ParserException("Expected valid block signature type")
                    }

                    val blockType = if (type != WasmType.NONE) arrayOf(type) else arrayOf()

                    functionBodyVisitor.visitTryInstruction(blockType)
                }

                CATCH -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch code: exceptions not enabled.")
                    }

                    functionBodyVisitor.visitCatchInstruction()
                }

                THROW -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid throw code: exceptions not enabled.")
                    }

                    val exceptionIndex = source.readIndex()
                    if (exceptionIndex >= context.numberTotalExceptions) {
                        throw ParserException("invalid call exception index: %$exceptionIndex")
                    }

                    functionBodyVisitor.visitThrowInstruction(exceptionIndex)
                }

                RETHROW -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid rethrow code: exceptions not enabled.")
                    }

                    functionBodyVisitor.visitRethrowInstruction()
                }

                THROW_REF -> {
                    if (!context.options.features.isExceptionHandlingEnabled) {
                        throw ParserException("Invalid catch code: exceptions not enabled.")
                    }

                    functionBodyVisitor.visitThrowRefInstruction()
                }

                END -> {
                    if (endBodyPosition == source.position) {
                        seenEndOpcode = true
                        functionBodyVisitor.visitEndFunctionInstruction()
                    } else {
                        functionBodyVisitor.visitEndInstruction()
                    }
                }

                BR -> {
                    val depth = source.readIndex()

                    functionBodyVisitor.visitBrInstruction(depth)
                }

                BR_IF -> {
                    val depth = source.readIndex()

                    functionBodyVisitor.visitBrIfInstruction(depth)
                }

                BR_TABLE -> {
                    val numberTargets = source.readVarUInt32()
                    val targets = Array(numberTargets.toInt()) { 0u }

                    for (targetIndex in 0u until numberTargets) {
                        val depth = source.readIndex()

                        targets[targetIndex.toInt()] = depth
                    }

                    val defaultTarget = source.readIndex()

                    functionBodyVisitor.visitBrTableInstruction(targets, defaultTarget)
                }

                RETURN -> {
                    functionBodyVisitor.visitReturnInstruction()
                }

                CALL -> {
                    val funcIndex = source.readIndex()

                    if (funcIndex >= context.numberTotalFunctions) {
                        throw ParserException("Invalid call function index: %$funcIndex")
                    }

                    functionBodyVisitor.visitCallInstruction(funcIndex)
                }

                CALL_INDIRECT -> {
                    val signatureIndex = source.readIndex()
                    if (signatureIndex >= context.numberSignatures) {
                        throw ParserException("Invalid call_indirect signature index")
                    }

                    val reserved = source.readVarUInt32()
                    if (reserved != 0u) {
                        throw ParserException("Call_indirect reserved value must be 0")
                    }

                    functionBodyVisitor.visitCallIndirectInstruction(signatureIndex, reserved = false)
                }

                DROP -> {
                    functionBodyVisitor.visitDropInstruction()
                }

                SELECT -> {
                    functionBodyVisitor.visitSelectInstruction()
                }

                GET_GLOBAL -> {
                    val globalIndex = source.readIndex()

                    functionBodyVisitor.visitGetGlobalInstruction(globalIndex)
                }

                SET_LOCAL -> {
                    val localIndex = source.readIndex()

                    functionBodyVisitor.visitSetLocalInstruction(localIndex)
                }

                TEE_LOCAL -> {
                    val localIndex = source.readIndex()

                    functionBodyVisitor.visitTeeLocalInstruction(localIndex)
                }

                GET_LOCAL -> {
                    val localIndex = source.readIndex()

                    functionBodyVisitor.visitGetLocalInstruction(localIndex)
                }

                SET_GLOBAL -> {
                    val globalIndex = source.readIndex()

                    functionBodyVisitor.visitSetGlobalInstruction(globalIndex)
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

                    functionBodyVisitor.visitLoadInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitStoreInstruction(opcode, alignment, offset)
                }

                MEMORY_SIZE -> {
                    val reserved = source.readVarUInt1()
                    if (reserved != 0u) {
                        throw ParserException("MemorySize reserved value must be 0.")
                    }
                    functionBodyVisitor.visitMemorySizeInstruction(reserved = false)
                }

                MEMORY_GROW -> {
                    val reserved = source.readVarUInt1()
                    if (reserved != 0u) {
                        throw ParserException("MemoryGrow reserved value must be 0")
                    }

                    functionBodyVisitor.visitMemoryGrowInstruction(reserved = false)
                }

                I32_CONST -> {
                    val value = source.readVarInt32()

                    functionBodyVisitor.visitConstInt32Instruction(value)
                }

                I64_CONST -> {
                    val value = source.readVarInt64()

                    functionBodyVisitor.visitConstInt64Instruction(value)
                }

                F32_CONST -> {
                    val value = source.readFloat32()

                    functionBodyVisitor.visitConstFloat32Instruction(value)
                }

                F64_CONST -> {
                    val value = source.readFloat64()

                    functionBodyVisitor.visitConstFloat64Instruction(value)
                }

                I32_EQZ -> {
                    functionBodyVisitor.visitEqualZeroInstruction(opcode)
                }

                I32_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                I32_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                I32_LT_S -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                I32_LE_S -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                I32_LT_U -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                I32_LE_U -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                I32_GT_S -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                I32_GE_S -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                I32_GT_U -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                I32_GE_U -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                I64_EQZ -> {
                    functionBodyVisitor.visitEqualZeroInstruction(opcode)
                }

                I64_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                I64_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                I64_LT_S -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                I64_LE_S -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                I64_LT_U -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                I64_LE_U -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                I64_GT_S -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                I64_GE_S -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                I64_GT_U -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                I64_GE_U -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                F32_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                F32_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                F32_LT -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                F32_LE -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                F32_GT -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                F32_GE -> {
                    functionBodyVisitor.visitGreaterEqualInstruction(opcode)
                }

                F64_EQ -> {
                    functionBodyVisitor.visitEqualInstruction(opcode)
                }

                F64_NE -> {
                    functionBodyVisitor.visitNotEqualInstruction(opcode)
                }

                F64_LT -> {
                    functionBodyVisitor.visitLessThanInstruction(opcode)
                }

                F64_LE -> {
                    functionBodyVisitor.visitLessEqualInstruction(opcode)
                }

                F64_GT -> {
                    functionBodyVisitor.visitGreaterThanInstruction(opcode)
                }

                F64_GE -> {
                    functionBodyVisitor.visitCompareInstruction(opcode)
                }

                I32_CLZ -> {
                    functionBodyVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                I32_CTZ -> {
                    functionBodyVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                I32_POPCNT -> {
                    functionBodyVisitor.visitPopulationCountInstruction(opcode)
                }

                I32_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                I32_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                I32_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                I32_DIV_S -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                I32_DIV_U -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                I32_REM_S -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                I32_REM_U -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                I32_AND -> {
                    functionBodyVisitor.visitAndInstruction(opcode)
                }

                I32_OR -> {
                    functionBodyVisitor.visitOrInstruction(opcode)
                }

                I32_XOR -> {
                    functionBodyVisitor.visitXorInstruction(opcode)
                }

                I32_SHL -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_SHR_U -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_SHR_S -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I32_ROTL -> {
                    functionBodyVisitor.visitRotateLeftInstruction(opcode)
                }

                I32_ROTR -> {
                    functionBodyVisitor.visitRotateRightInstruction(opcode)
                }

                I64_CLZ -> {
                    functionBodyVisitor.visitCountLeadingZerosInstruction(opcode)
                }

                I64_CTZ -> {
                    functionBodyVisitor.visitCountTrailingZerosInstruction(opcode)
                }

                I64_POPCNT -> {
                    functionBodyVisitor.visitPopulationCountInstruction(opcode)
                }

                I64_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                I64_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                I64_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                I64_DIV_S -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                I64_DIV_U -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                I64_REM_S -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                I64_REM_U -> {
                    functionBodyVisitor.visitRemainderInstruction(opcode)
                }

                I64_AND -> {
                    functionBodyVisitor.visitAndInstruction(opcode)
                }

                I64_OR -> {
                    functionBodyVisitor.visitOrInstruction(opcode)
                }

                I64_XOR -> {
                    functionBodyVisitor.visitXorInstruction(opcode)
                }

                I64_SHL -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I64_SHR_U -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I64_SHR_S -> {
                    functionBodyVisitor.visitShiftLeftInstruction(opcode)
                }

                I64_ROTL -> {
                    functionBodyVisitor.visitRotateLeftInstruction(opcode)
                }

                I64_ROTR -> {
                    functionBodyVisitor.visitRotateRightInstruction(opcode)
                }

                F32_ABS -> {
                    functionBodyVisitor.visitAbsoluteInstruction(opcode)
                }

                F32_NEG -> {
                    functionBodyVisitor.visitNegativeInstruction(opcode)
                }

                F32_CEIL -> {
                    functionBodyVisitor.visitCeilingInstruction(opcode)
                }

                F32_FLOOR -> {
                    functionBodyVisitor.visitFloorInstruction(opcode)
                }

                F32_TRUNC -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                F32_NEAREST -> {
                    functionBodyVisitor.visitNearestInstruction(opcode)
                }

                F32_SQRT -> {
                    functionBodyVisitor.visitSqrtInstruction(opcode)
                }

                F32_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                F32_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                F32_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                F32_DIV -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                F32_MIN -> {
                    functionBodyVisitor.visitMinInstruction(opcode)
                }

                F32_MAX -> {
                    functionBodyVisitor.visitMaxInstruction(opcode)
                }

                F32_COPYSIGN -> {
                    functionBodyVisitor.visitCopySignInstruction(opcode)
                }

                F64_ABS -> {
                    functionBodyVisitor.visitAbsoluteInstruction(opcode)
                }

                F64_NEG -> {
                    functionBodyVisitor.visitNegativeInstruction(opcode)
                }

                F64_CEIL -> {
                    functionBodyVisitor.visitCeilingInstruction(opcode)
                }

                F64_FLOOR -> {
                    functionBodyVisitor.visitFloorInstruction(opcode)
                }

                F64_TRUNC -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                F64_NEAREST -> {
                    functionBodyVisitor.visitNearestInstruction(opcode)
                }

                F64_SQRT -> {
                    functionBodyVisitor.visitSqrtInstruction(opcode)
                }

                F64_ADD -> {
                    functionBodyVisitor.visitAddInstruction(opcode)
                }

                F64_SUB -> {
                    functionBodyVisitor.visitSubtractInstruction(opcode)
                }

                F64_MUL -> {
                    functionBodyVisitor.visitMultiplyInstruction(opcode)
                }

                F64_DIV -> {
                    functionBodyVisitor.visitDivideInstruction(opcode)
                }

                F64_MIN -> {
                    functionBodyVisitor.visitMinInstruction(opcode)
                }

                F64_MAX -> {
                    functionBodyVisitor.visitMaxInstruction(opcode)
                }

                F64_COPYSIGN -> {
                    functionBodyVisitor.visitCopySignInstruction(opcode)
                }

                I32_WRAP_I64 -> {
                    functionBodyVisitor.visitWrapInstruction(opcode)
                }

                I32_TRUNC_SF32,
                I32_TRUNC_UF32,
                I32_TRUNC_SF64,
                I32_TRUNC_UF64 -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                I64_EXTEND_SI32,
                I64_EXTEND_UI32 -> {
                    functionBodyVisitor.visitExtendInstruction(opcode)
                }

                I64_TRUNC_SF32,
                I64_TRUNC_UF32,
                I64_TRUNC_SF64,
                I64_TRUNC_UF64 -> {
                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                F32_CONVERT_SI32,
                F32_CONVERT_UI32,
                F32_CONVERT_SI64,
                F32_CONVERT_UI64 -> {
                    functionBodyVisitor.visitConvertInstruction(opcode)
                }

                F32_DEMOTE_F64 -> {
                    functionBodyVisitor.visitDemoteInstruction(opcode)
                }

                F64_CONVERT_SI32,
                F64_CONVERT_UI32,
                F64_CONVERT_SI64,
                F64_CONVERT_UI64 -> {
                    functionBodyVisitor.visitConvertInstruction(opcode)
                }

                F64_PROMOTE_F32 -> {
                    functionBodyVisitor.visitPromoteInstruction(opcode)
                }

                I32_REINTERPRET_F32,
                I64_REINTERPRET_F64,
                F32_REINTERPRET_I32,
                F64_REINTERPRET_I64 -> {
                    functionBodyVisitor.visitReinterpretInstruction(opcode)
                }

                I32_EXTEND8_S,
                I32_EXTEND16_S,
                I64_EXTEND8_S,
                I64_EXTEND16_S,
                I64_EXTEND32_S -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid opcode: ${opcode.code} not enabled.")
                    }

                    functionBodyVisitor.visitExtendInstruction(opcode)
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
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitTruncateInstruction(opcode)
                }

                MEMORY_ATOMIC_NOTIFY -> {
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicWakeInstruction(opcode, alignment, offset)
                }

                MEMORY_ATOMIC_WAIT32,
                MEMORY_ATOMIC_WAIT64 -> {
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicWaitInstruction(opcode, alignment, offset)
                }

                I32_ATOMIC_LOAD,
                I64_ATOMIC_LOAD,
                I32_ATOMIC_LOAD8_U,
                I32_ATOMIC_LOAD16_U,
                I64_ATOMIC_LOAD8_U,
                I64_ATOMIC_LOAD16_U,
                I64_ATOMIC_LOAD32_U -> {
                    if (!context.options.features.isThreadsEnabled) {
                        throw ParserException("Invalid wake code: threads not enabled.")
                    }

                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    functionBodyVisitor.visitAtomicLoadInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitAtomicStoreInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitAtomicRmwAddInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitAtomicRmwAndInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitAtomicRmwOrInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitAtomicRmwXorInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
                }

                V128_CONST -> {
                    if (!context.options.features.isSIMDEnabled) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val value: V128Value = source.readV128()

                    functionBodyVisitor.visitSimdConstInstruction(value)
                }

                V128_LOAD -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    functionBodyVisitor.visitSimdLoadInstruction(opcode, alignment, offset)
                }

                V128_STORE -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val alignment = source.readVarUInt32()
                    val offset = source.readVarUInt32()

                    functionBodyVisitor.visitSimdStoreInstruction(opcode, alignment, offset)
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

                    functionBodyVisitor.visitSimdSplatInstruction(opcode, value)
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
                    functionBodyVisitor.visitSimdExtractLaneInstruction(opcode, index)
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
                    functionBodyVisitor.visitSimdReplaceLaneInstruction(opcode, index)
                }

                V8X16_SHUFFLE -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    val lanesIndex = UIntArray(16) { 0u }

                    for (i in 0u until 16u) {
                        lanesIndex[i.toInt()] = source.readVarUInt32()
                    }

                    functionBodyVisitor.visitSimdShuffleInstruction(opcode, V128Value(lanesIndex))
                }

                I8X16_ADD,
                I16X8_ADD,
                I32X4_ADD,
                I64X2_ADD -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAddInstruction(opcode)
                }

                I8X16_SUB,
                I16X8_SUB,
                I32X4_SUB,
                I64X2_SUB -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSubtractInstruction(opcode)
                }

                I8X16_MUL,
                I16X8_MUL,
                I32X4_MUL -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMultiplyInstruction(opcode)
                }

                I8X16_NEG,
                I16X8_NEG,
                I32X4_NEG,
                I64X2_NEG -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNegativeInstruction(opcode)
                }

                I8X16_ADD_SATURATE_S,
                I8X16_ADD_SATURATE_U,
                I16X8_ADD_SATURATE_S,
                I16X8_ADD_SATURATE_U -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAddSaturateInstruction(opcode)
                }

                I8X16_SUB_SATURATE_S,
                I8X16_SUB_SATURATE_U,
                I16X8_SUB_SATURATE_S,
                I16X8_SUB_SATURATE_U -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSubtractSaturateInstruction(opcode)
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

                    functionBodyVisitor.visitSimdShiftLeftInstruction(opcode)
                }

                V128_AND -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAndInstruction(opcode)
                }

                V128_OR -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdOrInstruction(opcode)
                }

                V128_XOR -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdXorInstruction(opcode)
                }

                V128_NOT -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNotInstruction(opcode)
                }

                V128_BITSELECT -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid V128Value code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdBitSelectInstruction(opcode)
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

                    functionBodyVisitor.visitSimdAllTrueInstruction(opcode)
                }

                I8X16_EQ,
                I16X8_EQ,
                I32X4_EQ,
                F32X4_EQ,
                F64X2_EQ -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdEqualInstruction(opcode)
                }

                I8X16_NE,
                I16X8_NE,
                I32X4_NE,
                F32X4_NE,
                F64X2_NE -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNotEqualInstruction(opcode)
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

                    functionBodyVisitor.visitSimdLessThanInstruction(opcode)
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

                    functionBodyVisitor.visitSimdLessEqualInstruction(opcode)
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

                    functionBodyVisitor.visitSimdGreaterThanInstruction(opcode)
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

                    functionBodyVisitor.visitSimdGreaterEqualInstruction(opcode)
                }

                F32X4_NEG,
                F64X2_NEG -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdNegativeInstruction(opcode)
                }

                F32X4_ABS,
                F64X2_ABS -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAbsInstruction(opcode)
                }

                F32X4_MIN,
                F64X2_MIN -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMinInstruction(opcode)
                }

                F32X4_MAX,
                F64X2_MAX -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMaxInstruction(opcode)
                }

                F32X4_ADD,
                F64X2_ADD -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdAddInstruction(opcode)
                }

                F32X4_SUB,
                F64X2_SUB -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSubtractInstruction(opcode)
                }

                F32X4_DIV,
                F64X2_DIV -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdDivideInstruction(opcode)
                }

                F32X4_MUL,
                F64X2_MUL -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdMultiplyInstruction(opcode)
                }

                F32X4_SQRT,
                F64X2_SQRT -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdSqrtInstruction(opcode)
                }

                F32X4_CONVERT_S_I32X4,
                F32X4_CONVERT_U_I32X4,
                F64X2_CONVERT_S_I64X2,
                F64X2_CONVERT_U_I64X2 -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdConvertInstruction(opcode)
                }

                I32X4_TRUNC_S_F32X4_SAT,
                I32X4_TRUNC_U_F32X4_SAT,
                I64X2_TRUNC_S_F64X2_SAT,
                I64X2_TRUNC_U_F64X2_SAT -> {
                    if (!opcode.isEnabled(context.options.features)) {
                        throw ParserException("Invalid SIMD code: SIMD support not enabled.")
                    }

                    functionBodyVisitor.visitSimdTruncateInstruction(opcode)
                }

                MEMORY_FILL -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid memory.fill code: bulk memory not enabled.")
                    }

                    val address = source.readVarUInt32()
                    val value = source.readVarUInt32()
                    val size = source.readVarUInt32()

                    functionBodyVisitor.visitMemoryFillInstruction(opcode, address, value, size)
                }

                MEMORY_COPY -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid memory.copy code: bulk memory not enabled.")
                    }

                    val target = source.readVarUInt32()
                    val address = source.readVarUInt32()
                    val size = source.readVarUInt32()

                    functionBodyVisitor.visitMemoryFillInstruction(opcode, target, address, size)
                }

                MEMORY_INIT -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid memory.copy code: bulk memory not enabled.")
                    }

                    val target = source.readVarUInt32()
                    val address = source.readVarUInt32()
                    val size = source.readVarUInt32()

                    val zero = source.readVarUInt32()

                    functionBodyVisitor.visitMemoryInitInstruction(opcode, target, address, size)
                }

                DATA_DROP -> {
                    if (!context.options.features.isBulkMemoryEnabled) {
                        throw ParserException("Invalid data.drop code: bulk memory not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
                }

                CALL_REF -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid call_ref code: reference types not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
                }

                RETURN_CALL_REF -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid return_call_ref code: reference types not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
                }

                REF_AS_NON_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid ref_as_non_null code: reference types not enabled.")
                    }

                    // TODO
                }

                BR_ON_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid br_on_null code: reference types not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
                }

                BR_ON_NON_NULL -> {
                    if (!context.options.features.isReferenceTypesEnabled) {
                        throw ParserException("Invalid br_on_non_null code: reference types not enabled.")
                    }

                    // TODO
                    source.readVarUInt32()
                }

                else -> throw ParserException("Unexpected opcode: %$opcode(0x${opcode.opcode.toHexString()})")
            }
        }

        if (numberOfInstructions > WasmBinary.MAX_FUNCTION_INSTRUCTIONS) {
            throw ParserException("Number of function locals $numberOfInstructions exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS}")
        }

        if (!seenEndOpcode) {
            throw ParserException("Function Body must end with END opcode")
        }
    }
}
