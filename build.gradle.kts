import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    alias(libraries.plugins.detekt)
    alias(libraries.plugins.kotlin.dokka)
    alias(libraries.plugins.kotlinx.bcv)

    id("build-project-default")
}

description = "Root Project"

allprojects {
    group = "org.wasmium.wasm"
    version = "0.1.0"

    configurations.all {
        resolutionStrategy {
            failOnNonReproducibleResolution()
        }
    }
}

apiValidation {
    ignoredPackages.add("org.wasmium.wasm.internal")

    ignoredProjects.addAll(
        listOf(
            "wasmium-wasm-bom",
            "wasmium-wasm-version-catalog",
        )
    )
}

tasks {
    val dokkaHtmlMultiModule by getting(DokkaMultiModuleTask::class) {
        moduleName.set(rootProject.name)
        moduleVersion.set("${rootProject.version}")
    }

    val detektAll by registering(Detekt::class) {
        description = "Run detekt on whole project"

        buildUponDefaultConfig = true
        parallel = true
        setSource(projectDir)
        config.setFrom(project.file("./config/detekt/detekt.yml"))
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/**", "**/build/**", "**/build.gradle.kts/**", "**/settings.gradle.kts/**")
    }

    named<Wrapper>("wrapper") {
        gradleVersion = libraries.versions.gradle.asProvider().get()
        distributionType = DistributionType.ALL

        doLast {
            println("Gradle wrapper version: $gradleVersion")
        }
    }

    // Fix CodeQL workflow execution
    val testClasses by registering
}
