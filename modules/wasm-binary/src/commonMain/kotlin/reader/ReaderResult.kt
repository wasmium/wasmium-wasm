package org.wasmium.wasm.binary.reader

public sealed class ReaderResult {
    public open val messages: MutableList<String> = mutableListOf()

    public class Success(override val messages: MutableList<String>) : ReaderResult()
    public class Failure(override val messages: MutableList<String>) : ReaderResult()
}
