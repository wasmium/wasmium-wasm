package org.wasmium.wasm.binary.validator

import org.wasmium.wasm.binary.Features

public class ValidatorOptionsBuilder {
    protected var features: Features = Features()

    public constructor()

    public constructor(options: ValidatorOptions) {
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

    public fun features(builder: Features.() -> Unit): ValidatorOptionsBuilder {
        features.apply(builder)

        return this
    }

    public fun build(): ValidatorOptions = ValidatorOptions(
        features = features,
    )
}

public fun ValidatorOptions(builder: ValidatorOptionsBuilder.() -> Unit): ValidatorOptions =
    ValidatorOptionsBuilder().apply(builder).build()

public fun ValidatorOptions(options: ValidatorOptions, builder: ValidatorOptionsBuilder.() -> Unit): ValidatorOptions =
    ValidatorOptionsBuilder(options).apply(builder).build()
