package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.ParserException
import org.wasmium.wasm.binary.WasmBinaryReader
import org.wasmium.wasm.binary.visitors.ModuleVisitor

public class TagSectionReader(private val context: ReaderContext) {
    public fun readTagSection(source: WasmBinaryReader, visitor: ModuleVisitor) {
        if (context.options.features.isExceptionHandlingEnabled) {
            throw ParserException("Tag section is not enabled")
        }

        val numberOfTagTypes = source.readVarUInt32()

        val tagSectionVisitor = visitor.visitTagSection()
        for (index in 0u until numberOfTagTypes) {
            val tagType = source.readTagType()

            tagSectionVisitor.visitTag(tagType)
        }
        tagSectionVisitor.visitEnd()
    }
}
