@file:Suppress("PackageDirectoryMismatch")

package build.gradle.plugin

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

public val KonanTarget.buildHost: KonanTarget
    get() = when (this) {
        KonanTarget.ANDROID_ARM32,
        KonanTarget.ANDROID_ARM64,
        KonanTarget.ANDROID_X64,
        KonanTarget.ANDROID_X86,
        KonanTarget.LINUX_ARM32_HFP,
        KonanTarget.LINUX_ARM64,
        KonanTarget.LINUX_MIPS32,
        KonanTarget.LINUX_MIPSEL32,
        KonanTarget.WASM32,
        is KonanTarget.ZEPHYR,
        KonanTarget.LINUX_X64 -> KonanTarget.LINUX_X64

        KonanTarget.IOS_SIMULATOR_ARM64,
        KonanTarget.TVOS_SIMULATOR_ARM64,
        KonanTarget.WATCHOS_DEVICE_ARM64,
        KonanTarget.WATCHOS_SIMULATOR_ARM64,
        KonanTarget.MACOS_ARM64 -> KonanTarget.MACOS_ARM64

        KonanTarget.WATCHOS_X64,
        KonanTarget.WATCHOS_X86,
        KonanTarget.TVOS_X64,
        KonanTarget.IOS_X64,
        KonanTarget.MACOS_X64 -> KonanTarget.MACOS_X64

        KonanTarget.TVOS_ARM64,
        KonanTarget.WATCHOS_ARM32,
        KonanTarget.WATCHOS_ARM64,
        KonanTarget.IOS_ARM32,
        KonanTarget.IOS_ARM64,
        -> KonanTarget.MACOS_X64

        KonanTarget.MINGW_X64,
        KonanTarget.MINGW_X86 -> KonanTarget.MINGW_X64
    }

public val Project.mainHost: KonanTarget
    get() = when (val os = "${properties["project.mainOS"]}") {
        "linux" -> KonanTarget.LINUX_X64
        "windows" -> KonanTarget.MINGW_X64
        "macosX64" -> KonanTarget.MACOS_X64
        "macosArm64" -> KonanTarget.MACOS_ARM64
        else -> error(
            "mainOS key $os unknown. Supported keys are [linux, windows, macosX64, macosArm64]"
        )
    }

public val KotlinTarget.buildHost : KonanTarget
    get() = if (this is KotlinNativeTarget) konanTarget.buildHost else project.mainHost

public val KotlinTarget.enabled : Boolean
    get() = buildHost == HostManager.host

public fun KonanTarget.supports(hostManager: HostManager): Set<String> {
    return hostManager.enabledByHost[this]?.mapTo(mutableSetOf()) { target -> target.name } ?: emptySet()
}
