plugins {
    alias(libraries.plugins.kotlinx.kover)

    id("build-project-default")
    id("build-multiplatform")
    id("build-publishing")
}

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
            languageSettings.apply {
                optIn("kotlinx.coroutines.FlowPreview")
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
                implementation(libraries.kotlin.test)
            }
        }
    }
}
