package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.ByteBuffer
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_EXPRESSIONS
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_KIND
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_PASSIVE_OR_DECLARATIVE
import org.wasmium.wasm.binary.WasmBinary.ELEMENT_TABLE_INDEX
import org.wasmium.wasm.binary.WasmBinaryWriter
import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitors.ElementSegmentVisitor
import org.wasmium.wasm.binary.visitors.InitializerExpressionVisitor

public class ElementSegmentWriter(
    private val context: WriterContext,
    private val body: ByteBuffer,
) : ElementSegmentVisitor {
    private var numberOfExpressions = 0u
    private val expressionsBody = ByteBuffer()
    private var elementType = 0u
    private var tableIndex: UInt? = null
    private var type: WasmType? = null
    private var elementIndices: List<UInt> = mutableListOf()
    private var offsetBody = ByteBuffer()

    public override fun visitNonActiveMode(passive: Boolean) {
        elementType = elementType or ELEMENT_PASSIVE_OR_DECLARATIVE.toUInt()

        if (passive) {
            elementType = elementType or ELEMENT_TABLE_INDEX.toUInt()
        }
    }

    public override fun visitActiveMode(tableIndex: UInt): InitializerExpressionVisitor {
        if (tableIndex != 0u) {
            elementType = elementType or ELEMENT_TABLE_INDEX.toUInt()
            this.tableIndex = tableIndex
        }

        return InitializerExpressionWriter(context, offsetBody)
    }

    override fun visitType(type: WasmType) {
        // TODO check only FUNC_REF or EXTERN_REF
        this.type = type
    }

    public override fun visitInitializerExpression(): InitializerExpressionVisitor {
        elementType = elementType or ELEMENT_EXPRESSIONS.toUInt()

        numberOfExpressions++

        return InitializerExpressionWriter(context, expressionsBody)
    }

    public override fun visitElementIndices(elementIndices: List<UInt>) {
        this.elementIndices = elementIndices
    }

    public override fun visitEnd() {
        WasmBinaryWriter(body).writeVarUInt32(elementType)

        if ((elementType and ELEMENT_PASSIVE_OR_DECLARATIVE.toUInt()) == 0u) {
            if ((elementType and ELEMENT_TABLE_INDEX.toUInt()) != 0u) {
                WasmBinaryWriter(body).writeIndex(tableIndex!!)
            }

            WasmBinaryWriter(body).writeByteArray(offsetBody.toByteArray())
        }

        if ((elementType and ELEMENT_EXPRESSIONS.toUInt()) != 0u) {
            if ((elementType and 0b011u) != 0u) {
                WasmBinaryWriter(body).writeType(type!!)
            }

            WasmBinaryWriter(body).writeVarUInt32(numberOfExpressions)
            WasmBinaryWriter(body).writeByteArray(expressionsBody.toByteArray())
        } else {
            if ((elementType and 0b011u) != 0u) {
                WasmBinaryWriter(body).writeVarUInt32(ELEMENT_KIND.toUInt())
            }

            WasmBinaryWriter(body).writeVarUInt32(elementIndices.size.toUInt())
            for (index in elementIndices) {
                WasmBinaryWriter(body).writeVarUInt32(index)
            }
        }
    }
}
