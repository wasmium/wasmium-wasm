@file:Suppress("UnstableApiUsage")

import build.gradle.api.includeModule

pluginManagement {
    includeBuild("build-settings-logic")
    includeBuild("build-logic")
}

plugins {
    id("build-settings-default")
}

rootProject.name = "wasmium-wasm"

includeModule("wasm-binary")
includeModule("wasm-wir")
