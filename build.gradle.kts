import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    alias(libraries.plugins.detekt)
    alias(libraries.plugins.kotlinx.bcv)

    id("build-project-default")
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

plugins.withType<YarnPlugin> {
    yarn.apply {
        download = false
        ignoreScripts = false
        lockFileDirectory = rootDir.resolve("gradle/js")
        reportNewYarnLock = true
        yarnLockAutoReplace = true
        yarnLockMismatchReport = YarnLockMismatchReport.FAIL

        resolution("braces", "3.0.3")
        resolution("follow-redirects", "1.15.6")
        resolution("body-parser", "1.20.3")
    }
}

tasks {
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
