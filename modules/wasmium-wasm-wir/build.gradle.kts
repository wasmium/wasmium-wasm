@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import build.gradle.dsl.withCompilerArguments
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id(libraries.plugins.kotlin.multiplatform.get().pluginId)

    alias(libraries.plugins.kotlin.dokka)
    alias(libraries.plugins.kotlinx.kover)

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
        moduleName = "wasmium-wasm-wir"

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
                implementation(libraries.kotlinx.io.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }

    withSourcesJar()
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
