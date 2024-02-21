import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

kotlin {
    explicitApi()

    jvmToolchain {
        val mainJvmCompiler = providers.gradleProperty("kotlin.javaToolchain.mainJvmCompiler").map(JavaLanguageVersion::of)

        languageVersion = mainJvmCompiler
    }
}

gradlePlugin {
    plugins {
        register("BuildPublicationPlugin") {
            id = "build-publication-plugin"
            implementationClass = "build.gradle.plugins.publication.BuildPublicationPlugin"
        }
    }
}
