plugins {
    `java-platform`
    `maven-publish`
}

description = "${rootProject.description} (Bill of Materials)"

val modulesToIncludeInBom = setOf(
    "wasm-binary",
    "wasm-wir",
)

val librariesToIncludeInBom = setOf(
    "kotlin-reflect",
    "kotlinx-coroutines-bom",
    "kotlinx-coroutines-core",
    "kotlinx-datetime",
    "kotlinx-io-core",
    "kotlinx-serialization-bom",
    "kotlinx-serialization-core",
    "kotlinx-serialization-json",
).map { it.replace("-", ".") }

dependencies {
    constraints {
        for (subproject in project.rootProject.subprojects) {
            if (subproject.name in modulesToIncludeInBom) {
                api(subproject)
            }
        }

        val catalog = versionCatalogs.named("libraries")
        for (alias in catalog.libraryAliases.filter { it in librariesToIncludeInBom }) {
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
