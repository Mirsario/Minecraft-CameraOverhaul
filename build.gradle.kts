// Copyright 2020-2025 Mirsario & Contributors.
// Released under the GNU General Public License 3.0
// See LICENSE.md for details.

plugins {
	id("dev.architectury.loom")
	id("architectury-plugin")
}

repositories {
	// Libraries
	maven("https://maven.shedaniel.me") // ClothConfig
	maven("https://maven.terraformersmc.com") // ModMenu
	maven("https://maven.nucleoid.xyz") // Placeholder API (ModMenu depencency)
}

val minecraft = stonecutter.current.version
val loader = loom.platform.get().name.lowercase()
val mcType: String = property("mc.type").toString()
val mcVersion: String = property("mc.version").toString()

base {
	group = property("maven_group")!!
	version = "v${property("mod.version")}-${property("loader.id")}+mc${property("mc.displayed_range")}"
	archivesName.set(property("archives_base_name").toString())
}

// Set architectury platform.
architectury.common("fabric");
//	architectury.common(stonecutter.tree.branches.mapNotNull {
//		if (stonecutter.current.project !in it) null else it.property("loader.id").toString()
//	})

// Configure Java.
java {
	val java = if (stonecutter.eval(mcVersion, ">=1.20.5")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	sourceCompatibility = java
	targetCompatibility = java
}

// Setup preprocessor.
stonecutter {
	const("FABRIC_LOADER", loader == "fabric")
	const("FORGE_LOADER", loader == "forge")
	const("MC_RELEASE", mcType == "release")
	const("MC_BETA", mcType == "beta")
	const("MC_ALPHA", mcType == "alpha")
	const("false", false)
}

// To change any versions see the gradle.properties files under root and "/versions/*/"
dependencies {
	minecraft("com.mojang:minecraft:${mcVersion}")
	mappings(loom.officialMojangMappings())

	// Common libraries
	implementation("io.hotmoka:toml4j:0.7.3") { include(this) }
	if (stonecutter.eval(mcVersion, "<1.19.3")) {
		implementation("org.joml:joml:1.10.5") { include(this) }
	}

	if (loader == "fabric") {
		modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

		// Cloth Config
		val clothConfigVersion: String = property("mods.clothconfig.ref").toString()
		val clothConfigMajor: Int = if (clothConfigVersion != "[VERSIONED]") clothConfigVersion.split(".")[0].toInt() else 0
		modApi("me.shedaniel.cloth:${if (clothConfigMajor <= 2) "config-2" else "cloth-config-fabric"}:${clothConfigVersion}") {
			// Prevent preparing two loader versions in cache. Not needed.
			exclude(group = "net.fabricmc")
			exclude(group = "net.fabricmc.fabric-api")
		}
		// ModMenu API
		modImplementation("com.terraformersmc:modmenu:${property("mods.modmenu.ref")}")
	}
}

loom {
	//accessWidenerPath = rootProject.file("src/main/resources/template.accesswidener")

	decompilers {
		get("vineflower").apply { // Adds names to lambdas - useful for mixins
			options.put("mark-corresponding-synthetics", "1")
		}
	}
	//	if (loader == "forge") {
	//		forge.mixinConfigs(
	//			"template-common.mixins.json",
	//			"template-forge.mixins.json",
	//		)
	//	}
}

tasks.processResources {
	filesMatching("fabric.mod.json") {
		expand(mapOf(
			"mod_id" to project.property("mod.id"),
			"mod_name" to project.property("mod.name"),
			"mod_description" to project.property("mod.description"),
			"mod_version" to project.property("mod.version"),
			"mc_version_range" to project.property("mc.version_range"),
			"mods_clothconfig_range" to project.property("mods.clothconfig.range"),
			"mods_modmenu_range" to project.property("mods.modmenu.range")
		))
	}
}

// Copy produced jars into /out/
val copyJars = tasks.register<Copy>("copyJars") {
	from(tasks.getByName("remapJar"))
	into("../../out/")
}
tasks.getByName("build").finalizedBy(copyJars)

tasks.build {
	group = "hidden"
	description = "Run 'buildAllVersions' instead!"
}