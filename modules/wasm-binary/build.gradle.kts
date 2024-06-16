@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import build.gradle.dsl.withCompilerArguments

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover")
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
                apiVersion = ApiVersion.KOTLIN_1_6.toString()
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
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.1")
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
