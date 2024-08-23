package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinaryException

public class VerifierException(public override val message: String) : WasmBinaryException()
