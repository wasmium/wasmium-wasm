package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ResizableLimits

public class MemoryTypeNode {
    public var memoryIndex: UInt? = null
    public var limits: ResizableLimits? = null
}
