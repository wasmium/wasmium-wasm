package org.wasmium.wasm.binary.tree.instructions

public class TryCatchArgument(
    public val kind: TryCatchKind,
    public val label: UInt,
    public val tag: UInt?,
) {
    public enum class TryCatchKind(public val kindId: UInt) {
        CATCH(0u),
        CATCH_REF(1u),
        CATCH_ALL(2u),
        CATCH_ALL_REF(3u),
        ;
    }
}
