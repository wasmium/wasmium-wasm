@file:Suppress("UnstableApiUsage")

import build.gradle.api.includeModule

pluginManagement {
    includeBuild("build-settings-logic")
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("buildCatalog") {
            from(files("./gradle/catalogs/buildCatalog.versions.toml"))
        }
    }
}

plugins {
    id("build-settings-default")
}

rootProject.name = "wasmium-wasm-binary"

includeModule("wasm-binary")
