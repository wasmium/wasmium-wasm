package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.visitors.CodeSectionVisitor
import org.wasmium.wasm.binary.visitors.DataCountSectionVisitor
import org.wasmium.wasm.binary.visitors.DataSectionVisitor
import org.wasmium.wasm.binary.visitors.ElementSectionVisitor
import org.wasmium.wasm.binary.visitors.ExceptionSectionVisitor
import org.wasmium.wasm.binary.visitors.ExportSectionVisitor
import org.wasmium.wasm.binary.visitors.FunctionSectionVisitor
import org.wasmium.wasm.binary.visitors.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitors.ImportSectionVisitor
import org.wasmium.wasm.binary.visitors.LinkingSectionVisitor
import org.wasmium.wasm.binary.visitors.MemorySectionVisitor
import org.wasmium.wasm.binary.visitors.ModuleVisitor
import org.wasmium.wasm.binary.visitors.NameSectionVisitor
import org.wasmium.wasm.binary.visitors.RelocationSectionVisitor
import org.wasmium.wasm.binary.visitors.StartSectionVisitor
import org.wasmium.wasm.binary.visitors.TableSectionVisitor
import org.wasmium.wasm.binary.visitors.TypeSectionVisitor
import org.wasmium.wasm.binary.visitors.UnknownSectionVisitor

public class ModuleValidator : ModuleVisitor {
    override fun visitHeader(version: UInt) {
        // empty
    }

    override fun visitTypeSection(): TypeSectionVisitor? {
        return null
    }

    override fun visitFunctionSection(): FunctionSectionVisitor? {
        return null
    }

    override fun visitStartSection(functionIndex: UInt): StartSectionVisitor? {
        return null
    }

    override fun visitImportSection(): ImportSectionVisitor? {
        return null
    }

    override fun visitExportSection(): ExportSectionVisitor? {
        return null
    }

    override fun visitTableSection(): TableSectionVisitor? {
        return null
    }

    override fun visitElementSection(): ElementSectionVisitor? {
        return null
    }

    override fun visitGlobalSection(): GlobalSectionVisitor? {
        return null
    }

    override fun visitCodeSection(): CodeSectionVisitor? {
        return null
    }

    override fun visitMemorySection(): MemorySectionVisitor? {
        return null
    }

    override fun visitDataSection(): DataSectionVisitor? {
        return null
    }

    override fun visitExceptionSection(): ExceptionSectionVisitor? {
        return null
    }

    override fun visitRelocationSection(): RelocationSectionVisitor? {
        return null
    }

    override fun visitUnknownSection(name: String, content: ByteArray): UnknownSectionVisitor? {
        return null
    }

    override fun visitLinkingSection(): LinkingSectionVisitor? {
        return null
    }

    override fun visitNameSection(): NameSectionVisitor? {
        return null
    }

    override fun visitDataCountSection(dataCount: UInt): DataCountSectionVisitor? {
        return null
    }

    override fun visitEnd() {
        // empty
    }
}
