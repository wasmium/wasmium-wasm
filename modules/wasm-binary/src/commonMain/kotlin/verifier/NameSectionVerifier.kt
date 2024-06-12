package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.IndexName
import org.wasmium.wasm.binary.visitor.NameSectionVisitor

public class NameSectionVerifier(private val delegate: NameSectionVisitor? = null, private val context: VerifierContext) : NameSectionVisitor {
    private var done: Boolean = false
    private var numberOfNames: UInt = 0u

    override fun visitModuleName(name: String) {
        checkEnd()

        numberOfNames++

        delegate?.visitModuleName(name)
    }

    override fun visitFunctionNames(names: List<IndexName>) {
        checkEnd()

        for ((functionIndex, _) in names) {
            if (functionIndex > context.numberOfTotalFunctions) {
                context.messages.add("warning: Function index out of bounds in name section, function subsection at index %$functionIndex")
            }
        }

        numberOfNames++

        delegate?.visitFunctionNames(names)
    }

    override fun visitGlobalNames(names: List<IndexName>) {
        checkEnd()

        for ((globalIndex, _) in names) {
            if (globalIndex >= context.numberOfTotalGlobals) {
                context.messages.add("warning: Global index out of bounds in name section, global subsection at index %$globalIndex")
            }
        }

        numberOfNames++

        delegate?.visitGlobalNames(names)
    }

    override fun visitTagNames(names: List<IndexName>) {
        checkEnd()

        for ((tagIndex, _) in names) {
            if (tagIndex >= context.numberOfTags) {
                context.messages.add("warning: Tag index out of bounds in name section, tag subsection at index %$tagIndex")
            }
        }

        numberOfNames++

        delegate?.visitTagNames(names)
    }

    override fun visitTableNames(names: List<IndexName>) {
        checkEnd()

        for ((tableIndex, _) in names) {
            if (tableIndex >= context.numberOfTotalTables) {
                context.messages.add("warning: Table index out of bounds in name section, table subsection at index %$tableIndex")
            }
        }

        numberOfNames++

        delegate?.visitTableNames(names)
    }

    override fun visitMemoryNames(names: List<IndexName>) {
        checkEnd()

        for ((memoryIndex, _) in names) {
            if (memoryIndex >= context.numberTotalMemories) {
                context.messages.add("warning: Memory index out of bounds in name section, memory subsection at index %$memoryIndex")
            }
        }

        numberOfNames++

        delegate?.visitMemoryNames(names)
    }

    override fun visitElementNames(names: List<IndexName>) {
        checkEnd()

        for ((elementIndex, _) in names) {
            if (elementIndex >= context.numberOfElements) {
                context.messages.add("warning: Element index out of bounds in name section, element subsection at index %$elementIndex")
            }
        }

        numberOfNames++

        delegate?.visitElementNames(names)
    }

    override fun visitDataNames(names: List<IndexName>) {
        checkEnd()

        for ((dataIndex, _) in names) {
            if (dataIndex >= context.numberOfDataSegments) {
                context.messages.add("warning: Data index out of bounds in name section, data subsection at index %$dataIndex")
            }
        }

        numberOfNames++

        delegate?.visitDataNames(names)
    }

    override fun visitTypeNames(names: List<IndexName>) {
        checkEnd()

        for ((typeIndex, _) in names) {
            if (typeIndex >= context.numberOfTypes) {
                context.messages.add("warning: Type index out of bounds in name section, type subsection at index %$typeIndex")
            }
        }

        numberOfNames++

        delegate?.visitTypeNames(names)
    }

    override fun visitLocalNames(functionIndex: UInt, names: List<IndexName>) {
        checkEnd()

        if (functionIndex > context.numberOfTotalFunctions) {
            context.messages.add("warning: Function index out of bounds in name section, function subsection at index %$functionIndex")
        }

        for ((localIndex, _) in names) {
            if (localIndex > WasmBinary.MAX_FUNCTION_LOCALS) {
                context.messages.add("warning: Local names of size $localIndex greater then ${WasmBinary.MAX_FUNCTION_LOCALS} locals for function index %$functionIndex")
            }
        }

        numberOfNames++

        delegate?.visitLocalNames(functionIndex, names)
    }

    override fun visitLabelNames(functionIndex: UInt, names: List<IndexName>) {
        checkEnd()

        if (functionIndex > context.numberOfTotalFunctions) {
            context.messages.add("warning: Function index out of bounds in name section, function subsection at index %$functionIndex")
        }

        for ((labelIndex, _) in names) {
            if (labelIndex > WasmBinary.MAX_NAMES_LABELS) {
                context.messages.add("warning: Label names of size $labelIndex greater then ${WasmBinary.MAX_NAMES_LABELS} labels for function index %$functionIndex")
            }
        }

        numberOfNames++

        delegate?.visitLabelNames(functionIndex, names)
    }

    override fun visitFieldNames(functionIndex: UInt, names: List<IndexName>) {
        checkEnd()

        if (functionIndex > context.numberOfTotalFunctions) {
            context.messages.add("warning: Function index out of bounds in name section, function subsection at index %$functionIndex")
        }

        for ((fieldIndex, _) in names) {
            if (fieldIndex > WasmBinary.MAX_NAMES_FIELDS) {
                context.messages.add("warning: Field names of size $fieldIndex greater then ${WasmBinary.MAX_NAMES_FIELDS} fields for function index %$functionIndex")
            }
        }

        numberOfNames++

        delegate?.visitFieldNames(functionIndex, names)
    }

    override fun visitEnd() {
        checkEnd()

        if (numberOfNames > WasmBinary.MAX_NAMES) {
            throw VerifierException("Number of names $numberOfNames exceed the maximum of ${WasmBinary.MAX_NAMES}")
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
