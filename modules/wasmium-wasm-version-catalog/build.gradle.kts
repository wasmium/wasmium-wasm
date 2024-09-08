import org.gradle.api.problems.internal.GradleCoreProblemGroup.versionCatalog
import javax.xml.catalog.CatalogManager.catalog

plugins {
    id("version-catalog")

    id("build-publishing")
}

description = "Version Catalog"

catalog {
    versionCatalog {
        version("kotlin", libraries.versions.kotlin.get())

        rootProject.subprojects.forEach { subproject ->
            if (subproject.plugins.hasPlugin("maven-publish") && !subproject.name.endsWith("version-catalog")) {
                subproject.publishing.publications.withType<MavenPublication>().configureEach {
                    library(artifactId, "$groupId:$artifactId:$version")
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("versionCatalog") {
            from(components["versionCatalog"])
        }
    }
}
