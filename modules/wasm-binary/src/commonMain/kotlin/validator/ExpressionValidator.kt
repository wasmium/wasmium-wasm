package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.tree.BlockType
import org.wasmium.wasm.binary.tree.BlockType.BlockTypeKind.FUNCTION_TYPE
import org.wasmium.wasm.binary.tree.BlockType.BlockTypeKind.VALUE_TYPE
import org.wasmium.wasm.binary.tree.Opcode
import org.wasmium.wasm.binary.tree.Opcode.*
import org.wasmium.wasm.binary.tree.V128Value
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.tree.WasmType.EXTERN_REF
import org.wasmium.wasm.binary.tree.WasmType.F32
import org.wasmium.wasm.binary.tree.WasmType.F64
import org.wasmium.wasm.binary.tree.WasmType.FUNC_REF
import org.wasmium.wasm.binary.tree.WasmType.I32
import org.wasmium.wasm.binary.tree.WasmType.I64
import org.wasmium.wasm.binary.tree.WasmType.NONE
import org.wasmium.wasm.binary.tree.WasmType.V128
import org.wasmium.wasm.binary.tree.instructions.TryCatchArgument
import org.wasmium.wasm.binary.tree.FunctionType
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

private class ControlFrame(
    var opcode: Opcode,
    var startTypes: List<WasmType> = mutableListOf(),
    var endTypes: List<WasmType> = mutableListOf(),
    var height: Int,
    var unreachable: Boolean = false,
)

