import build.gradle.api.includeModule

pluginManagement {
    includeBuild("build-settings-logic")
    includeBuild("build-logic")
}

plugins {
    id("build-settings-default")
}

rootProject.name = "wasmium-wasm"

includeModule("binary")
includeModule("wir")
includeModule("bom")
includeModule("version-catalog")
