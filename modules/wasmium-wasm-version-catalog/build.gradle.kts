plugins {
    `version-catalog`
    `maven-publish`
}

val modulesToIncludeInBom = setOf(
    "wasmium-wasm-binary",
    "wasmium-wasm-wir",
)

val librariesToIncludeInBom = setOf(
    "kotlin-reflect",
    "kotlin-test",
    "kotlin-test-junit5",
    "kotlinx-coroutines-bom",
    "kotlinx-coroutines-core",
    "kotlinx-coroutines-test",
    "kotlinx-datetime",
    "kotlinx-io-core",
    "kotlinx-serialization-bom",
    "kotlinx-serialization-core",
    "kotlinx-serialization-json",
).map { it.replace("-", ".") }

catalog {
    versionCatalog {
        version("kotlin", libraries.versions.kotlin.asProvider().get())

        val catalog = versionCatalogs.named("libraries")
        for (alias in catalog.libraryAliases.filter { it in librariesToIncludeInBom }) {
            library(alias, catalog.findLibrary(alias).get().get().toString())
        }

        val modules = mutableListOf<String>()
        for (subproject in project.rootProject.subprojects) {
            if (subproject.name in modulesToIncludeInBom) {
                library(subproject.name, "${subproject.group}:${subproject.name}:${subproject.version}")
                modules.add(subproject.name)
            }
        }

        bundle("wasmium-wasm-all", modules)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["versionCatalog"])

            pom {
                name.set(artifactId)
                description.set(project.description)
                url.set("https://github.com/wasmium/wasmium-wasm")
                inceptionYear.set("2024")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                scm {
                    val base = "github.com/wasmium/wasmium-wasm"

                    url.set("https://$base")
                    connection.set("scm:git:git://$base.git")
                    developerConnection.set("scm:git:ssh://git@$base.git")
                }
            }
        }
    }
}
