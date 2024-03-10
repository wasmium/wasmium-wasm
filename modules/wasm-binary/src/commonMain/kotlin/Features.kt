package org.wasmium.wasm.binary

public class Features {
    /** Tails call proposal enabled. */
    public var isTailCallsEnabled: Boolean = false

    /** Extended constant expressions proposal enabled. */
    public var isExtendedConstantExpressionsEnabled: Boolean = false

    /** Typed function references proposal enabled. */
    public var isTypedFunctionReferencesEnabled: Boolean = false

    /** Garbage Collector proposal enabled. */
    public var isGCEnabled: Boolean = false

    /** Multiple Memory proposal enabled. */
    public var isMultipleMemoryEnabled: Boolean = false

    /** Threading proposal enabled. */
    public var isThreadsEnabled: Boolean = false

    /** Relaxed SIMD proposal enabled. */
    public var isRelaxedSIMDEnabled: Boolean = false

    /** Custom Annotation Syntax in the Text Format proposal enabled. */
    public var isCustomAnnotationSyntaxEnabled: Boolean = false

    /** Memory64 proposal enabled. */
    public var isMemory64Enabled: Boolean = false

    /** Exception handling proposal enabled. */
    public var isExceptionHandlingEnabled: Boolean = false

    /** Web Content Security Policy proposal enabled. */
    public var isWebContentSecurityPolicyEnabled: Boolean = false

    /** Branch Hinting proposal enabled. */
    public var isBranchHintingEnabled: Boolean = false

    /** JS Promise Integration proposal enabled. */
    public var isJSPromiseIntegrationEnabled: Boolean = false

    /** Type Reflection for WebAssembly JavaScript API proposal enabled. */
    public var isTypeReflectionEnabled: Boolean = false

    /** ECMAScript module integration proposal enabled. */
    public var isECMAScriptModuleIntegrationEnabled: Boolean = false

    /** Relaxed dead code validation proposal enabled. */
    public var isRelaxedDeadCodeValidationEnabled: Boolean = false

    /** Numeric Values in WAT Data Segments proposal enabled. */
    public var isNumericValuesInWATDataSegmentsEnabled: Boolean = false

    /** Instrument and Tracing Technology proposal enabled. */
    public var isInstrumentAndTracingTechnologyEnabled: Boolean = false

    /** Extended Name Section proposal enabled. */
    public var isExtendedNameSectionEnabled: Boolean = false

    /** Type Imports proposal enabled. */
    public var isTypeImportsEnabled: Boolean = false

    /** Component Model proposal enabled. */
    public var isComponentModelEnabled: Boolean = false

    /** WebAssembly C and C++ API proposal enabled. */
    public var isWebAssemblyCAndCppAPIEnabled: Boolean = false

    /** Flexible Vectors proposal enabled. */
    public var isFlexibleVectorsEnabled: Boolean = false

    /** Call Tags proposal enabled. */
    public var isCallTagsEnabled: Boolean = false

    /** Stack Switching proposal enabled. */
    public var isStackSwitchingEnabled: Boolean = false

    /** Constant Time proposal enabled. */
    public var isConstantTimeEnabled: Boolean = false

    /** JS Customization for GC Objects proposal enabled. */
    public var isJSCustomizationForGCObjectsEnabled: Boolean = false

    /** Memory control proposal enabled. */
    public var isMemoryControlEnabled: Boolean = false

    /** Reference-Typed Strings proposal enabled. */
    public var isReferenceTypedStringsEnabled: Boolean = false

    /** Profiles proposal enabled. */
    public var isProfilesEnabled: Boolean = false

    /** JS String Builtins proposal enabled. */
    public var isJSStringBuiltinsEnabled: Boolean = false

    /** Rounding Variants proposal enabled. */
    public var isRoundingVariantsEnabled: Boolean = false

    /** Shared-Everything Threads proposal enabled. */
    public var isSharedEverythingThreadsEnabled: Boolean = false

    /** Frozen Values proposal enabled. */
    public var isFrozenValuesEnabled: Boolean = false

    /** Compilation Hints proposal enabled. */
    public var isCompilationHintsEnabled: Boolean = false

    /** FC instructions enabled support */
    public var isFCEnabled: Boolean = false

    /** SIMD support. */
    public var isSIMDEnabled: Boolean = false

    /** Import/export mutable globals proposal enabled. */
    public var isMutableGlobals: Boolean = false

    /** Non-trapping float-to-int conversions proposal enabled. */
    public var isNonTrappingFloatToIntEnabled: Boolean = false

    /** Sign-extension operators proposal enabled. */
    public var isSignExtensionEnabled: Boolean = false

    /** Multi value proposal enabled. */
    public var isMultiValueEnabled: Boolean = false

    /** JS BigInt to WASM i64 integration proposal enabled. */
    public var isBigIntEnabled: Boolean = false

    /** Reference types proposal enabled. */
    public var isReferenceTypesEnabled: Boolean = false

    /** Bulk memory operations proposal enabled. */
    public var isBulkMemoryEnabled: Boolean = false

    /** Fixed-width SIMD proposal enabled. */
    public var isFixedSIMDEnabled: Boolean = false

    public fun enableAll() {
        isTailCallsEnabled = true
        isExtendedConstantExpressionsEnabled = true
        isTypedFunctionReferencesEnabled = true
        isGCEnabled = true
        isMultipleMemoryEnabled = true
        isThreadsEnabled = true
        isRelaxedSIMDEnabled = true
        isCustomAnnotationSyntaxEnabled = true
        isMemory64Enabled = true
        isExceptionHandlingEnabled = true
        isWebContentSecurityPolicyEnabled = true
        isBranchHintingEnabled = true
        isJSPromiseIntegrationEnabled = true
        isTypeReflectionEnabled = true
        isECMAScriptModuleIntegrationEnabled = true
        isRelaxedDeadCodeValidationEnabled = true
        isNumericValuesInWATDataSegmentsEnabled = true
        isInstrumentAndTracingTechnologyEnabled = true
        isExtendedNameSectionEnabled = true
        isTypeImportsEnabled = true
        isComponentModelEnabled = true
        isWebAssemblyCAndCppAPIEnabled = true
        isFlexibleVectorsEnabled = true
        isCallTagsEnabled = true
        isStackSwitchingEnabled = true
        isConstantTimeEnabled = true
        isJSCustomizationForGCObjectsEnabled = true
        isMemoryControlEnabled = true
        isReferenceTypedStringsEnabled = true
        isProfilesEnabled = true
        isJSStringBuiltinsEnabled = true
        isRoundingVariantsEnabled = true
        isSharedEverythingThreadsEnabled = true
        isFrozenValuesEnabled = true
        isCompilationHintsEnabled = true
        isFCEnabled = true
        isSIMDEnabled = true
        isMutableGlobals = true
        isNonTrappingFloatToIntEnabled = true
        isSignExtensionEnabled = true
        isMultiValueEnabled = true
        isBigIntEnabled = true
        isReferenceTypesEnabled = true
        isBulkMemoryEnabled = true
        isFixedSIMDEnabled = true
    }
}
