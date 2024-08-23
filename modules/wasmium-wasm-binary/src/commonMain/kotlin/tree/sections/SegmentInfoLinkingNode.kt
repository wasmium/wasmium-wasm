package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.LinkingKind

public class SegmentInfoLinkingNode : LinkingNode(LinkingKind.SEGMENT_INFO) {
    public var name: String? = null
    public var alignment: UInt? = null
    public var flags: UInt? = null
}
