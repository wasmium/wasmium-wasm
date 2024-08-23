package org.wasmium.wasm.binary.reader

import org.wasmium.wasm.binary.Features
import org.wasmium.wasm.binary.tree.SectionKind

public class ReaderOptionsBuilder {
    protected var debugNames: Boolean = false
    protected var skipSections: List<SectionKind> = mutableListOf()
    protected var features: Features = Features()

    public constructor()

    public constructor(options: ReaderOptions) {
        debugNames = options.debugNames
        skipSections = options.skipSections
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
        debugNames = enable

        return this
    }

    public fun skipSections(sections: List<SectionKind>): ReaderOptionsBuilder {
        skipSections = sections

        return this
    }

    public fun features(builder: Features.() -> Unit): ReaderOptionsBuilder {
        features.apply(builder)

        return this
    }

    public fun build(): ReaderOptions = ReaderOptions(
        debugNames = debugNames,
        skipSections = skipSections,
        features = features,
    )
}

public fun ReaderOptions(builder: ReaderOptionsBuilder.() -> Unit): ReaderOptions =
    ReaderOptionsBuilder().apply(builder).build()

public fun ReaderOptions(options: ReaderOptions, builder: ReaderOptionsBuilder.() -> Unit): ReaderOptions =
    ReaderOptionsBuilder(options).apply(builder).build()
