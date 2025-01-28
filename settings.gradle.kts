pluginManagement {
    includeBuild("build-settings-logic")
    includeBuild("build-logic")
}

plugins {
    id("build-settings-default")
    id("build-foojay")
}

rootProject.name = "wasmium-wasm"

include("modules:binary")
include("modules:wir")

include("publishing:bom")
include("publishing:version-catalog")
