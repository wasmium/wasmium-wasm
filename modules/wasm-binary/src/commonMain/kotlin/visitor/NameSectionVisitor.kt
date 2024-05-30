package org.wasmium.wasm.binary.visitors

import org.wasmium.wasm.binary.tree.IndexName

public interface NameSectionVisitor {

    public fun visitModuleName(name: String)

    public fun visitFunctionNames(names: List<IndexName>)

    public fun visitGlobalNames(names: List<IndexName>)

    public fun visitTagNames(names: List<IndexName>)

    public fun visitTableNames(names: List<IndexName>)

    public fun visitMemoryNames(names: List<IndexName>)

    public fun visitElementNames(names: List<IndexName>)

    public fun visitDataNames(names: List<IndexName>)

    public fun visitTypeNames(names: List<IndexName>)

    public fun visitLocalNames(functionIndex: UInt, names: List<IndexName>)

    public fun visitLabelNames(functionIndex: UInt, names: List<IndexName>)

    public fun visitFieldNames(functionIndex: UInt, names: List<IndexName>)

    public fun visitEnd()
}
