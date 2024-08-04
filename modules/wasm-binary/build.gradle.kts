@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import build.gradle.dsl.withCompilerArguments
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id(catalog.plugins.kotlin.multiplatform.get().pluginId)

    alias(catalog.plugins.kotlin.dokka)
    alias(catalog.plugins.kotlinx.kover)

    id("build-project-default")
}

kotlin {
    explicitApi()

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    withCompilerArguments {
                        requiresOptIn()
                        suppressExpectActualClasses()
                        suppressVersionWarnings()
                    }
                }
            }
        }
    }

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    withCompilerArguments {
                        requiresJsr305()
                    }
                }
            }
        }
    }

    wasmJs {
        moduleName = "wasmium-wasm"

        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useConfigDirectory(project.projectDir.resolve("karma.config.d").resolve("wasm"))
                }
            }
        }
    }

    wasmWasi {
        nodejs()
    }

    js {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    sourceMap = true
                }
            }
        }

        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useConfigDirectory(project.projectDir.resolve("karma.config.d").resolve("js"))
                }
            }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                apiVersion = ApiVersion.KOTLIN_2_0.toString()
                languageVersion = LanguageVersion.KOTLIN_2_0.toString()
                progressiveMode = true

                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            kotlin {
                srcDirs("src/commonMain/kotlinX")
            }
            dependencies {
                implementation(catalog.bundles.kotlinx.io)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

tasks {
    withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            documentedVisibilities.set(Visibility.values().toSet())
        }
        failOnWarning.set(true)
        offlineMode.set(true)
    }
}
