plugins {
    alias(libraries.plugins.detekt)
    alias(libraries.plugins.kotlinx.bcv)

    id("build-project-default")
    id("build-wrapper-configurer")
    id("build-detekt-configurer")
}

run {
    description = "Root Project"
}

allprojects {
    group = "org.wasmium.wasm"

    configurations.configureEach {
        resolutionStrategy {
            failOnNonReproducibleResolution()
        }
    }
}

apiValidation {
    ignoredPackages.add("org.wasmium.wasm.internal")

    ignoredProjects.addAll(
        listOf(
            "platform",
            "version-catalog",
        )
    )
}

tasks {
    // Fix CodeQL workflow execution
    val testClasses by registering
}
