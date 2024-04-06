package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType

public class LocalType {
    public var localIndex: UInt? = null
    public var count: UInt? = null
    public var type: WasmType? = null
}
