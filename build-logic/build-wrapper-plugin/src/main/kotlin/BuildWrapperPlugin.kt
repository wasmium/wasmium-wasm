@file:Suppress("PackageDirectoryMismatch")

package build.gradle.plugins.wrapper

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType
import org.gradle.kotlin.dsl.named
import org.gradle.util.GradleVersion

private const val DEFAULT_GRADLE_WRAPPER_VERSION: String = "8.6"

public class BuildWrapperPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        checkCompatibility()

        tasks.named<Wrapper>("wrapper") {
            gradleVersion = getGradleWrapperVersion()
            distributionType = DistributionType.ALL

            doLast {
                println("Gradle wrapper version: $version")
            }
        }
    }

    private fun Project.getGradleWrapperVersion(): String = findProperty("gradle-wrapper.version")?.toString() ?: DEFAULT_GRADLE_WRAPPER_VERSION

    private fun checkCompatibility() {
        if (GradleVersion.current().baseVersion < GradleVersion.version("8.6")) {
            throw IllegalStateException("This version of the Wrapper Upgrade Gradle plugin is not compatible with Gradle < 8.6")
        }
    }
}
