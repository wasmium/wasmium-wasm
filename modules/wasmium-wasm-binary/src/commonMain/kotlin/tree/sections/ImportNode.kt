package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ExternalKind
import org.wasmium.wasm.binary.visitor.ImportSectionVisitor

public abstract class ImportNode(
    /** The module being imported from.  */
    public open val module: String,
    /** The name of the imported value.  */
    public open val name: String,
    /** The type of the import.  */
    public open val externalKind: ExternalKind,
) {
    public abstract fun accept(importSectionVisitor: ImportSectionVisitor)
}
