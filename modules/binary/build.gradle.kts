plugins {
    alias(libraries.plugins.kotlinx.kover)

    `maven-publish`

    id("build-kotlin-multiplatform")
    id("build-project-default")
    id("build-maven-publishing-configurer")
}

description = "Wasmium Binary"

kotlin {
    explicitApi()

    sourceSets {
        configureEach {
            languageSettings.apply {
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.time.ExperimentalTime")
            }
        }

        matching { it.name.endsWith("Test") }.configureEach {
            compilerOptions {
                optIn.add("kotlinx.coroutines.FlowPreview")
            }
        }

        val commonMain by getting {
            kotlin {
                srcDirs("src/commonMain/kotlinX")
            }
            dependencies {
                implementation(libraries.kotlinx.io.core)
                implementation(libraries.kotlinx.coroutines.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libraries.kotlin.test)
            }
        }

        val jvmTest by getting
    }
}
