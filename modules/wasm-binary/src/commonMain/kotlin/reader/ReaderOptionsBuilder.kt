package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.Features

public class ReaderOptionsBuilder {
    protected var isDebugNamesEnabled: Boolean = false
    protected var isSkipCodeSection: Boolean = false
    protected var features: Features = Features()

    public constructor()

    public constructor(options: ReaderOptions) {
        isDebugNamesEnabled = options.isDebugNamesEnabled
        isSkipCodeSection = options.isSkipCodeSection
        features.apply {
            isExceptionHandlingEnabled = options.features.isExceptionHandlingEnabled
            isThreadsEnabled = options.features.isThreadsEnabled
            isSIMDEnabled = options.features.isSIMDEnabled
            isGCEnabled = options.features.isGCEnabled
            isFCEnabled = options.features.isFCEnabled
            isTailCallsEnabled = options.features.isTailCallsEnabled
            isReferenceTypesEnabled = options.features.isReferenceTypesEnabled
            isSignExtensionEnabled = options.features.isSignExtensionEnabled
            isTypedFunctionReferencesEnabled = options.features.isTypedFunctionReferencesEnabled
            isRelaxedSIMDEnabled = options.features.isRelaxedSIMDEnabled
        }
    }

    public fun debugNames(enable: Boolean): ReaderOptionsBuilder {
        isDebugNamesEnabled = enable

        return this
    }

    public fun skipCodeSection(enable: Boolean): ReaderOptionsBuilder {
        isSkipCodeSection = enable

        return this
    }

    public fun features(builder: Features.() -> Unit): ReaderOptionsBuilder {
        features.apply(builder)

        return this
    }

    public fun build(): ReaderOptions = ReaderOptions(
        isDebugNamesEnabled = isDebugNamesEnabled,
        isSkipCodeSection = isSkipCodeSection,
        features = features,
    )
}

public fun ReaderOptions(builder: ReaderOptionsBuilder.() -> Unit): ReaderOptions =
    ReaderOptionsBuilder().apply(builder).build()

public fun ReaderOptions(options: ReaderOptions, builder: ReaderOptionsBuilder.() -> Unit): ReaderOptions =
    ReaderOptionsBuilder(options).apply(builder).build()
