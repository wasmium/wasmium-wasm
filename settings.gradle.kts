import build.gradle.api.includeModule

pluginManagement {
    includeBuild("build-settings-logic")
    includeBuild("build-logic")
}

plugins {
    id("build-settings-default")
}

rootProject.name = "wasmium-wasm"

includeModule("wasmium-wasm-binary")
includeModule("wasmium-wasm-wir")
includeModule("wasmium-wasm-bom")
includeModule("wasmium-wasm-version-catalog")
