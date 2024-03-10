package org.wasmium.wasm.binary

public class WriterOptionsBuilder {
    protected var isDebugNamesEnabled: Boolean = false

    public constructor()

    public constructor(options: WriterOptions) {
        isDebugNamesEnabled = options.isDebugNamesEnabled
    }

    public fun debugNames(enable: Boolean): WriterOptionsBuilder {
        isDebugNamesEnabled = enable
        return this
    }

    public fun build(): WriterOptions = WriterOptions(
        isDebugNamesEnabled = isDebugNamesEnabled,
    )
}

public fun WriterOptions(builder: WriterOptionsBuilder.() -> Unit): WriterOptions =
    WriterOptionsBuilder().apply(builder).build()

public fun WriterOptions(options: WriterOptions, builder: WriterOptionsBuilder.() -> Unit): WriterOptions =
    WriterOptionsBuilder(options).apply(builder).build()