public class ExpressionValidator(
    private val delegate: ExpressionVisitor? = null,
    private val context: LocalContext,
    private val resultTypes: List<WasmType>,
) : ExpressionVisitor {

    private val valueStack = mutableListOf<WasmType>()
    private val controlStack = mutableListOf<ControlFrame>()

    init {
        pushControl(END, emptyList(), resultTypes)
    }

    private fun pushValue(type: WasmType) {
        valueStack.add(type)
    }

    private fun popValue(): WasmType {
        val frame = getControlReference(0u)

        if (valueStack.size == frame.height) {
            if (frame.unreachable) {
                return NONE
            } else {
                throw ValidatorException("Expression stack underflow")
            }
        }

        return valueStack.removeLast()
    }

    private fun popValue(type: WasmType): WasmType {
        val value = popValue()
        if (value != NONE && value != type) {
            throw ValidatorException("Expected value of type $type, found $value")
        }

        return value
    }

    private fun popValues(types: List<WasmType>): List<WasmType> {
        val popped = mutableListOf<WasmType>()

        for (i in types.size - 1 downTo 0) {
            popped.add(popValue(types[i]))
        }

        return popped.reversed()
    }

    private fun pushValues(types: List<WasmType>) {
        types.forEach { pushValue(it) }
    }

    private fun getControlReference(depth: UInt): ControlFrame {
        if (depth >= controlStack.size.toUInt()) {
            throw ValidatorException("Invalid control frame depth: $depth")
        }

        return controlStack[controlStack.size - depth.toInt() - 1]
    }

    private fun pushControl(opcode: Opcode, startTypes: List<WasmType>, endTypes: List<WasmType>) {
        controlStack.add(
            ControlFrame(
                opcode = opcode,
                startTypes = startTypes,
                endTypes = endTypes,
                height = valueStack.size,
                unreachable = false,
            )
        )
        pushValues(startTypes)
    }

    private fun popControl(): ControlFrame {
        if (controlStack.isEmpty()) {
            throw ValidatorException("Attempted to pop empty control stack")
        }

        val frame = getControlReference(0u)
        popValues(frame.endTypes)

        if (valueStack.size != frame.height) {
            throw ValidatorException("Stack height does not match frame height ${valueStack.size} != ${frame.height}")
        }

        controlStack.removeLast()

        return frame
    }

    private fun labelTypes(frame: ControlFrame): List<WasmType> {
        return if (frame.opcode == LOOP) frame.startTypes else frame.endTypes
    }

    private fun unreachable() {
        val frame = getControlReference(0u)
        while (valueStack.size > frame.height) {
            valueStack.removeLast()
        }

        frame.unreachable = true
    }

    override fun visitEnd() {
        if (controlStack.isNotEmpty()) {
            throw ValidatorException("Not all control frames are empty")
        }

        delegate?.visitEnd()
    }

    override fun visitEndInstruction() {
        val frame = popControl()
        pushValues(frame.endTypes)

        delegate?.visitEndInstruction()
    }

    override fun visitReturnInstruction() {
//        if (context.returns.isEmpty()) {
//            throw ValidatorException("Expression does not return a value")
//        }

        popValues(context.returns)
        unreachable()

        delegate?.visitReturnInstruction()
    }

    override fun visitAtomicLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        when (opcode) {
            I32_ATOMIC_LOAD,
            I32_ATOMIC_LOAD8_U,
            I32_ATOMIC_LOAD16_U -> {
                popValue(I32)
                pushValue(I32)
            }

            I64_ATOMIC_LOAD,
            I64_ATOMIC_LOAD8_U,
            I64_ATOMIC_LOAD16_U,
            I64_ATOMIC_LOAD32_U -> {
                popValue(I32)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for atomic load instruction: $opcode")
        }

        delegate?.visitAtomicLoadInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        when (opcode) {
            I32_ATOMIC_STORE,
            I32_ATOMIC_STORE8,
            I32_ATOMIC_STORE16 -> {
                popValue(I32)
                popValue(I32)
            }

            I64_ATOMIC_STORE,
            I64_ATOMIC_STORE8,
            I64_ATOMIC_STORE16,
            I64_ATOMIC_STORE32 -> {
                popValue(I64)
                popValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for atomic store instruction: $opcode")
        }

        delegate?.visitAtomicStoreInstruction(opcode, alignment, offset)
    }

    override fun visitAtomicRmwCompareExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwCompareExchangeInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitAtomicWaitInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicWaitInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitAtomicWakeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicWakeInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitBrTableInstruction(targets: List<UInt>, defaultTarget: UInt) {
        popValue(I32)

        val defaultLabelTypes = labelTypes(getControlReference(defaultTarget))

        for (target in targets) {
            val labelTypes = labelTypes(getControlReference(target))
            if (labelTypes.size != defaultLabelTypes.size) {
                throw ValidatorException("Mismatched label types in br_table")
            }

            pushValues(popValues(labelTypes))
        }

        popValues(defaultLabelTypes)
        unreachable()

        delegate?.visitBrTableInstruction(targets, defaultTarget)
    }

    override fun visitConvertInstruction(opcode: Opcode) {
        when (opcode) {
            F32_CONVERT_S_I32,
            F32_CONVERT_U_I32 -> {
                popValue(I32)
                pushValue(F32)
            }

            F32_CONVERT_S_I64,
            F32_CONVERT_U_I64 -> {
                popValue(I64)
                pushValue(F32)
            }

            F64_CONVERT_S_I32,
            F64_CONVERT_U_I32 -> {
                popValue(I32)
                pushValue(F64)
            }

            F64_CONVERT_S_I64,
            F64_CONVERT_U_I64 -> {
                popValue(I64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for convert instruction: $opcode")
        }

        delegate?.visitConvertInstruction(opcode)
    }

    override fun visitConstFloat32Instruction(value: Float) {
        pushValue(F32)

        delegate?.visitConstFloat32Instruction(value)
    }

    override fun visitConstFloat64Instruction(value: Double) {
        pushValue(F64)

        delegate?.visitConstFloat64Instruction(value)
    }

    override fun visitConstInt32Instruction(value: Int) {
        pushValue(I32)

        delegate?.visitConstInt32Instruction(value)
    }

    override fun visitConstInt64Instruction(value: Long) {
        pushValue(I64)

        delegate?.visitConstInt64Instruction(value)
    }

    override fun visitSimdConstInstruction(value: V128Value) {
        pushValue(V128)

        delegate?.visitSimdConstInstruction(value)
    }

    override fun visitSimdShuffleInstruction(opcode: Opcode, value: V128Value) {
        delegate?.visitSimdShuffleInstruction(opcode, value)

        TODO()
    }

    override fun visitLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        when (opcode) {
            I32_LOAD,
            I32_LOAD8_S,
            I32_LOAD8_U,
            I32_LOAD16_S,
            I32_LOAD16_U -> {
                popValue(I32)
                pushValue(I32)
            }

            I64_LOAD,
            I64_LOAD8_S,
            I64_LOAD8_U,
            I64_LOAD16_S,
            I64_LOAD16_U,
            I64_LOAD32_S,
            I64_LOAD32_U -> {
                popValue(I32)
                pushValue(I64)
            }

            F32_LOAD -> {
                popValue(I32)
                pushValue(F32)
            }

            F64_LOAD -> {
                popValue(I32)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for load instruction: $opcode")
        }

        delegate?.visitLoadInstruction(opcode, alignment, offset)
    }

    override fun visitSimdLoadInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitSimdLoadInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        when (opcode) {
            I32_STORE,
            I32_STORE8,
            I32_STORE16 -> {
                popValue(I32)
                popValue(I32)
            }

            I64_STORE,
            I64_STORE8,
            I64_STORE16,
            I64_STORE32 -> {
                popValue(I64)
                popValue(I32)
            }

            F32_STORE -> {
                popValue(F32)
                popValue(I32)
            }

            F64_STORE -> {
                popValue(F64)
                popValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for store instruction: $opcode")
        }

        delegate?.visitStoreInstruction(opcode, alignment, offset)
    }

    override fun visitSimdStoreInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitSimdStoreInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitWrapInstruction(opcode: Opcode) {
        when (opcode) {
            I32_WRAP_I64 -> {
                popValue(I64)
                pushValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for wrap instruction: $opcode")
        }

        delegate?.visitWrapInstruction(opcode)
    }

    override fun visitExtendInstruction(opcode: Opcode) {
        when (opcode) {
            I32_EXTEND8_S,
            I32_EXTEND16_S -> {
                popValue(I32)
                pushValue(I32)
            }

            I64_EXTEND8_S,
            I64_EXTEND16_S,
            I64_EXTEND32_S -> {
                popValue(I64)
                pushValue(I64)
            }

            I64_EXTEND_S_I32,
            I64_EXTEND_U_I32 -> {
                popValue(I32)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for extend instruction: $opcode")
        }

        delegate?.visitExtendInstruction(opcode)
    }

    override fun visitDemoteInstruction(opcode: Opcode) {
        when (opcode) {
            F32_DEMOTE_F64 -> {
                popValue(F64)
                pushValue(F32)
            }

            else -> throw ValidatorException("Invalid opcode for demote instruction: $opcode")
        }

        delegate?.visitDemoteInstruction(opcode)
    }

    override fun visitPromoteInstruction(opcode: Opcode) {
        when (opcode) {
            F64_PROMOTE_F32 -> {
                popValue(F32)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for promote instruction: $opcode")
        }

        delegate?.visitPromoteInstruction(opcode)
    }

    override fun visitReinterpretInstruction(opcode: Opcode) {
        when (opcode) {
            I32_REINTERPRET_F32 -> {
                popValue(F32)
                pushValue(I32)
            }

            F32_REINTERPRET_I32 -> {
                popValue(I32)
                pushValue(F32)
            }

            I64_REINTERPRET_F64 -> {
                popValue(F64)
                pushValue(I64)
            }

            F64_REINTERPRET_I64 -> {
                popValue(I64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for reinterpret instruction: $opcode")
        }
        delegate?.visitReinterpretInstruction(opcode)
    }

    override fun visitUnreachableInstruction() {
        unreachable()

        delegate?.visitUnreachableInstruction()
    }

    override fun visitNopInstruction() {
        delegate?.visitNopInstruction()
    }

    private fun getFunctionType(blockType: BlockType): FunctionType {
        val inputs = mutableListOf<WasmType>()
        val outputs = mutableListOf<WasmType>()

        when (blockType.kind) {
            VALUE_TYPE -> {
                when (val valueType = WasmType.fromWasmTypeId(blockType.value.toUInt())) {
                    NONE -> {
                        // do nothing
                    }

                    I32,
                    I64,
                    F32,
                    F64,
                    V128,
                    FUNC_REF,
                    EXTERN_REF -> {
                        outputs.add(valueType)
                    }

                    else -> throw ValidatorException("Invalid block type: $blockType")
                }
            }

            FUNCTION_TYPE -> {
                val functionType = context.types.getOrElse(blockType.value){
                    throw ValidatorException("Invalid type index: ${blockType.value}")
                }

                inputs.addAll(functionType.parameters)
                outputs.addAll(functionType.results)
            }
        }

        return FunctionType(NONE, inputs, outputs)
    }

    override fun visitIfInstruction(blockType: BlockType) {
        val functionType = getFunctionType(blockType)

        popValue(I32)
        popValues(functionType.parameters)
        pushControl(IF, functionType.parameters, functionType.results)

        delegate?.visitIfInstruction(blockType)
    }

    override fun visitLoopInstruction(blockType: BlockType) {
        val functionType = getFunctionType(blockType)

        popValues(functionType.parameters)
        pushControl(LOOP, functionType.parameters, functionType.results)

        delegate?.visitLoopInstruction(blockType)
    }

    override fun visitBlockInstruction(blockType: BlockType) {
        val functionType = getFunctionType(blockType)

        popValues(functionType.parameters)
        pushControl(BLOCK, functionType.parameters, functionType.results)

        delegate?.visitBlockInstruction(blockType)
    }

    override fun visitElseInstruction() {
        val frame = popControl()
        if (frame.opcode != IF) {
            throw ValidatorException("Else instruction not in if block: ${frame.opcode}")
        }

        pushControl(ELSE, frame.startTypes, frame.endTypes)

        delegate?.visitElseInstruction()
    }

    override fun visitTryInstruction(blockType: BlockType) {
        val functionType = getFunctionType(blockType)

        popValues(functionType.parameters)
        pushControl(TRY, functionType.parameters, functionType.results)

        delegate?.visitTryInstruction(blockType)
    }

    override fun visitTryTableInstruction(blockType: BlockType, handlers: List<TryCatchArgument>) {
        val functionType = getFunctionType(blockType)

        popValues(functionType.parameters)
        pushControl(TRY_TABLE, functionType.parameters, functionType.results)

        delegate?.visitTryTableInstruction(blockType, handlers)
    }

    override fun visitCatchInstruction() {
        delegate?.visitCatchInstruction()

        TODO()
    }

    override fun visitThrowRefInstruction() {
        delegate?.visitThrowRefInstruction()

        TODO()
    }

    override fun visitThrowInstruction(exceptionIndex: UInt) {
        delegate?.visitThrowInstruction(exceptionIndex)

        TODO()
    }

    override fun visitRethrowInstruction() {
        delegate?.visitRethrowInstruction()

        TODO()
    }

    override fun visitBrInstruction(depth: UInt) {
        val labels = labelTypes(getControlReference(depth))

        popValues(labels)
        unreachable()

        delegate?.visitBrInstruction(depth)
    }

    override fun visitBrIfInstruction(depth: UInt) {
        val labels = labelTypes(getControlReference(depth))

        popValue(I32)
        popValues(labels)
        pushValues(labels)

        delegate?.visitBrIfInstruction(depth)
    }

    override fun visitCallInstruction(functionIndex: UInt) {
        val functionType = context.functions.getOrElse(functionIndex.toInt()){
            throw ValidatorException("Invalid function index: $functionIndex")
        }

        popValues(functionType.parameters)
        pushValues(functionType.results)

        delegate?.visitCallInstruction(functionIndex)
    }

    override fun visitCallIndirectInstruction(typeIndex: UInt, reserved: UInt) {
        val tableType = context.tables.getOrElse(0){
            throw ValidatorException("Invalid table index: 0")
        }

        if (tableType.elementType != FUNC_REF) {
            throw ValidatorException("Invalid table element type: ${tableType.elementType}")
        }

        val functionType = context.types.getOrElse(typeIndex.toInt()){
            throw ValidatorException("Invalid type index: $typeIndex")
        }

        popValue(I32)
        popValues(functionType.parameters)
        pushValues(functionType.results)

        delegate?.visitCallIndirectInstruction(typeIndex, reserved)
    }

    override fun visitDropInstruction() {
        popValue()

        delegate?.visitDropInstruction()
    }

    override fun visitSelectInstruction() {
        popValue(I32)
        val type1 = popValue()
        val type2 = popValue()

        if (!type1.isNumberType() || !type2.isNumberType()) {
            throw ValidatorException("Expected select arguments to be numbers, found $type1 and $type2")
        }

        if (type1 != type2) {
            throw ValidatorException("Expected select arguments to be of the same type, found $type1 and $type2")
        }

        pushValue(type1)

        delegate?.visitSelectInstruction()
    }

    override fun visitSelectTypedInstruction(types: List<WasmType>) {
        popValue(I32)
        val type = popValue()
        popValue()

        pushValue(type)
    }

    override fun visitGetGlobalInstruction(globalIndex: UInt) {
        val globalType = context.globals.getOrElse(globalIndex.toInt()){
            throw ValidatorException("Invalid global index: $globalIndex")
        }

        pushValue(globalType.contentType)

        delegate?.visitGetGlobalInstruction(globalIndex)
    }

    override fun visitSetGlobalInstruction(globalIndex: UInt) {
        val globalType = context.globals.getOrElse(globalIndex.toInt()){
            throw ValidatorException("Invalid global index: $globalIndex")
        }
        popValue(globalType.contentType)

        delegate?.visitSetGlobalInstruction(globalIndex)
    }

    override fun visitSetLocalInstruction(localIndex: UInt) {
        val localType = context.locals.getOrElse(localIndex.toInt()){
            throw ValidatorException("Invalid local index: $localIndex")
        }
        popValue(localType)

        delegate?.visitSetLocalInstruction(localIndex)
    }

    override fun visitTeeLocalInstruction(localIndex: UInt) {
        val localType = context.locals.getOrElse(localIndex.toInt()) {
            throw ValidatorException("Invalid local index: $localIndex")
        }
        popValue(localType)
        pushValue(localType)

        delegate?.visitTeeLocalInstruction(localIndex)
    }

    override fun visitGetLocalInstruction(localIndex: UInt) {
        val localType = context.locals.getOrElse(localIndex.toInt()){
            throw ValidatorException("Invalid local index: $localIndex")
        }

        pushValue(localType)

        delegate?.visitGetLocalInstruction(localIndex)
    }

    override fun visitMemorySizeInstruction(reserved: UInt) {
        pushValue(I32)

        delegate?.visitMemorySizeInstruction(reserved)
    }

    override fun visitMemoryGrowInstruction(reserved: UInt) {
        popValue(I32)
        pushValue(I32)

        delegate?.visitMemoryGrowInstruction(reserved)
    }

    override fun visitEqualZeroInstruction(opcode: Opcode) {
        when (opcode) {
            I32_EQZ -> {
                popValue(I32)
                pushValue(I32)
            }

            I64_EQZ -> {
                popValue(I64)
                pushValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for equal zero instruction: $opcode")
        }

        delegate?.visitEqualZeroInstruction(opcode)
    }

    override fun visitEqualInstruction(opcode: Opcode) {
        when (opcode) {
            I32_EQ -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_EQ -> {
                popValue(I64)
                popValue(I64)
                pushValue(I32)
            }

            F32_EQ -> {
                popValue(F32)
                popValue(F32)
                pushValue(I32)
            }

            F64_EQ -> {
                popValue(F64)
                popValue(F64)
                pushValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for equals instruction: $opcode")
        }

        delegate?.visitEqualInstruction(opcode)
    }

    override fun visitNotEqualInstruction(opcode: Opcode) {
        when (opcode) {
            I32_NE -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_NE -> {
                popValue(I64)
                popValue(I64)
                pushValue(I32)
            }

            F32_NE -> {
                popValue(F32)
                popValue(F32)
                pushValue(I32)
            }

            F64_NE -> {
                popValue(F64)
                popValue(F64)
                pushValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for equals instruction: $opcode")
        }

        delegate?.visitNotEqualInstruction(opcode)
    }

    override fun visitLessThanInstruction(opcode: Opcode) {
        when (opcode) {
            I32_LT_S,
            I32_LT_U -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_LT_S,
            I64_LT_U -> {
                popValue(I64)
                popValue(I64)
                pushValue(I32)
            }

            F32_LT -> {
                popValue(F32)
                popValue(F32)
                pushValue(I32)
            }

            F64_LT -> {
                popValue(F64)
                popValue(F64)
                pushValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for less than instruction: $opcode")
        }

        delegate?.visitLessThanInstruction(opcode)
    }

    override fun visitLessEqualInstruction(opcode: Opcode) {
        when (opcode) {
            I32_LE_S,
            I32_LE_U -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_LE_S,
            I64_LE_U -> {
                popValue(I64)
                popValue(I64)
                pushValue(I32)
            }

            F32_LE -> {
                popValue(F32)
                popValue(F32)
                pushValue(I32)
            }

            F64_LE -> {
                popValue(F64)
                popValue(F64)
                pushValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for less equals instruction: $opcode")
        }

        delegate?.visitLessEqualInstruction(opcode)
    }

    override fun visitGreaterThanInstruction(opcode: Opcode) {
        when (opcode) {
            I32_GT_S,
            I32_GT_U -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_GT_S,
            I64_GT_U -> {
                popValue(I64)
                popValue(I64)
                pushValue(I32)
            }

            F32_GT -> {
                popValue(F32)
                popValue(F32)
                pushValue(I32)
            }

            F64_GT -> {
                popValue(F64)
                popValue(F64)
                pushValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for greater than instruction: $opcode")
        }

        delegate?.visitGreaterThanInstruction(opcode)
    }

    override fun visitGreaterEqualInstruction(opcode: Opcode) {
        when (opcode) {
            I32_GE_S,
            I32_GE_U -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_GE_S,
            I64_GE_U -> {
                popValue(I64)
                popValue(I64)
                pushValue(I32)
            }

            F32_GE -> {
                popValue(F32)
                popValue(F32)
                pushValue(I32)
            }

            F64_GE -> {
                popValue(F64)
                popValue(F64)
                pushValue(I32)
            }

            else -> throw ValidatorException("Invalid opcode for greater equals instruction: $opcode")
        }

        delegate?.visitGreaterEqualInstruction(opcode)
    }

    override fun visitCountLeadingZerosInstruction(opcode: Opcode) {
        when (opcode) {
            I32_CLZ -> {
                popValue(I32)
                pushValue(I32)
            }

            I64_CLZ -> {
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for count trailing zeros instruction: $opcode")
        }

        delegate?.visitCountLeadingZerosInstruction(opcode)
    }

    override fun visitCountTrailingZerosInstruction(opcode: Opcode) {
        when (opcode) {
            I32_CTZ -> {
                popValue(I32)
                pushValue(I32)
            }

            I64_CTZ -> {
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for count trailing zeros instruction: $opcode")
        }

        delegate?.visitCountTrailingZerosInstruction(opcode)
    }

    override fun visitPopulationCountInstruction(opcode: Opcode) {
        when (opcode) {
            I32_POPCNT -> {
                popValue(I32)
                pushValue(I32)
            }

            I64_POPCNT -> {
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for population count instruction: $opcode")
        }

        delegate?.visitPopulationCountInstruction(opcode)
    }

    override fun visitAddInstruction(opcode: Opcode) {
        when (opcode) {
            I32_ADD -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_ADD -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            F32_ADD -> {
                popValue(F32)
                popValue(F32)
                pushValue(F32)
            }

            F64_ADD -> {
                popValue(F64)
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for add instruction: $opcode")
        }

        delegate?.visitAddInstruction(opcode)
    }

    override fun visitSubtractInstruction(opcode: Opcode) {
        when (opcode) {
            I32_SUB -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_SUB -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            F32_SUB -> {
                popValue(F32)
                popValue(F32)
                pushValue(F32)
            }

            F64_SUB -> {
                popValue(F64)
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for subtract instruction: $opcode")
        }

        delegate?.visitSubtractInstruction(opcode)
    }

    override fun visitMultiplyInstruction(opcode: Opcode) {
        when (opcode) {
            I32_MUL -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_MUL -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            F32_MUL -> {
                popValue(F32)
                popValue(F32)
                pushValue(F32)
            }

            F64_MUL -> {
                popValue(F64)
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for multiply instruction: $opcode")
        }

        delegate?.visitMultiplyInstruction(opcode)
    }

    override fun visitDivideInstruction(opcode: Opcode) {
        when (opcode) {
            I32_DIV_S,
            I32_DIV_U -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_DIV_S,
            I64_DIV_U -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            F32_DIV -> {
                popValue(F32)
                popValue(F32)
                pushValue(F32)
            }

            F64_DIV -> {
                popValue(F64)
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for divide instruction: $opcode")
        }

        delegate?.visitDivideInstruction(opcode)
    }

    override fun visitRemainderInstruction(opcode: Opcode) {
        when (opcode) {
            I32_REM_S,
            I32_REM_U -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_REM_S,
            I64_REM_U -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for and instruction: $opcode")
        }

        delegate?.visitRemainderInstruction(opcode)
    }

    override fun visitAndInstruction(opcode: Opcode) {
        when (opcode) {
            I32_AND -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_AND -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for and instruction: $opcode")
        }

        delegate?.visitAndInstruction(opcode)
    }

    override fun visitOrInstruction(opcode: Opcode) {
        when (opcode) {
            I32_OR -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_OR -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for and instruction: $opcode")
        }

        delegate?.visitOrInstruction(opcode)
    }

    override fun visitSimdXorInstruction(opcode: Opcode) {
        delegate?.visitSimdXorInstruction(opcode)

        TODO()
    }

    override fun visitShiftLeftInstruction(opcode: Opcode) {
        when (opcode) {
            I32_SHL -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_SHL -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for shift left instruction: $opcode")
        }

        delegate?.visitShiftLeftInstruction(opcode)
    }

    override fun visitRotateLeftInstruction(opcode: Opcode) {
        when (opcode) {
            I32_ROTL -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_ROTL -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for rotate left instruction: $opcode")
        }

        delegate?.visitRotateLeftInstruction(opcode)
    }

    override fun visitRotateRightInstruction(opcode: Opcode) {
        when (opcode) {
            I32_ROTR -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_ROTR -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for rotate right instruction: $opcode")
        }

        delegate?.visitRotateRightInstruction(opcode)
    }

    override fun visitAbsoluteInstruction(opcode: Opcode) {
        when (opcode) {
            F32_ABS -> {
                popValue(F32)
                pushValue(F32)
            }

            F64_ABS -> {
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for absolute instruction: $opcode")
        }

        delegate?.visitAbsoluteInstruction(opcode)
    }

    override fun visitNegativeInstruction(opcode: Opcode) {
        when (opcode) {
            F32_NEG -> {
                popValue(F32)
                pushValue(F32)
            }

            F64_NEG -> {
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for negative instruction: $opcode")
        }

        delegate?.visitNegativeInstruction(opcode)
    }

    override fun visitCeilingInstruction(opcode: Opcode) {
        when (opcode) {
            F32_CEIL -> {
                popValue(F32)
                pushValue(F32)
            }

            F64_CEIL -> {
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for ceiling instruction: $opcode")
        }

        delegate?.visitCeilingInstruction(opcode)
    }

    override fun visitFloorInstruction(opcode: Opcode) {
        when (opcode) {
            F32_FLOOR -> {
                popValue(F32)
                pushValue(F32)
            }

            F64_FLOOR -> {
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for floor instruction: $opcode")
        }

        delegate?.visitFloorInstruction(opcode)
    }

    override fun visitTruncateInstruction(opcode: Opcode) {
        when (opcode) {
            F32_TRUNC -> {
                popValue(F32)
                pushValue(F32)
            }

            F64_TRUNC -> {
                popValue(F64)
                pushValue(F64)
            }

            I32_TRUNC_S_F32,
            I32_TRUNC_U_F32,
            I32_TRUNC_S_SAT_F32,
            I32_TRUNC_U_SAT_F32 -> {
                popValue(F32)
                pushValue(I32)
            }

            I32_TRUNC_S_F64,
            I32_TRUNC_U_F64,
            I32_TRUNC_S_SAT_F64,
            I32_TRUNC_U_SAT_F64 -> {
                popValue(F64)
                pushValue(I32)
            }

            I64_TRUNC_S_F32,
            I64_TRUNC_U_F32,
            I64_TRUNC_S_SAT_F32,
            I64_TRUNC_U_SAT_F32,
            -> {
                popValue(F32)
                pushValue(I64)
            }

            I64_TRUNC_S_F64,
            I64_TRUNC_U_F64,
            I64_TRUNC_S_SAT_F64,
            I64_TRUNC_U_SAT_F64 -> {
                popValue(F64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for truncate instruction: $opcode")
        }

        delegate?.visitTruncateInstruction(opcode)
    }

    override fun visitNearestInstruction(opcode: Opcode) {
        when (opcode) {
            F32_NEAREST -> {
                popValue(F32)
                pushValue(F32)
            }

            F64_NEAREST -> {
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for nearest instruction: $opcode")
        }

        delegate?.visitNearestInstruction(opcode)
    }

    override fun visitSqrtInstruction(opcode: Opcode) {
        when (opcode) {
            F32_SQRT -> {
                popValue(F32)
                pushValue(F32)
            }

            F64_SQRT -> {
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for sqrt instruction: $opcode")
        }

        delegate?.visitSqrtInstruction(opcode)
    }

    override fun visitMinInstruction(opcode: Opcode) {
        when (opcode) {
            F32_MIN -> {
                popValue(F32)
                popValue(F32)
                pushValue(F32)
            }

            F64_MIN -> {
                popValue(F64)
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for min instruction: $opcode")
        }

        delegate?.visitMinInstruction(opcode)
    }

    override fun visitMaxInstruction(opcode: Opcode) {
        when (opcode) {
            F32_MAX -> {
                popValue(F32)
                popValue(F32)
                pushValue(F32)
            }

            F64_MAX -> {
                popValue(F64)
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for max instruction: $opcode")
        }

        delegate?.visitMaxInstruction(opcode)
    }

    override fun visitAtomicRmwAddInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwAddInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitAtomicRmwSubtractInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwSubtractInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitAtomicRmwAndInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwAndInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitAtomicRmwOrInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwOrInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitAtomicRmwXorInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwXorInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitAtomicRmwExchangeInstruction(opcode: Opcode, alignment: UInt, offset: UInt) {
        delegate?.visitAtomicRmwExchangeInstruction(opcode, alignment, offset)

        TODO()
    }

    override fun visitSimdSplatInstruction(opcode: Opcode, value: UInt) {
        delegate?.visitSimdSplatInstruction(opcode, value)

        TODO()
    }

    override fun visitSimdExtractLaneInstruction(opcode: Opcode, index: UInt) {
        delegate?.visitSimdExtractLaneInstruction(opcode, index)

        TODO()
    }

    override fun visitSimdReplaceLaneInstruction(opcode: Opcode, index: UInt) {
        delegate?.visitSimdReplaceLaneInstruction(opcode, index)

        TODO()
    }

    override fun visitSimdAddInstruction(opcode: Opcode) {
        delegate?.visitSimdAddInstruction(opcode)

        TODO()
    }

    override fun visitSimdSubtractInstruction(opcode: Opcode) {
        delegate?.visitSimdSubtractInstruction(opcode)

        TODO()
    }

    override fun visitSimdMultiplyInstruction(opcode: Opcode) {
        delegate?.visitSimdMultiplyInstruction(opcode)

        TODO()
    }

    override fun visitSimdNegativeInstruction(opcode: Opcode) {
        delegate?.visitSimdNegativeInstruction(opcode)

        TODO()
    }

    override fun visitSimdAddSaturateInstruction(opcode: Opcode) {
        delegate?.visitSimdAddSaturateInstruction(opcode)

        TODO()
    }

    override fun visitSimdSubtractSaturateInstruction(opcode: Opcode) {
        delegate?.visitSimdSubtractSaturateInstruction(opcode)

        TODO()
    }

    override fun visitSimdShiftLeftInstruction(opcode: Opcode) {
        delegate?.visitSimdShiftLeftInstruction(opcode)

        TODO()
    }

    override fun visitSimdAndInstruction(opcode: Opcode) {
        delegate?.visitSimdAndInstruction(opcode)

        TODO()
    }

    override fun visitSimdOrInstruction(opcode: Opcode) {
        delegate?.visitSimdOrInstruction(opcode)

        TODO()
    }

    override fun visitSimdNotInstruction(opcode: Opcode) {
        delegate?.visitSimdNotInstruction(opcode)

        TODO()
    }

    override fun visitSimdBitSelectInstruction(opcode: Opcode) {
        delegate?.visitSimdBitSelectInstruction(opcode)

        TODO()
    }

    override fun visitSimdAllTrueInstruction(opcode: Opcode) {
        delegate?.visitSimdAllTrueInstruction(opcode)

        TODO()
    }

    override fun visitSimdEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdEqualInstruction(opcode)

        TODO()
    }

    override fun visitSimdNotEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdNotEqualInstruction(opcode)

        TODO()
    }

    override fun visitSimdLessThanInstruction(opcode: Opcode) {
        delegate?.visitSimdLessThanInstruction(opcode)

        TODO()
    }

    override fun visitSimdLessEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdLessEqualInstruction(opcode)

        TODO()
    }

    override fun visitSimdGreaterThanInstruction(opcode: Opcode) {
        delegate?.visitSimdGreaterThanInstruction(opcode)

        TODO()
    }

    override fun visitSimdGreaterEqualInstruction(opcode: Opcode) {
        delegate?.visitSimdGreaterEqualInstruction(opcode)

        TODO()
    }

    override fun visitSimdMinInstruction(opcode: Opcode) {
        delegate?.visitSimdMinInstruction(opcode)

        TODO()
    }

    override fun visitSimdMaxInstruction(opcode: Opcode) {
        delegate?.visitSimdMaxInstruction(opcode)

        TODO()
    }

    override fun visitSimdDivideInstruction(opcode: Opcode) {
        delegate?.visitSimdDivideInstruction(opcode)

        TODO()
    }

    override fun visitSimdSqrtInstruction(opcode: Opcode) {
        delegate?.visitSimdSqrtInstruction(opcode)

        TODO()
    }

    override fun visitSimdConvertInstruction(opcode: Opcode) {
        delegate?.visitSimdConvertInstruction(opcode)

        TODO()
    }

    override fun visitSimdTruncateInstruction(opcode: Opcode) {
        delegate?.visitSimdTruncateInstruction(opcode)

        TODO()
    }

    override fun visitCopySignInstruction(opcode: Opcode) {
        when (opcode) {
            F32_COPYSIGN -> {
                popValue(F32)
                popValue(F32)
                pushValue(F32)
            }

            F64_COPYSIGN -> {
                popValue(F64)
                popValue(F64)
                pushValue(F64)
            }

            else -> throw ValidatorException("Invalid opcode for copy sign instruction: $opcode")
        }

        delegate?.visitCopySignInstruction(opcode)
    }

    override fun visitXorInstruction(opcode: Opcode) {
        when (opcode) {
            I32_XOR -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_XOR -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for xor instruction: $opcode")
        }

        delegate?.visitXorInstruction(opcode)
    }

    override fun visitSimdAbsInstruction(opcode: Opcode) {
        delegate?.visitSimdAbsInstruction(opcode)

        TODO()
    }

    override fun visitMemoryFillInstruction(memoryIndex: UInt) {
        popValue(I32)
        popValue(I32)
        popValue(I32)

        delegate?.visitMemoryFillInstruction(memoryIndex)
    }

    override fun visitMemoryCopyInstruction(targetIndex: UInt, sourceIndex: UInt) {
        popValue(I32)
        popValue(I32)
        popValue(I32)

        delegate?.visitMemoryCopyInstruction(targetIndex, sourceIndex)
    }

    override fun visitMemoryInitInstruction(memoryIndex: UInt, segmentIndex: UInt) {
        popValue(I32)
        popValue(I32)
        popValue(I32)

        delegate?.visitMemoryInitInstruction(memoryIndex, segmentIndex)
    }

    override fun visitTableInitInstruction(segmentIndex: UInt, tableIndex: UInt) {
        delegate?.visitTableInitInstruction(segmentIndex, tableIndex)

        TODO()
    }

    override fun visitDataDropInstruction(segmentIndex: UInt) {
        delegate?.visitDataDropInstruction(segmentIndex)

        TODO()
    }

    override fun visitTableSizeInstruction(tableIndex: UInt) {
        delegate?.visitTableSizeInstruction(tableIndex)

        TODO()
    }

    override fun visitTableGrowInstruction(tableIndex: UInt) {
        delegate?.visitTableGrowInstruction(tableIndex)

        TODO()
    }

    override fun visitTableFillInstruction(tableIndex: UInt) {
        delegate?.visitTableFillInstruction(tableIndex)

        TODO()
    }

    override fun visitGetTableInstruction(tableIndex: UInt) {
        delegate?.visitGetTableInstruction(tableIndex)

        TODO()
    }

    override fun visitSetTableInstruction(tableIndex: UInt) {
        delegate?.visitSetTableInstruction(tableIndex)

        TODO()
    }

    override fun visitTableCopyInstruction(targetTableIndex: UInt, sourceTableIndex: UInt) {
        delegate?.visitTableCopyInstruction(targetTableIndex, sourceTableIndex)

        TODO()
    }

    override fun visitElementDropInstruction(segmentIndex: UInt) {
        delegate?.visitElementDropInstruction(segmentIndex)

        TODO()
    }

    override fun visitAtomicFenceInstruction(reserved: UInt) {
        delegate?.visitAtomicFenceInstruction(reserved)

        TODO()
    }

    override fun visitReferenceEqualInstruction() {
        delegate?.visitReferenceEqualInstruction()

        TODO()
    }

    override fun visitReferenceFunctionInstruction(functionIndex: UInt) {
        delegate?.visitReferenceFunctionInstruction(functionIndex)

        TODO()
    }

    override fun visitReferenceIsNullInstruction() {
        val value = popValue()
        if (!value.isReferenceType()) {
            throw ValidatorException("Expected reference type, found $value")
        }

        pushValue(I32)

        delegate?.visitReferenceIsNullInstruction()
    }

    override fun visitReferenceAsNonNullInstruction() {
        val value = popValue()
        if (!value.isReferenceType()) {
            throw ValidatorException("Expected reference type, found $value")
        }

        pushValue(value)

        delegate?.visitReferenceAsNonNullInstruction()
    }

    override fun visitReferenceNullInstruction(type: WasmType) {
        pushValue(type)

        delegate?.visitReferenceNullInstruction(type)
    }

    override fun visitShiftRightInstruction(opcode: Opcode) {
        when (opcode) {
            I32_SHR_S,
            I32_SHR_U -> {
                popValue(I32)
                popValue(I32)
                pushValue(I32)
            }

            I64_SHR_U,
            I64_SHR_S -> {
                popValue(I64)
                popValue(I64)
                pushValue(I64)
            }

            else -> throw ValidatorException("Invalid opcode for shift right instruction: $opcode")
        }

        delegate?.visitShiftRightInstruction(opcode)
    }

    override fun visitCallRefInstruction(typeIndex: UInt) {
        TODO("Not yet implemented")
    }

    override fun visitBrOnNonNullInstruction(labelIndex: UInt) {
        TODO("Not yet implemented")
    }

    override fun visitBrOnNullInstruction(labelIndex: UInt) {
        TODO("Not yet implemented")
    }

    override fun visitReturnCallRefInstruction(typeIndex: UInt) {
        TODO("Not yet implemented")
    }
}
