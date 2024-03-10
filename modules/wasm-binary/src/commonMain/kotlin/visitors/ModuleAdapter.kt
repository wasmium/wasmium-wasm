package org.wasmium.wasm.binary.visitors

public class ModuleAdapter(protected val delegate: ModuleVisitor? = null) : ModuleVisitor {
    override fun visit(version: UInt) {
        delegate?.visit(version)
    }

    public override fun visitTypeSection(): TypeSectionVisitor {
        if (delegate != null) {
            return TypeSectionAdapter(delegate.visitTypeSection())
        }

        return TypeSectionAdapter()
    }

    public override fun visitFunctionSection(): FunctionSectionVisitor {
        if (delegate != null) {
            return FunctionSectionAdapter(delegate.visitFunctionSection())
        }

        return FunctionSectionAdapter()
    }

    public override fun visitStartSection(): StartSectionVisitor {
        if (delegate != null) {
            return StartSectionAdapter(delegate.visitStartSection())
        }

        return StartSectionAdapter()
    }

    public override fun visitImportSection(): ImportSectionVisitor {
        if (delegate != null) {
            return ImportSectionAdapter(delegate.visitImportSection())
        }

        return ImportSectionAdapter()
    }

    public override fun visitExportSection(): ExportSectionVisitor {
        if (delegate != null) {
            return ExportSectionAdapter(delegate.visitExportSection())
        }

        return ExportSectionAdapter()
    }

    public override fun visitTableSection(): TableSectionVisitor {
        if (delegate != null) {
            return TableSectionAdapter(delegate.visitTableSection())
        }

        return TableSectionAdapter()
    }

    public override fun visitElementSection(): ElementSectionVisitor {
        if (delegate != null) {
            return ElementSectionAdapter(delegate.visitElementSection())
        }

        return ElementSectionAdapter()
    }

    public override fun visitGlobalSection(): GlobalSectionVisitor {
        if (delegate != null) {
            return GlobalSectionAdapter(delegate.visitGlobalSection())
        }

        return GlobalSectionAdapter()
    }

    public override fun visitCodeSection(): CodeSectionVisitor {
        if (delegate != null) {
            return CodeSectionAdapter(delegate.visitCodeSection())
        }

        return CodeSectionAdapter(null)
    }

    public override fun visitMemorySection(): MemorySectionVisitor {
        if (delegate != null) {
            return MemorySectionAdapter(delegate.visitMemorySection())
        }

        return MemorySectionAdapter()
    }

    public override fun visitDataSection(): DataSectionVisitor {
        if (delegate != null) {
            return DataSectionAdapter(delegate.visitDataSection())
        }

        return DataSectionAdapter()
    }

    public override fun visitExceptionSection(): ExceptionSectionVisitor {
        if (delegate != null) {
            return ExceptionSectionAdapter(delegate.visitExceptionSection())
        }

        return ExceptionSectionAdapter()
    }

    public override fun visitRelocationSection(): RelocationSectionVisitor {
        if (delegate != null) {
            return RelocationSectionAdapter(delegate.visitRelocationSection())
        }

        return RelocationSectionAdapter()
    }

    public override fun visitCustomSection(): CustomSectionVisitor {
        if (delegate != null) {
            return CustomSectionAdapter(delegate.visitCustomSection())
        }

        return CustomSectionAdapter()
    }

    public override fun visitLinkingSection(): LinkingSectionVisitor {
        if (delegate != null) {
            return LinkingSectionAdapter(delegate.visitLinkingSection())
        }

        return LinkingSectionAdapter()
    }

    public override fun visitNameSection(): NameSectionVisitor {
        if (delegate != null) {
            return NameSectionAdapter(delegate.visitNameSection())
        }

        return NameSectionAdapter()
    }

    public override fun visitEnd() {
        delegate?.visitEnd()
    }
}
