pluginManagement {
	repositories {
		// Shared
		mavenCentral()
		gradlePluginPortal()
		// Stonecutter
		maven("https://maven.kikugie.dev/releases")
		maven("https://maven.kikugie.dev/snapshots")
		// Architectury
		maven("https://maven.architectury.dev")
		// Fabric
		maven("https://maven.fabricmc.net")
		// Forge
		maven("https://maven.minecraftforge.net")
		// NeoForge
		maven("https://maven.neoforged.net/releases")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.6-alpha.5" //"0.5.1" //"0.5-beta.2"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	shared {
		vers("fabric-1.14.4", "1.14.4")
		vers("fabric-1.15", "1.15")
		vers("fabric-1.17", "1.17")
		vers("fabric-1.19", "1.19")
		vers("fabric-1.19.3", "1.19.3")
		vers("fabric-1.20.6", "1.20.6")
		//vers("forge-1.20.1", "1.20.1")
		//vers("neoforge-1.20.6", "1.20.6")

		vcsVersion = "fabric-1.20.6"
	}

	create(rootProject)
}