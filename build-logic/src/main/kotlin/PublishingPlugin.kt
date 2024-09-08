package build.gradle.plugins.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import java.util.*

public class PublishingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.apply<MavenPublishPlugin>()

        // publications are not automatically configured with Kotlin JVM plugin, but they are with KMP.
        project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            // sourcesJar is not added by default by the Kotlin JVM plugin
            project.configure<JavaPluginExtension> {
                withSourcesJar()
            }

            project.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("maven") {
                        // java components have the sourcesJar and the Kotlin artifacts
                        from(project.components["java"])
                    }
                }
            }
        }

        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            build.gradle.plugins.build.fixOverlappingOutputsForSigningTask(project)
        }

        project.pluginManager.withPlugin("org.jetbrains.dokka") {
            build.gradle.plugins.build.configureDokka(project)
        }

        project.configure<PublishingExtension> {
            // configureEach reacts on new publications being registered and configures them too
            publications.configureEach {
                if (this is MavenPublication) {
                    val base = "github.com/wasmium/wasmium-wasm"

                    pom {
                        // using providers because the name and description can be set after application of the plugin
                        name.set(project.provider { project.name })
                        description.set(project.provider { project.description })
                        // to be replaced by lazy property
                        version = project.version.toString()
                        url.set("https://$base")
                        inceptionYear.set("2024")

                        organization {
                            name = "wasmium"
                            url = "https://github.com/wasmium"
                        }

                        developers {
                            developer {
                                name = "The Wasmium Team"
                            }
                        }

                        issueManagement {
                            system = "GitHub"
                            url = "https://$base/issues"
                        }

                        licenses {
                            license {
                                name.set("Apache License 2.0")
                                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }

                        scm {
                            url.set("https://$base")
                            connection.set("scm:git:git://$base.git")
                            developerConnection.set("scm:git:ssh://git@$base.git")
                        }
                    }
                }
            }

            repositories {
                maven {
                    name = "GitHubPackages"
                    url = project.uri("https://maven.pkg.github.com/wasmium/wasmium-wasm")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR")
                        password = System.getenv("GITHUB_TOKEN")
                    }
                }
            }
        }

        project.apply<SigningPlugin>()
        project.configure<SigningExtension> {
            val signingKeyId = System.getenv("SIGNING_KEY_ID")
            val signingSecretKey = System.getenv("SIGNING_KEY")?.let { String(Base64.getDecoder().decode(it)) }
            val signingPassword = System.getenv("SIGNING_PASSWORD") ?: ""

            if (signingKeyId != null) {
                useInMemoryPgpKeys(signingKeyId, signingSecretKey, signingPassword)

                val publishing: PublishingExtension by project
                sign(publishing.publications)
            }
        }
    }
}

// Resolves issues with .asc task output of the sign task of native targets.
// See: https://github.com/gradle/gradle/issues/26132
// And: https://youtrack.jetbrains.com/issue/KT-46466
private fun fixOverlappingOutputsForSigningTask(project: Project) {
    project.tasks.withType<Sign>().configureEach {
        val pubName = name.removePrefix("sign").removeSuffix("Publication")

        // These tasks only exist for native targets, hence findByName() to avoid trying to find them for other targets

        // Task ':linkDebugTest<platform>' uses this output of task ':sign<platform>Publication' without declaring an explicit or implicit dependency
        project.tasks.findByName("linkDebugTest$pubName")?.let {
            mustRunAfter(it)
        }
        // Task ':compileTestKotlin<platform>' uses this output of task ':sign<platform>Publication' without declaring an explicit or implicit dependency
        project.tasks.findByName("compileTestKotlin$pubName")?.let {
            mustRunAfter(it)
        }
    }
}

private fun configureDokka(project: Project) {
    project.configure<PublishingExtension> {
        // configureEach reacts on new publications being registered and configures them too
        publications.configureEach {
            if (this is MavenPublication) {
                val publication = this
                val dokkaJar = project.tasks.register("${publication.name}DokkaJar", Jar::class) {
                    group = JavaBasePlugin.DOCUMENTATION_GROUP
                    description = "Assembles Kotlin docs with Dokka into a Javadoc jar"
                    archiveClassifier.set("javadoc")
                    from(project.tasks.named("dokkaHtml"))

                    // each archive name should be distinct, to avoid implicit dependency issues.
                    // we use the same format as the sources Jar tasks.
                    // https://youtrack.jetbrains.com/issue/KT-46466
                    archiveBaseName.set("${archiveBaseName.get()}-${publication.name}")
                }
                artifact(dokkaJar)
            }
        }
    }
}
