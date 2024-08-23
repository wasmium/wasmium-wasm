package org.wasmium.wasm.binary.tree.instructions

import org.wasmium.wasm.binary.tree.Opcode

public abstract class AbstractInstruction(public override val opcode: Opcode) : Instruction
