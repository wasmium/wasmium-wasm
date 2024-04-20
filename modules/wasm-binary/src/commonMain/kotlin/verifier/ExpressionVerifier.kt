package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.Opcode.*
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ExpressionVisitor

public open class ExpressionVerifier(private val delegate: ExpressionVisitor? = null, private val context: VerifierContext) : ExpressionVisitor {
    private var done: Boolean = false
    private var numberOfInstructions: UInt = 0u

    override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid $opcode code: threads not enabled.")
        }

        when (opcode) {
            I32_ATOMIC_LOAD,
            I64_ATOMIC_LOAD,
            I32_ATOMIC_LOAD8_U,
            I32_ATOMIC_LOAD16_U,
            I64_ATOMIC_LOAD8_U,
            I64_ATOMIC_LOAD16_U,
            I64_ATOMIC_LOAD32_U -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic load opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAtomicLoadInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid wake code: threads not enabled.")
        }

        when (opcode) {
            I32_ATOMIC_STORE,
            I64_ATOMIC_STORE,
            I32_ATOMIC_STORE8,
            I32_ATOMIC_STORE16,
            I64_ATOMIC_STORE8,
            I64_ATOMIC_STORE16,
            I64_ATOMIC_STORE32 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic store opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAtomicStoreInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid wake code: threads not enabled.")
        }

        when (opcode) {
            I32_ATOMIC_RMW_CMPXCHG,
            I64_ATOMIC_RMW_CMPXCHG,
            I32_ATOMIC_RMW8_U_CMPXCHG,
            I32_ATOMIC_RMW16_U_CMPXCHG,
            I64_ATOMIC_RMW8_U_CMPXCHG,
            I64_ATOMIC_RMW16_U_CMPXCHG,
            I64_ATOMIC_RMW32_U_CMPXCHG -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic rmw compare exchange opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid $opcode code: threads not enabled.")
        }

        when (opcode) {
            MEMORY_ATOMIC_WAIT32,
            MEMORY_ATOMIC_WAIT64 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic wait opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAtomicWaitInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid $opcode code: threads not enabled.")
        }

        when (opcode) {
            MEMORY_ATOMIC_NOTIFY -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic wake instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAtomicWakeInstruction(opcode, alignment, offset)
    }

    override fun visitBrTableInstruction(targets: List<UInt>, defaultTarget: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitBrTableInstruction(targets, defaultTarget)
    }

    override fun visitCompareInstruction(opcode: Opcode) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitCompareInstruction(opcode)
    }

    override fun visitConvertInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_CONVERT_SI32,
            F32_CONVERT_UI32,
            F32_CONVERT_SI64,
            F32_CONVERT_UI64,
            F64_CONVERT_SI32,
            F64_CONVERT_UI32,
            F64_CONVERT_SI64,
            F64_CONVERT_UI64 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid convert instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitConvertInstruction(opcode)
    }

    override fun visitEndInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate?.visitEndInstruction()
    }

    override fun visitConstFloat32Instruction(value: Float) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitConstFloat32Instruction(value)
    }

    override fun visitConstFloat64Instruction(value: Double) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitConstFloat64Instruction(value)
    }

    override fun visitConstInt32Instruction(value: Int) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitConstInt32Instruction(value)
    }

    override fun visitConstInt64Instruction(value: Long) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitConstInt64Instruction(value)
    }

    override fun visitSimdConstInstruction(value: V128Value) {
        checkEnd()

        if (!context.options.features.isSIMDEnabled) {
            throw ParserException("Invalid V128 value: SIMD support not enabled.")
        }

        numberOfInstructions++

        delegate?.visitSimdConstInstruction(value)
    }

    override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            V8X16_SHUFFLE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD shuffle instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdShuffleInstruction(opcode, value)
    }

    override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        when (opcode) {
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
                // valid
            }

            else -> {
                throw VerifierException("Invalid load instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitLoadInstruction(opcode, alignment, offset)
    }

    override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid V128Value code: SIMD support not enabled.")
        }

        when (opcode) {
            V128_LOAD -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD shuffle instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdLoadInstruction(opcode, alignment, offset)
    }

    override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        when (opcode) {
            I32_STORE8,
            I32_STORE16,
            I64_STORE8,
            I64_STORE16,
            I64_STORE32,
            I32_STORE,
            I64_STORE,
            F32_STORE,
            F64_STORE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid store instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitStoreInstruction(opcode, alignment, offset)
    }

    override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid V128Value code: SIMD support not enabled.")
        }

        when (opcode) {
            V128_STORE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD store instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdStoreInstruction(opcode, alignment, offset)
    }

    override fun visitWrapInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_WRAP_I64 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid wrap instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitWrapInstruction(opcode)
    }

    override fun visitExtendInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code not enabled.")
        }

        when (opcode) {
            I32_EXTEND8_S,
            I32_EXTEND16_S,
            I64_EXTEND8_S,
            I64_EXTEND16_S,
            I64_EXTEND32_S,
            I64_EXTEND_SI32,
            I64_EXTEND_UI32 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid extend instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitExtendInstruction(opcode)
    }

    override fun visitDemoteInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_DEMOTE_F64 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid demote instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitDemoteInstruction(opcode)
    }

    override fun visitPromoteInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F64_PROMOTE_F32 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid promote instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitPromoteInstruction(opcode)
    }

    override fun visitReinterpretInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_REINTERPRET_F32,
            I64_REINTERPRET_F64,
            F32_REINTERPRET_I32,
            F64_REINTERPRET_I64 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid reinterpret instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitReinterpretInstruction(opcode)
    }

    override fun visitUnreachableInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate?.visitUnreachableInstruction()
    }

    override fun visitNopInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate?.visitNopInstruction()
    }

    override fun visitIfInstruction(types: List<WasmType>) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitIfInstruction(types)
    }

    override fun visitLoopInstruction(types: List<WasmType>) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitLoopInstruction(types)
    }

    override fun visitBlockInstruction(types: List<WasmType>) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitBlockInstruction(types)
    }

    override fun visitElseInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate?.visitElseInstruction()
    }

    override fun visitTryInstruction(types: List<WasmType>) {
        checkEnd()

        if (!context.options.features.isExceptionHandlingEnabled) {
            throw ParserException("Invalid try code: exceptions not enabled.")
        }

        numberOfInstructions++

        delegate?.visitTryInstruction(types)
    }

    override fun visitCatchInstruction() {
        checkEnd()

        if (!context.options.features.isExceptionHandlingEnabled) {
            throw ParserException("Invalid catch code: exceptions not enabled.")
        }

        numberOfInstructions++

        delegate?.visitCatchInstruction()
    }

    override fun visitThrowRefInstruction() {
        checkEnd()

        if (!context.options.features.isExceptionHandlingEnabled) {
            throw ParserException("Invalid catch code: exceptions not enabled.")
        }

        numberOfInstructions++

        delegate?.visitThrowRefInstruction()
    }

    override fun visitThrowInstruction(exceptionIndex: UInt) {
        checkEnd()

        if (!context.options.features.isExceptionHandlingEnabled) {
            throw ParserException("Invalid throw code: exceptions not enabled.")
        }

        if (exceptionIndex >= context.numberOfTotalExceptions) {
            throw ParserException("invalid call exception index: %$exceptionIndex")
        }

        numberOfInstructions++

        delegate?.visitThrowInstruction(exceptionIndex)
    }

    override fun visitRethrowInstruction() {
        checkEnd()

        if (!context.options.features.isExceptionHandlingEnabled) {
            throw ParserException("Invalid rethrow code: exceptions not enabled.")
        }

        numberOfInstructions++

        delegate?.visitRethrowInstruction()
    }

    override fun visitBrInstruction(depth: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitBrInstruction(depth)
    }

    override fun visitBrIfInstruction(depth: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitBrIfInstruction(depth)
    }

    override fun visitReturnInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate?.visitReturnInstruction()
    }

    override fun visitCallInstruction(functionIndex: UInt) {
        checkEnd()

        if (functionIndex >= context.numberOfTotalFunctions) {
            throw ParserException("Invalid call function index: %$functionIndex")
        }

        numberOfInstructions++

        delegate?.visitCallInstruction(functionIndex)
    }

    override fun visitCallIndirectInstruction(typeIndex: UInt, reserved: Boolean) {
        checkEnd()

        if (typeIndex >= context.numberOfTypes) {
            throw ParserException("Invalid call_indirect signature index")
        }

        if (reserved) {
            throw VerifierException("Invalid reserved value $reserved")
        }

        numberOfInstructions++

        delegate?.visitCallIndirectInstruction(typeIndex, reserved)
    }

    override fun visitDropInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate?.visitDropInstruction()
    }

    override fun visitSelectInstruction() {
        checkEnd()

        numberOfInstructions++

        delegate?.visitSelectInstruction()
    }

    override fun visitGetGlobalInstruction(globalIndex: UInt) {
        checkEnd()

        if (globalIndex >= context.numberOfTotalGlobals) {
            throw VerifierException("Invalid global index $globalIndex")
        }

        numberOfInstructions++

        delegate?.visitGetGlobalInstruction(globalIndex)
    }

    override fun visitSetLocalInstruction(localIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitSetLocalInstruction(localIndex)
    }

    override fun visitTeeLocalInstruction(localIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitTeeLocalInstruction(localIndex)
    }

    override fun visitGetLocalInstruction(localIndex: UInt) {
        checkEnd()

        numberOfInstructions++

        delegate?.visitGetLocalInstruction(localIndex)
    }

    override fun visitSetGlobalInstruction(globalIndex: UInt) {
        checkEnd()

        if (globalIndex >= context.numberOfTotalGlobals) {
            throw VerifierException("Invalid global index $globalIndex")
        }

        numberOfInstructions++

        delegate?.visitSetGlobalInstruction(globalIndex)
    }

    override fun visitMemorySizeInstruction(reserved: Boolean) {
        checkEnd()

        if (reserved) {
            throw VerifierException("Invalid reserved value $reserved")
        }

        numberOfInstructions++

        delegate?.visitMemorySizeInstruction(reserved)
    }

    override fun visitMemoryGrowInstruction(reserved: Boolean) {
        checkEnd()

        if (reserved) {
            throw VerifierException("Invalid reserved value $reserved")
        }

        numberOfInstructions++

        delegate?.visitMemoryGrowInstruction(reserved)
    }

    override fun visitEqualZeroInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_EQZ,
            I64_EQZ -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid equal zero instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitEqualZeroInstruction(opcode)
    }

    override fun visitEqualInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_EQ,
            I64_EQ,
            F32_EQ,
            F64_EQ -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid equal instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitEqualInstruction(opcode)
    }

    override fun visitNotEqualInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_NE,
            I64_NE,
            F32_NE,
            F64_NE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid not equal instruction opcode $opcode")
            }
        }
        numberOfInstructions++

        delegate?.visitNotEqualInstruction(opcode)
    }

    override fun visitLessThanInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F64_LT,
            I32_LT_S,
            I32_LT_U,
            F32_LT,
            I64_LT_S,
            I64_LT_U -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid less than instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitLessThanInstruction(opcode)
    }

    override fun visitLessEqualInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_LE_S,
            I32_LE_U,
            I64_LE_S,
            I64_LE_U,
            F32_LE,
            F64_LE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid less equal instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitLessEqualInstruction(opcode)
    }

    override fun visitGreaterThanInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_GT_S,
            I32_GT_U,
            I64_GT_S,
            I64_GT_U,
            F32_GT,
            F64_GT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid greater than instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitGreaterThanInstruction(opcode)
    }

    override fun visitGreaterEqualInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_GE_S,
            I32_GE_U,
            I64_GE_S,
            I64_GE_U,
            F32_GE,
            F64_GE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid greater equal instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitGreaterEqualInstruction(opcode)
    }

    override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_CLZ,
            I64_CLZ -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid count leading zeros instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitCountLeadingZerosInstruction(opcode)
    }

    override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_CTZ,
            I64_CTZ -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid count trailing zeros instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitCountTrailingZerosInstruction(opcode)
    }

    override fun visitPopulationCountInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_POPCNT,
            I64_POPCNT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid population count instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitPopulationCountInstruction(opcode)
    }

    override fun visitAddInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_ADD,
            I64_ADD,
            F32_ADD,
            F64_ADD -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid add instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAddInstruction(opcode)
    }

    override fun visitSubtractInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_SUB,
            I64_SUB,
            F32_SUB,
            F64_SUB -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid subtract instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSubtractInstruction(opcode)
    }

    override fun visitMultiplyInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_MUL,
            I64_MUL,
            F32_MUL,
            F64_MUL -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid multiply instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitMultiplyInstruction(opcode)
    }

    override fun visitDivideInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_DIV,
            I32_DIV_S,
            I32_DIV_U,
            F64_DIV,
            I64_DIV_S,
            I64_DIV_U -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid divide instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitDivideInstruction(opcode)
    }

    override fun visitRemainderInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_REM_S,
            I32_REM_U,
            I64_REM_S,
            I64_REM_U -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid remainder instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitRemainderInstruction(opcode)
    }

    override fun visitAndInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_AND,
            I64_AND -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid and instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAndInstruction(opcode)
    }

    override fun visitOrInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_OR,
            I64_OR -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid or instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitOrInstruction(opcode)
    }

    override fun visitSimdXorInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        numberOfInstructions++

        delegate?.visitSimdXorInstruction(opcode)
    }

    override fun visitShiftLeftInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_SHL,
            I64_SHL -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid shift left instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitShiftLeftInstruction(opcode)
    }

    override fun visitRotateLeftInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_ROTL,
            I64_ROTL -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid rotate left instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitRotateLeftInstruction(opcode)
    }

    override fun visitRotateRightInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_ROTR,
            I64_ROTR -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid rotate right instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitRotateRightInstruction(opcode)
    }

    override fun visitAbsoluteInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_ABS,
            F64_ABS -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid absolute instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAbsoluteInstruction(opcode)
    }

    override fun visitNegativeInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_NEG,
            F64_NEG -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid negative instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitNegativeInstruction(opcode)
    }

    override fun visitCeilingInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_CEIL,
            F64_CEIL -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid ceiling instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitCeilingInstruction(opcode)
    }

    override fun visitFloorInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_FLOOR,
            F64_FLOOR -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid floor instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitFloorInstruction(opcode)
    }

    override fun visitTruncateInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            F32_TRUNC,
            F64_TRUNC,
            I32_TRUNC_SF32,
            I32_TRUNC_UF32,
            I32_TRUNC_SF64,
            I32_TRUNC_UF64,
            I64_TRUNC_SF32,
            I64_TRUNC_UF32,
            I64_TRUNC_SF64,
            I64_TRUNC_UF64,
            I32_TRUNC_S_SAT_F32,
            I32_TRUNC_U_SAT_F32,
            I32_TRUNC_S_SAT_F64,
            I32_TRUNC_U_SAT_F64,
            I64_TRUNC_S_SAT_F32,
            I64_TRUNC_U_SAT_F32,
            I64_TRUNC_S_SAT_F64,
            I64_TRUNC_U_SAT_F64 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid truncate instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitTruncateInstruction(opcode)
    }

    override fun visitNearestInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_NEAREST,
            F64_NEAREST -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid nearest instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitNearestInstruction(opcode)
    }

    override fun visitSqrtInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_SQRT,
            F64_SQRT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid sqrt instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSqrtInstruction(opcode)
    }

    override fun visitMinInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_MIN,
            F64_MIN -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid min instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitMinInstruction(opcode)
    }

    override fun visitMaxInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_MAX,
            F64_MAX -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid max instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitMaxInstruction(opcode)
    }

    override fun visitAtomicRmwAddInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid $opcode code: threads not enabled.")
        }

        when (opcode) {
            I32_ATOMIC_RMW_ADD,
            I64_ATOMIC_RMW_ADD,
            I32_ATOMIC_RMW8_U_ADD,
            I32_ATOMIC_RMW16_U_ADD,
            I64_ATOMIC_RMW8_U_ADD,
            I64_ATOMIC_RMW16_U_ADD,
            I64_ATOMIC_RMW32_U_ADD -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic rmw add instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAtomicRmwAddInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid $opcode code: threads not enabled.")
        }

        when (opcode) {
            I32_ATOMIC_RMW_SUB,
            I64_ATOMIC_RMW_SUB,
            I32_ATOMIC_RMW8_U_SUB,
            I32_ATOMIC_RMW16_U_SUB,
            I64_ATOMIC_RMW8_U_SUB,
            I64_ATOMIC_RMW16_U_SUB,
            I64_ATOMIC_RMW32_U_SUB -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic rmw subtract instruction opcode $opcode")
            }
        }
        numberOfInstructions++

        delegate?.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwAndInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid $opcode code: threads not enabled.")
        }

        when (opcode) {
            I32_ATOMIC_RMW_AND,
            I64_ATOMIC_RMW_AND,
            I32_ATOMIC_RMW8_U_AND,
            I32_ATOMIC_RMW16_U_AND,
            I64_ATOMIC_RMW8_U_AND,
            I64_ATOMIC_RMW16_U_AND,
            I64_ATOMIC_RMW32_U_AND -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic rmw and instruction opcode $opcode")
            }
        }
        numberOfInstructions++

        delegate?.visitAtomicRmwAndInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwOrInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid $opcode code: threads not enabled.")
        }

        when (opcode) {
            I32_ATOMIC_RMW_OR,
            I64_ATOMIC_RMW_OR,
            I32_ATOMIC_RMW8_U_OR,
            I32_ATOMIC_RMW16_U_OR,
            I64_ATOMIC_RMW8_U_OR,
            I64_ATOMIC_RMW16_U_OR,
            I64_ATOMIC_RMW32_U_OR -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic rmw or instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAtomicRmwOrInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwXorInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid $opcode code: threads not enabled.")
        }

        when (opcode) {
            I32_ATOMIC_RMW_XOR,
            I64_ATOMIC_RMW_XOR,
            I32_ATOMIC_RMW8_U_XOR,
            I32_ATOMIC_RMW16_U_XOR,
            I64_ATOMIC_RMW8_U_XOR,
            I64_ATOMIC_RMW16_U_XOR,
            I64_ATOMIC_RMW32_U_XOR -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic rmw xor instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAtomicRmwXorInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid $opcode code: threads not enabled.")
        }

        when (opcode) {
            I32_ATOMIC_RMW_XCHG,
            I64_ATOMIC_RMW_XCHG,
            I32_ATOMIC_RMW8_U_XCHG,
            I32_ATOMIC_RMW16_U_XCHG,
            I64_ATOMIC_RMW8_U_XCHG,
            I64_ATOMIC_RMW16_U_XCHG,
            I64_ATOMIC_RMW32_U_XCHG -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid atomic rmw exchange instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)
    }

    override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_SPLAT,
            I16X8_SPLAT,
            I32X4_SPLAT,
            I64X2_SPLAT,
            F32X4_SPLAT,
            F64X2_SPLAT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD splat instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdSplatInstruction(opcode, value)
    }

    override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_EXTRACT_LANE_S,
            I8X16_EXTRACT_LANE_U,
            I16X8_EXTRACT_LANE_S,
            I16X8_EXTRACT_LANE_U,
            I32X4_EXTRACT_LANE,
            I64X2_EXTRACT_LANE,
            F32X4_EXTRACT_LANE,
            F64X2_EXTRACT_LANE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD extract lane instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdExtractLaneInstruction(opcode, index)
    }

    override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_REPLACE_LANE,
            I16X8_REPLACE_LANE,
            I32X4_REPLACE_LANE,
            I64X2_REPLACE_LANE,
            F32X4_REPLACE_LANE,
            F64X2_REPLACE_LANE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD replace lane instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdReplaceLaneInstruction(opcode, index)
    }

    override fun visitSimdAddInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_ADD,
            I16X8_ADD,
            I32X4_ADD,
            I64X2_ADD,
            F32X4_ADD,
            F64X2_ADD -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD add instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdAddInstruction(opcode)
    }

    override fun visitSimdSubtractInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_SUB,
            I16X8_SUB,
            I32X4_SUB,
            I64X2_SUB,
            F32X4_SUB,
            F64X2_SUB -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD subtract instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdSubtractInstruction(opcode)
    }

    override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_MUL,
            I16X8_MUL,
            I32X4_MUL,
            F32X4_MUL,
            F64X2_MUL -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD multiply instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdMultiplyInstruction(opcode)
    }

    override fun visitSimdNegativeInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_NEG,
            I16X8_NEG,
            I32X4_NEG,
            I64X2_NEG,
            F32X4_NEG,
            F64X2_NEG -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD negative instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdNegativeInstruction(opcode)
    }

    override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_ADD_SATURATE_S,
            I8X16_ADD_SATURATE_U,
            I16X8_ADD_SATURATE_S,
            I16X8_ADD_SATURATE_U -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD add saturate instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdAddSaturateInstruction(opcode)
    }

    override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_SUB_SATURATE_S,
            I8X16_SUB_SATURATE_U,
            I16X8_SUB_SATURATE_S,
            I16X8_SUB_SATURATE_U -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD subtract saturate instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdSubtractSaturateInstruction(opcode)
    }

    override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
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
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD shift left instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdShiftLeftInstruction(opcode)
    }

    override fun visitSimdAndInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            V128_AND -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD and instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdAndInstruction(opcode)
    }

    override fun visitSimdOrInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            V128_OR -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD or instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdOrInstruction(opcode)
    }

    override fun visitSimdNotInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            V128_NOT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD not instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdNotInstruction(opcode)
    }

    override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            V128_BITSELECT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD bit select instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdBitSelectInstruction(opcode)
    }

    override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_ANY_TRUE,
            I16X8_ANY_TRUE,
            I32X4_ANY_TRUE,
            I64X2_ANY_TRUE,
            I8X16_ALL_TRUE,
            I16X8_ALL_TRUE,
            I32X4_ALL_TRUE,
            I64X2_ALL_TRUE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD all true instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdAllTrueInstruction(opcode)
    }

    override fun visitSimdEqualInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_EQ,
            I16X8_EQ,
            I32X4_EQ,
            F32X4_EQ,
            F64X2_EQ -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD equal instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdEqualInstruction(opcode)
    }

    override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_NE,
            I16X8_NE,
            I32X4_NE,
            F32X4_NE,
            F64X2_NE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD not equal instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdNotEqualInstruction(opcode)
    }

    override fun visitSimdLessThanInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_LT_S,
            I8X16_LT_U,
            I16X8_LT_S,
            I16X8_LT_U,
            I32X4_LT_S,
            I32X4_LT_U,
            F32X4_LT,
            F64X2_LT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD less than instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdLessThanInstruction(opcode)
    }

    override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_LE_S,
            I8X16_LE_U,
            I16X8_LE_S,
            I16X8_LE_U,
            I32X4_LE_S,
            I32X4_LE_U,
            F32X4_LE,
            F64X2_LE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD less equal instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdLessEqualInstruction(opcode)
    }

    override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_GT_S,
            I8X16_GT_U,
            I16X8_GT_S,
            I16X8_GT_U,
            I32X4_GT_S,
            I32X4_GT_U,
            F32X4_GT,
            F64X2_GT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD greater than instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdGreaterThanInstruction(opcode)
    }

    override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I8X16_GE_S,
            I8X16_GE_U,
            I16X8_GE_S,
            I16X8_GE_U,
            I32X4_GE_S,
            I32X4_GE_U,
            F32X4_GE,
            F64X2_GE -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD greater equal instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdGreaterEqualInstruction(opcode)
    }

    override fun visitSimdMinInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            F32X4_MIN,
            F64X2_MIN -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD min instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdMinInstruction(opcode)
    }

    override fun visitSimdMaxInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            F32X4_MAX,
            F64X2_MAX -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD max instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdMaxInstruction(opcode)
    }

    override fun visitSimdDivideInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            F32X4_DIV,
            F64X2_DIV -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD divide instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdDivideInstruction(opcode)
    }

    override fun visitSimdSqrtInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            F32X4_SQRT,
            F64X2_SQRT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD sqrt instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdSqrtInstruction(opcode)
    }

    override fun visitSimdConvertInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            F32X4_CONVERT_S_I32X4,
            F32X4_CONVERT_U_I32X4,
            F64X2_CONVERT_S_I64X2,
            F64X2_CONVERT_U_I64X2 -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD convert instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdConvertInstruction(opcode)
    }

    override fun visitSimdTruncateInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            I32X4_TRUNC_S_F32X4_SAT,
            I32X4_TRUNC_U_F32X4_SAT,
            I64X2_TRUNC_S_F64X2_SAT,
            I64X2_TRUNC_U_F64X2_SAT -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD truncate instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdTruncateInstruction(opcode)
    }

    override fun visitCopySignInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            F32_COPYSIGN,
            F64_COPYSIGN -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid copy sign instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitCopySignInstruction(opcode)
    }

    override fun visitXorInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_XOR,
            I64_XOR -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid xor instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitXorInstruction(opcode)
    }

    override fun visitSimdAbsInstruction(opcode: Opcode) {
        checkEnd()

        if (!opcode.isEnabled(context.options.features)) {
            throw ParserException("Invalid $opcode code: SIMD support not enabled.")
        }

        when (opcode) {
            F32X4_ABS,
            F64X2_ABS -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid SIMD abs instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitSimdAbsInstruction(opcode)
    }

    override fun visitMemoryFillInstruction(memoryIndex: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid memory.fill code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitMemoryFillInstruction(memoryIndex)
    }

    override fun visitMemoryCopyInstruction(targetIndex: UInt, sourceIndex: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid memory.copy code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitMemoryCopyInstruction(targetIndex, sourceIndex)
    }

    override fun visitMemoryInitInstruction(memoryIndex: UInt, segmentIndex: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid memory.init code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitMemoryInitInstruction(memoryIndex, segmentIndex)
    }

    override fun visitTableInitInstruction(segmentIndex: UInt, tableIndex: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid table.init code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitTableInitInstruction(segmentIndex, tableIndex)
    }

    override fun visitDataDropInstruction(segmentIndex: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid data.drop code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitDataDropInstruction(segmentIndex)
    }

    override fun visitTableSizeInstruction(tableIndex: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid table.size code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitTableSizeInstruction(tableIndex)
    }

    override fun visitTableGrowInstruction(tableIndex: UInt, value: UInt, delta: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid table.grow code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitTableGrowInstruction(tableIndex, value, delta)
    }

    override fun visitTableFillInstruction(tableIndex: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid table.fill code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitTableFillInstruction(tableIndex)
    }

    override fun visitTableCopyInstruction(targetTableIndex: UInt, sourceTableIndex: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid table.copy code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitTableCopyInstruction(targetTableIndex, sourceTableIndex)
    }

    override fun visitElementDropInstruction(segmentIndex: UInt) {
        checkEnd()

        if (!context.options.features.isBulkMemoryEnabled) {
            throw ParserException("Invalid elem.drop code: bulk memory not enabled.")
        }

        numberOfInstructions++

        delegate?.visitElementDropInstruction(segmentIndex)
    }

    override fun visitAtomicFenceInstruction(reserved: Boolean) {
        checkEnd()

        if (!context.options.features.isThreadsEnabled) {
            throw ParserException("Invalid atomic.fence code: threads not enabled.")
        }

        if (reserved) {
            throw VerifierException("Invalid reserved value $reserved")
        }

        numberOfInstructions++

        delegate?.visitAtomicFenceInstruction(reserved)
    }

    override fun visitReferenceEqualInstruction() {
        checkEnd()

        if (!context.options.features.isReferenceTypesEnabled) {
            throw ParserException("Invalid ref_eq code: reference types not enabled.")
        }


        numberOfInstructions++

        delegate?.visitReferenceEqualInstruction()
    }

    override fun visitReferenceFunctionInstruction(functionIndex: UInt) {
        checkEnd()

        if (!context.options.features.isReferenceTypesEnabled) {
            throw ParserException("Invalid ref_func code: reference types not enabled.")
        }

        numberOfInstructions++

        delegate?.visitReferenceFunctionInstruction(functionIndex)
    }

    override fun visitReferenceIsNullInstruction() {
        checkEnd()

        if (!context.options.features.isReferenceTypesEnabled) {
            throw ParserException("Invalid ref_is_null code: reference types not enabled.")
        }

        numberOfInstructions++

        delegate?.visitReferenceIsNullInstruction()
    }

    override fun visitReferenceNullInstruction(type: WasmType) {
        checkEnd()

        if (!context.options.features.isReferenceTypesEnabled) {
            throw ParserException("Invalid ref_null code: reference types not enabled.")
        }

        numberOfInstructions++

        delegate?.visitReferenceNullInstruction(type)
    }

    override fun visitShiftRightInstruction(opcode: Opcode) {
        checkEnd()

        when (opcode) {
            I32_SHR_S,
            I32_SHR_U,
            I64_SHR_S,
            I64_SHR_U -> {
                // valid
            }

            else -> {
                throw VerifierException("Invalid shift right instruction opcode $opcode")
            }
        }

        numberOfInstructions++

        delegate?.visitShiftRightInstruction(opcode)
    }

    override fun visitEnd() {
        checkEnd()

        if (this.numberOfInstructions > WasmBinary.MAX_FUNCTION_INSTRUCTIONS) {
            throw VerifierException("Number of function locals $numberOfInstructions exceed the maximum of ${WasmBinary.MAX_FUNCTION_LOCALS}")
        }

        done = true
        delegate?.visitEnd()
    }


    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called")
        }
    }
}
