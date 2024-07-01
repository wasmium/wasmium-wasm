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

dependencies {
    implementation(buildCatalog.build.gradle.plugin)
}

kotlin {
    explicitApi()
}

gradlePlugin {
    plugins {
        register("ProjectDefaultPlugin") {
            id = "build-project-default"
            implementationClass = "build.gradle.plugins.build.ProjectDefaultPlugin"
        }
    }
}

tasks.withType<ValidatePlugins>().configureEach {
    failOnWarning.set(true)
    enableStricterValidation.set(true)
}
