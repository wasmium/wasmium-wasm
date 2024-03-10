package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.visitors.Opcode

public abstract class IndexInstruction(opcode: Opcode, public val index: UInt) : Instruction(opcode)
