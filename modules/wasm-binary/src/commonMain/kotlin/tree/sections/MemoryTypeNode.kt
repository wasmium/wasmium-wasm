package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.ResizableLimits

public class MemoryTypeNode(
    public val memoryIndex: UInt,
    public val limits: ResizableLimits,
)
