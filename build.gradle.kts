import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")

    id("build-wrapper-plugin")
}

allprojects {
    group = "org.wasmium.wasm.binary"

    configurations.all {
        resolutionStrategy {
            failOnNonReproducibleResolution()
        }
    }
}

subprojects {
    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            documentedVisibilities.set(Visibility.values().toSet())
        }
        failOnWarning.set(true)
        offlineMode.set(true)
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
}

extensions.findByType<YarnRootExtension>()?.run {
    yarnLockMismatchReport = YarnLockMismatchReport.WARNING
    reportNewYarnLock = true
    yarnLockAutoReplace = false
}
