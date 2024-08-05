plugins {
    `version-catalog`
    `maven-publish`
}

val modulesToIncludeInBom = setOf(
    "wasm-binary",
    "wasm-wir",
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

val bundlesToIncludeInBom = setOf(
    "kotlinx-coroutines",
    "kotlinx-coroutines-test",
    "kotlinx-io",
    "kotlinx-serialization",
).map { it.replace("-", ".") }

catalog {
    versionCatalog {
        val catalog = versionCatalogs.named("libraries")

        for (alias in catalog.libraryAliases.filter { it in librariesToIncludeInBom }) {
            library(alias, catalog.findLibrary(alias).get().get().toString())
        }
        for (bundle in catalog.bundleAliases.filter { it in bundlesToIncludeInBom }) {
            bundle(bundle, catalog.findBundle(bundle).get().get().map(MinimalExternalModuleDependency::getName).toList())
        }

        val modules = mutableListOf<String>()
        for (subproject in project.rootProject.subprojects) {
            if (subproject.name in modulesToIncludeInBom) {
                library(subproject.name, "${subproject.group}:${subproject.name}:${subproject.version}")
                modules.add(subproject.name)
            }
        }

        bundle("wasmium-all", modules)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["versionCatalog"])
        }
    }
}
