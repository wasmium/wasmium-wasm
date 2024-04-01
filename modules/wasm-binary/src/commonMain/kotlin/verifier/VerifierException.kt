package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.WasmBinaryException

public class VerifierException(
    override val message: String,
) : WasmBinaryException()
