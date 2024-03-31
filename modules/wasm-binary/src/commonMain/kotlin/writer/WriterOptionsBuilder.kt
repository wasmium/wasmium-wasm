package org.wasmium.wasm.binary.writer

import org.wasmium.wasm.binary.Features

public class WriterOptionsBuilder {
    protected var isDebugNamesEnabled: Boolean = false
    protected var isRelocatableEnabled: Boolean = false
    protected var isCanonical: Boolean = false

    /** Available features. */
    protected var features: Features = Features()

    public constructor()

    public constructor(options: WriterOptions) {
        isDebugNamesEnabled = options.isDebugNamesEnabled
    }

    public fun debugNames(enable: Boolean): WriterOptionsBuilder {
        isDebugNamesEnabled = enable
        return this
    }

    public fun relocatable(enable: Boolean): WriterOptionsBuilder {
        isRelocatableEnabled = enable
        return this
    }

    public fun canonical(enable: Boolean): WriterOptionsBuilder {
        isCanonical = enable
        return this
    }

    public fun features(builder: Features.() -> Unit): WriterOptionsBuilder {
        features.apply(builder)
        return this
    }

    public fun build(): WriterOptions = WriterOptions(
        isDebugNamesEnabled = isDebugNamesEnabled,
        isRelocatableEnabled = isRelocatableEnabled,
        isCanonical = isCanonical,
        features = features,
    )
}

public fun WriterOptions(builder: WriterOptionsBuilder.() -> Unit): WriterOptions =
    WriterOptionsBuilder().apply(builder).build()

public fun WriterOptions(options: WriterOptions, builder: WriterOptionsBuilder.() -> Unit): WriterOptions =
    WriterOptionsBuilder(options).apply(builder).build()
