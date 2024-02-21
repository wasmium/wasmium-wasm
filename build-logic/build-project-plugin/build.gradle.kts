import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

configurations.all {
    resolutionStrategy {
        failOnNonReproducibleResolution()
    }
}

sourceSets {
    main {
        kotlin {
            srcDirs("src/main/kotlinX")
        }
    }
}

kotlin {
    explicitApi()

    jvmToolchain {
        val mainJvmCompiler = providers.gradleProperty("kotlin.javaToolchain.mainJvmCompiler").map(JavaLanguageVersion::of)

        languageVersion = mainJvmCompiler
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${getKotlinPluginVersion()}")
}

gradlePlugin {
    plugins {
        register("BuildProjectPlugin") {
            id = "build-project-plugin"
            implementationClass = "build.gradle.plugins.build.BuildProjectPlugin"
        }
    }
}
