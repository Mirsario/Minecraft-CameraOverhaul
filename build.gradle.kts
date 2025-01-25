// Copyright 2020-2025 Mirsario & Contributors.
// Released under the GNU General Public License 3.0
// See LICENSE.md for details.

plugins {
	id("dev.architectury.loom")
	id("architectury-plugin")
}

repositories {
	// NeoForge
	maven("https://maven.neoforged.net/releases")
	// Libraries
	maven("https://maven.shedaniel.me") // ClothConfig
	maven("https://maven.terraformersmc.com") // ModMenu
	maven("https://maven.nucleoid.xyz") // Placeholder API (ModMenu dependency)
}

// Set architectury platforms.
architectury.common(stonecutter.tree.branches.mapNotNull {
	if (stonecutter.current.project in it) it.project.findProperty("loom.platform")?.toString() else null
})

val minecraft = stonecutter.current.version
//val loader = loom.platform.get().name.lowercase()
val loader = property("loader.id").toString()
val mcType = property("mc.type").toString()
val mcVersion = property("mc.version").toString()
val isFabric = loader == "fabric"
val isForge = loader == "forge"
val isNeoForge = loader == "neoforge"
val isForgeLike = isForge || isNeoForge

base {
	group = property("maven_group")!!
	version = "v${property("mod.version")}-${property("loader.id")}+mc${property("mc.displayed_range")}"
	archivesName.set(property("archives_base_name").toString())
}

// Configure Java.
java {
	val java = if (stonecutter.eval(mcVersion, ">=1.20.5")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	sourceCompatibility = java
	targetCompatibility = java
}

// Setup preprocessor.
stonecutter {
	const("FABRIC", isFabric)
	const("FORGE", isForge)
	const("NEOFORGE", isNeoForge)
	const("FORGE_LIKE", isForgeLike)

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

	// Cloth Config
	val clothConfigVersion: String = property("mods.clothconfig.ref").toString()
	val clothConfigMajor: Int = if (clothConfigVersion != "[VERSIONED]") clothConfigVersion.split(".")[0].toInt() else 0
	modApi("me.shedaniel.cloth:${if (clothConfigMajor <= 2) "config-2" else "cloth-config-${loader}"}:${clothConfigVersion}") {
		// Prevent preparing two loader versions in cache. Not needed.
		exclude(group = "net.fabricmc")
		exclude(group = "net.fabricmc.fabric-api")
	}

	if (loader == "fabric") {
		modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

		// ModMenu API
		modImplementation("com.terraformersmc:modmenu:${property("mods.modmenu.ref")}")
	}
	// Note: String invocation means that the function resolution is delayed to the buildscript's runtime.
	if (loader == "forge") {
		"forge"("net.minecraftforge:forge:${property("deps.forge_loader")}")
	}
	if (loader == "neoforge") {
		"neoForge"("net.neoforged:neoforge:${property("deps.neoforge_loader")}")
	}
}

loom {
	//accessWidenerPath = rootProject.file("src/main/resources/${project.property("mod.id")}.accesswidener")

	decompilers {
		get("vineflower").apply { // Adds names to lambdas - useful for mixins
			options.put("mark-corresponding-synthetics", "1")
		}
	}
	if (loader == "forge") {
		forge.mixinConfigs("${project.property("mod.id")}.mixins.json")
	}
}

tasks.processResources {
	fun expandLoaderFile(include: Boolean, pattern: String, properties: () -> Map<String, Any?>) = filesMatching(pattern) {
		if (!include) exclude() else expand(properties().plus(mapOf(
			"mod_id" to project.property("mod.id"),
			"mod_name" to project.property("mod.name"),
			"mod_description" to project.property("mod.description"),
			"mod_version" to project.property("mod.version"),
			"mod_author" to project.property("mod.author"),
			"mc_version_range" to project.property("mc.version_range"),
			"contact_homepage" to project.property("contact.homepage"),
			"contact_sources" to project.property("contact.sources"),
			"contact_issues" to project.property("contact.issues"),
			"contact_email" to project.property("contact.email"),
			"mods_clothconfig_range" to project.property("mods.clothconfig.range"),
		)))
	}

	expandLoaderFile(isFabric, "fabric.mod.json", { mapOf(
		"mods_modmenu_range" to project.property("mods.modmenu.range"),
	)})
	expandLoaderFile(isForge, "META-INF/mods.toml", { mapOf() })
	expandLoaderFile(isNeoForge, "META-INF/neoforge.mods.toml", { mapOf() })
	expandLoaderFile(isForgeLike, "pack.mcmeta", { mapOf() })
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