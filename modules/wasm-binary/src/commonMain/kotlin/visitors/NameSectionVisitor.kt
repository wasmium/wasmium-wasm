package org.wasmium.wasm.binary.visitors

public interface NameSectionVisitor {
    public fun visitModuleName(name: String)

    public fun visitFunctionName(functionIndex: UInt, name: String)

    public fun visitGlobalName(functionIndex: UInt, name: String)

    public fun visitTagName(functionIndex: UInt, name: String)

    public fun visitTableName(functionIndex: UInt, name: String)

    public fun visitMemoryName(functionIndex: UInt, name: String)

    public fun visitElementName(functionIndex: UInt, name: String)

    public fun visitDataName(functionIndex: UInt, name: String)

    public fun visitLocalName(functionIndex: UInt, localIndex: UInt, name: String)

    public fun visitLabelName(functionIndex: UInt, localIndex: UInt, name: String)

    public fun visitEnd()
}
