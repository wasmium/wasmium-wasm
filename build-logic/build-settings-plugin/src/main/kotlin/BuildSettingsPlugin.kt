package build.gradle.plugins.build

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion

public class BuildSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings): Unit = with(settings) {
        checkCompatibility()
    }

    private fun checkCompatibility() {
        if (GradleVersion.current().baseVersion < GradleVersion.version("8.0")) {
            throw IllegalStateException("This version of the Wrapper Upgrade Gradle plugin is not compatible with Gradle < 8.0")
        }
    }
}
