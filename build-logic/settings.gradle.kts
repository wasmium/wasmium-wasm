@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

gradle.beforeSettings {
    rootProject.name = "build-logic"
}

include("build-project-plugin")
include("build-settings-plugin")
include("build-publication-plugin")
include("build-wrapper-plugin")
