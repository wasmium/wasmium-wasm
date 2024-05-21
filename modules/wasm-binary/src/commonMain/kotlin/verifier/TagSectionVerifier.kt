package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.tree.TagType
import org.wasmium.wasm.binary.visitors.TagSectionVisitor

public class TagSectionVerifier(private val delegate: TagSectionVisitor? = null, private val context: VerifierContext) : TagSectionVisitor {
    private var done: Boolean = false

    override fun visitTag(tagType: TagType) {
        checkEnd()

        if (tagType.index >= context.numberOfTypes) {
            throw ParserException("Invalid tag signature index: %${tagType.index}")
        }

        context.numberOfTags++

        delegate?.visitTag(tagType)
    }

    override fun visitEnd() {
        checkEnd()

        if (context.numberOfTags > WasmBinary.MAX_TAGS) {
            throw VerifierException("Number of exceptions ${context.numberOfTags} exceed the maximum of ${WasmBinary.MAX_TAGS}")
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
