@file:Suppress("UnstableApiUsage")

import build.gradle.api.includeModule

pluginManagement {
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.PREFER_PROJECT

    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("multiplatform") version "1.9.23" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.4" apply false
    id("org.jetbrains.dokka") version "1.9.10" apply false
    id("org.jetbrains.kotlinx.kover") version "0.7.5" apply false
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.13.2" apply false
    id("build-settings-plugin")
}

gradle.beforeSettings {
    rootProject.name = "wasmium-wasm-binary"
}

includeModule("wasm-binary")
