plugins {
    `version-catalog`
    `maven-publish`
}

val modulesToInclude = setOf(
    "wasm-binary",
    "wasm-wir",
)

catalog {
    versionCatalog {
        val catalog = versionCatalogs.named("libraries")
        for (alias in catalog.libraryAliases) {
            library(alias, catalog.findLibrary(alias).get().get().toString())
        }
        for(bundle in catalog.bundleAliases) {
            bundle(bundle, catalog.findBundle(bundle).get().get().map(MinimalExternalModuleDependency::getName).toList())
        }

        val modules = mutableListOf<String>()
        for (subproject in project.rootProject.subprojects) {
            if (subproject.name in modulesToInclude) {
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
