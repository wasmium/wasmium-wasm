package org.wasmium.wasm.binary

public inline fun repeatUInt(times: UInt, action: (UInt) -> Unit) {
    for(index in 0u until times) {
        action(index)
    }
}
