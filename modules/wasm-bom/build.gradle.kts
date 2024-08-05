plugins {
    `java-platform`
    `maven-publish`
}

description = "${rootProject.description} (Bill of Materials)"

val modulesToIncludeInBom = setOf(
    "wasm-binary",
    "wasm-wir",
)

dependencies {
    constraints {
        for (subproject in project.rootProject.subprojects) {
            if (subproject.name in modulesToIncludeInBom) {
                api(subproject)
            }
        }

        val catalog = versionCatalogs.named("libraries")

        for (alias in catalog.libraryAliases) {
            api(catalog.findLibrary(alias).get().get())
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            afterEvaluate { from(components["javaPlatform"]) }
        }
    }
}
