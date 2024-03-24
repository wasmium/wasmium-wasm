package org.wasmium.wasm.binary.tree.sections

import org.wasmium.wasm.binary.tree.WasmType

/**
 * Description of the exception type signature which corresponds to the data fields of an exception.
 */
public class ExceptionTypeNode(
    public val exceptionIndex: UInt,
    /** The type of each element in the signature  */
    public val exceptionTypes: Array<WasmType>,
)
