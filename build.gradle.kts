import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    alias(catalog.plugins.detekt)
    alias(catalog.plugins.kotlin.dokka)
    alias(catalog.plugins.kotlinx.bcv)

    id("build-project-default")
}

allprojects {
    group = "org.wasmium.wasm.binary"

    configurations.all {
        resolutionStrategy {
            failOnNonReproducibleResolution()
        }
    }
}

plugins.withType<YarnPlugin> {
    yarn.apply {
        lockFileDirectory = rootDir.resolve("gradle/js")
        yarnLockMismatchReport = YarnLockMismatchReport.FAIL
        yarnLockAutoReplace = true
        reportNewYarnLock = true
    }
}

tasks {
    dokkaHtmlMultiModule.configure {
        moduleName.set(rootProject.name)
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
        gradleVersion = catalog.versions.gradle.asProvider().get()
        distributionType = DistributionType.ALL

        doLast {
            println("Gradle wrapper version: $gradleVersion")
        }
    }
}
