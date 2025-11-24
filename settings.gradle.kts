pluginManagement {
	repositories {
		// Shared
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
		maven("https://maven.cassian.cc") // Temporary?
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
	id("dev.kikugie.stonecutter") version "0.7.7"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	shared {
		// Declared in preferred publishing order.

		// 1.21
		vers("fabric-1.21.9", "1.21.9")
		vers("neoforge-1.21.9", "1.21.9")
		vers("fabric-1.21.5", "1.21.5")
		vers("neoforge-1.21.5", "1.21.5")
		vers("fabric-1.21.2", "1.21.2")
		vers("neoforge-1.21.2", "1.21.2")
		vers("fabric-1.21.0", "1.21")
		vers("neoforge-1.21.0", "1.21")
		// 1.20
		vers("fabric-1.20.6", "1.20.6")
		vers("neoforge-1.20.6", "1.20.6")
		vers("fabric-1.20.0", "1.20")
		vers("forge-1.20.0", "1.20")
		// 1.19
		vers("fabric-1.19.3", "1.19.3")
		vers("forge-1.19.3", "1.19.3")
		vers("fabric-1.19.0", "1.19")
		vers("forge-1.19.0", "1.19")
		// 1.18
		vers("fabric-1.18.0", "1.18")
		vers("forge-1.18.0", "1.18")
		// 1.17
		vers("fabric-1.17.0", "1.17")
		vers("forge-1.17.1", "1.17")
		// 1.16
		vers("fabric-1.16.0", "1.16")
		// 1.15
		vers("fabric-1.15.0", "1.15")
		// 1.14
		vers("fabric-1.14.4", "1.14.4")

		vcsVersion = "fabric-1.21.5"
	}

	create(rootProject)
}
