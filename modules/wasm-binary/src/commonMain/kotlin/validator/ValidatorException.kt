package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.WasmBinaryException

public class ValidatorException(public override val message: String) : WasmBinaryException()
