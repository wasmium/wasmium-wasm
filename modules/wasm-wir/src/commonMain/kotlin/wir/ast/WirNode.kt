package org.wasmium.wir.ast

import org.wasmium.wir.ast.visitor.WirVisitable

public abstract class WirNode(public open val kind: WirNodeKind) : WirVisitable
