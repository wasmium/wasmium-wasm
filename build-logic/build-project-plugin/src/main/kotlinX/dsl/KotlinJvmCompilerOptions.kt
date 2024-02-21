@file:Suppress("PackageDirectoryMismatch")

package build.gradle.dsl

import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerToolOptions

public fun KotlinCommonCompilerToolOptions.withCompilerArguments(configure: KotlinCompilerArgumentsBuilder.() -> Unit) {
    val arguments = KotlinCompilerArgumentsBuilder().apply(configure).build()

    freeCompilerArgs.addAll(arguments)
}
