pluginManagement {
	repositories {
		// Shared
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		// Stonecutter
		maven("https://maven.kikugie.dev/releases")
		maven("https://maven.kikugie.dev/snapshots")
		// JvmDowngrader
		maven("https://maven.wagyourtail.xyz/releases")
		maven("https://maven.wagyourtail.xyz/snapshots")
		// Architectury
		maven("https://maven.architectury.dev")
		// ClothConfig
		maven("https://maven.shedaniel.me")
		// Fabric
		maven("https://maven.fabricmc.net")
		// Forge
		maven("https://maven.minecraftforge.net")
		// NeoForge
		maven("https://maven.neoforged.net/releases")
		maven("https://maven.neoforged.net/snapshots")
	}
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.3"
	// This plugin will choose the necessary loom plugin conditionally.
	// Must exist in both settings.gradle.kts as well as build.gradle.kts.
    id("dev.kikugie.loom-back-compat") version "0.3"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	shared {
		// Declared in preferred publishing order.

		// 1.21
		version("fabric-26.1.0", "26.1")
		version("neoforge-26.1.0", "26.1")
		version("fabric-1.21.11", "1.21.11")
		version("neoforge-1.21.11", "1.21.11")
		version("fabric-1.21.9", "1.21.9")
		version("neoforge-1.21.9", "1.21.9")
		version("fabric-1.21.5", "1.21.5")
		version("neoforge-1.21.5", "1.21.5")
		version("fabric-1.21.2", "1.21.2")
		version("neoforge-1.21.2", "1.21.2")
		version("fabric-1.21.0", "1.21")
		version("neoforge-1.21.0", "1.21")
		// 1.20
		version("fabric-1.20.6", "1.20.6")
		version("neoforge-1.20.6", "1.20.6")
		version("fabric-1.20.0", "1.20")
		version("forge-1.20.0", "1.20")
		// 1.19
		version("fabric-1.19.3", "1.19.3")
		version("forge-1.19.3", "1.19.3")
		version("fabric-1.19.0", "1.19")
		version("forge-1.19.0", "1.19")
		// 1.18
		version("fabric-1.18.0", "1.18")
		version("forge-1.18.0", "1.18")
		// 1.17
		version("fabric-1.17.0", "1.17")
		// version("forge-1.17.1", "1.17")
		// 1.16
		version("fabric-1.16.0", "1.16")
		// 1.15
		version("fabric-1.15.0", "1.15")
		// 1.14
		version("fabric-1.14.4", "1.14.4")

		vcsVersion = "fabric-26.1.0"
	}

	create(rootProject)
}
