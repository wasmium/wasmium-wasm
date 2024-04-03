package org.wasmium.wasm.binary.verifier

import org.wasmium.wasm.binary.Features

public class VerifierOptionsBuilder {
    protected var features: Features = Features()

    public constructor()

    public constructor(options: VerifierOptions) {
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

    public fun features(builder: Features.() -> Unit): VerifierOptionsBuilder {
        features.apply(builder)

        return this
    }

    public fun build(): VerifierOptions = VerifierOptions(
        features = features,
    )
}

public fun VerifierOptions(builder: VerifierOptionsBuilder.() -> Unit): VerifierOptions =
    VerifierOptionsBuilder().apply(builder).build()

public fun VerifierOptions(options: VerifierOptions, builder: VerifierOptionsBuilder.() -> Unit): VerifierOptions =
    VerifierOptionsBuilder(options).apply(builder).build()
