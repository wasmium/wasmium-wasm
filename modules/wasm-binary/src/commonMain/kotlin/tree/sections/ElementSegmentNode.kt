package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType
import org.wasmium.wasm.binary.visitor.ElementSegmentVisitor
import org.wasmium.wasm.binary.visitor.ExpressionVisitor

public class ElementSegmentNode : ElementSegmentVisitor {
    public var initializers: MutableList<ExpressionNode> = mutableListOf()

    public var elementIndices: MutableList<UInt> = mutableListOf()

    /** The reference type of the element segment. */
    public var type: WasmType? = null

    /** The table index of the element segment, if it is active. */
    public var tableIndex: UInt? = null

    /** The offset expression of an active element segment, or `null` if it is not active. */
    public var offsetExpression: ExpressionNode? = null

    /** Whether the element segment is passive, if it is not active. True if passive, false if declarative, ignored if active. */
    public var passive: Boolean = false

    public fun accept(elementSegmentVisitor: ElementSegmentVisitor) {
        if (offsetExpression == null) {
            elementSegmentVisitor.visitNonActiveMode(passive)
        } else {
            val offset = elementSegmentVisitor.visitActiveMode(tableIndex!!)
            offsetExpression!!.accept(offset)
        }

        if (type != null) {
            elementSegmentVisitor.visitType(type!!)
        }

        if (elementIndices.isNotEmpty()) {
            elementSegmentVisitor.visitElementIndices(elementIndices)
        } else if (initializers.isNotEmpty()) {
            for (initializer in initializers) {
                val expressionVisitor = elementSegmentVisitor.visitExpression()
                initializer.accept(expressionVisitor)
            }
        }

        elementSegmentVisitor.visitEnd()
    }

    override fun visitElementIndices(elementIndices: List<UInt>) {
        this.elementIndices.addAll(elementIndices)
    }

    override fun visitNonActiveMode(passive: Boolean) {
        this.passive = passive
    }

    override fun visitActiveMode(tableIndex: UInt): ExpressionVisitor {
        this.tableIndex = tableIndex

        this.offsetExpression = ExpressionNode()

        return this.offsetExpression!!
    }

    override fun visitType(type: WasmType) {
        this.type = type
    }

    override fun visitExpression(): ExpressionVisitor {
        val initializer = ExpressionNode()

        initializers.add(initializer)

        return initializer
    }

    public override fun visitEnd() {
        // empty
    }
}
