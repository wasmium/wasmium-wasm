package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinary
import org.wasmium.wasm.binary.visitor.CodeSectionVisitor
import org.wasmium.wasm.binary.visitor.DataCountSectionVisitor
import org.wasmium.wasm.binary.visitor.DataSectionVisitor
import org.wasmium.wasm.binary.visitor.ElementSectionVisitor
import org.wasmium.wasm.binary.visitor.ExportSectionVisitor
import org.wasmium.wasm.binary.visitor.ExternalDebugSectionVisitor
import org.wasmium.wasm.binary.visitor.FunctionSectionVisitor
import org.wasmium.wasm.binary.visitor.GlobalSectionVisitor
import org.wasmium.wasm.binary.visitor.ImportSectionVisitor
import org.wasmium.wasm.binary.visitor.LinkingSectionVisitor
import org.wasmium.wasm.binary.visitor.MemorySectionVisitor
import org.wasmium.wasm.binary.visitor.ModuleVisitor
import org.wasmium.wasm.binary.visitor.NameSectionVisitor
import org.wasmium.wasm.binary.visitor.RelocationSectionVisitor
import org.wasmium.wasm.binary.visitor.SourceMapSectionVisitor
import org.wasmium.wasm.binary.visitor.StartSectionVisitor
import org.wasmium.wasm.binary.visitor.TableSectionVisitor
import org.wasmium.wasm.binary.visitor.TagSectionVisitor
import org.wasmium.wasm.binary.visitor.TypeSectionVisitor
import org.wasmium.wasm.binary.visitor.UnknownSectionVisitor

public class ModuleVerifier(private val delegate: ModuleVisitor? = null, private val options: VerifierOptions) : ModuleVisitor {
    private val context = VerifierContext(options)
    private var started = false
    private var done = false
    private var numberOfSections = 0u

    override fun visitHeader(version: UInt) {
        checkEnd()

        started = true

        delegate?.visitHeader(version)
    }

    override fun visitTypeSection(): TypeSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return TypeSectionVerifier(delegate?.visitTypeSection(), context)
    }

    override fun visitFunctionSection(): FunctionSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return FunctionSectionVerifier(delegate?.visitFunctionSection(), context)
    }

    override fun visitStartSection(functionIndex: UInt): StartSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return StartSectionVerifier(delegate?.visitStartSection(functionIndex), context, functionIndex)
    }

    override fun visitImportSection(): ImportSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return ImportSectionVerifier(delegate?.visitImportSection(), context)
    }

    override fun visitExportSection(): ExportSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return ExportSectionVerifier(delegate?.visitExportSection(), context)
    }

    override fun visitTableSection(): TableSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return TableSectionVerifier(delegate?.visitTableSection(), context)
    }

    override fun visitElementSection(): ElementSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return ElementSectionVerifier(delegate?.visitElementSection(), context)
    }

    override fun visitGlobalSection(): GlobalSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return GlobalSectionVerifier(delegate?.visitGlobalSection(), context)
    }

    override fun visitCodeSection(): CodeSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return CodeSectionVerifier(delegate?.visitCodeSection(), context)
    }

    override fun visitMemorySection(): MemorySectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return MemorySectionVerifier(delegate?.visitMemorySection(), context)
    }

    override fun visitDataSection(): DataSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return DataSectionVerifier(delegate?.visitDataSection(), context)
    }

    override fun visitRelocationSection(): RelocationSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return RelocationSectionVerifier(delegate?.visitRelocationSection(), context)
    }

    override fun visitUnknownSection(name: String, content: ByteArray): UnknownSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return UnknownSectionVerifier(delegate?.visitUnknownSection(name, content), context)
    }

    override fun visitLinkingSection(): LinkingSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return LinkingSectionVerifier(delegate?.visitLinkingSection(), context)
    }

    override fun visitNameSection(): NameSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return NameSectionVerifier(delegate?.visitNameSection(), context)
    }

    override fun visitDataCountSection(dataCount: UInt): DataCountSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return DataCountSectionVerifier(delegate?.visitDataCountSection(dataCount), context, dataCount)
    }

    override fun visitSourceMapSection(sourceMapUrl: String): SourceMapSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return SourceMapSectionVerifier(delegate?.visitSourceMapSection(sourceMapUrl), context)
    }

    override fun visitExternalDebugSection(externalDebugUrl: String): ExternalDebugSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return ExternalDebugSectionVerifier(delegate?.visitExternalDebugSection(externalDebugUrl), context)
    }

    override fun visitTagSection(): TagSectionVisitor {
        checkStarted()
        checkEnd()

        numberOfSections++

        return TagSectionVerifier(delegate?.visitTagSection(), context)
    }

    override fun visitEnd() {
        checkStarted()
        checkEnd()

        if (numberOfSections > WasmBinary.MAX_SECTIONS) {
            throw VerifierException("Number of sections $numberOfSections exceed the maximum of ${WasmBinary.MAX_SECTIONS}")
        }

        done = true
        delegate?.visitEnd()
    }

    private fun checkStarted() {
        if (!started) {
            throw VerifierException("Cannot call a visit method before visit(Version) has been called.")
        }
    }

    private fun checkEnd() {
        if (done) {
            throw VerifierException("Cannot call a visit method after visitEnd has been called.")
        }
    }
}
