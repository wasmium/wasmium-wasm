package org.wasmium.wir.ast.expressions

import org.wasmium.wir.ast.WirNode
import org.wasmium.wir.ast.WirNodeKind
import org.wasmium.wir.ast.visitor.WirVisitable

public abstract class WirExpression(kind: WirNodeKind) : WirNode(kind), WirVisitable
