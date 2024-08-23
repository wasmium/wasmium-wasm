package org.wasmium.wir.ast

/**
 * Base of objects exported or imported by a module.
 */
public abstract class WirObject(
    public override val kind: WirNodeKind,
) : WirNode(kind) {
    /** Name of the module.  */
    public var module: String = ""

    /** Name of the table.  */
    public var name: String = ""

    /** Exported name of the table.  */
    public var exportName: String = ""

    /** Imported name of the table.  */
    public var importName: String = ""

    /** Whether this function is exported.  */
    public var isExported: Boolean = false

    /** Whether this function is imported.  */
    public var isImported: Boolean = false
}
