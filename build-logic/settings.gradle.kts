@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal {
            content {
                includeGroupAndSubgroups("com.gradle")
                includeGroupAndSubgroups("org.gradle")
            }
        }
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.PREFER_SETTINGS

    repositories {
        gradlePluginPortal {
            content {
                includeGroupAndSubgroups("com.gradle")
                includeGroupAndSubgroups("org.gradle")
            }
        }
        mavenCentral()
    }

    versionCatalogs {
        create("buildLibraries") {
            from(layout.rootDirectory.files("../gradle/catalogs/libraries.build.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
