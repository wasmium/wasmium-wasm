@file:Suppress("PackageDirectoryMismatch")

package build.gradle.plugin

import org.gradle.api.artifacts.Dependency
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

private fun notation(group: String, name: String, version: String? = null) =
    "$group:$name${version?.let { ":$version" } ?: ""}"

public fun KotlinDependencyHandler.api(group: String, name: String, version: String? = null): Dependency? =
    api(notation(group, name, version))

public fun KotlinDependencyHandler.compileOnly(group: String, name: String, version: String? = null): Dependency? =
    compileOnly(notation(group, name, version))

public fun KotlinDependencyHandler.implementation(group: String, name: String, version: String? = null): Dependency? =
    implementation(notation(group, name, version))

public fun KotlinDependencyHandler.runtimeOnly(group: String, name: String, version: String? = null): Dependency? =
    runtimeOnly(notation(group, name, version))
