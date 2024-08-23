package org.wasmium.wasm.binary.tree

import org.wasmium.wasm.binary.WasmBinary.Meta.VERSION_1
import org.wasmium.wasm.binary.WasmBinary.Meta.VERSION_2

public enum class WasmVersion(public val version: UInt) {
    V1(VERSION_1),
    V2(VERSION_2),
    ;
}
