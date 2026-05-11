// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0
// See LICENSE.md for details.

@file:OptIn(StonecutterExperimentalAPI::class)
import org.gradle.internal.declarativedsl.parsing.parse
import dev.kikugie.stonecutter.StonecutterExperimentalAPI
import java.util.*

plugins {
	id("dev.kikugie.stonecutter")
	id("xyz.wagyourtail.jvmdowngrader")
	id("com.gradleup.shadow")
	id("me.modmuss50.mod-publish-plugin")
	// This plugin will choose the necessary loom plugin conditionally.
	// Must exist in both settings.gradle.kts as well as build.gradle.kts.
    id("dev.kikugie.loom-back-compat")
}

// Utilities.
fun Project.required(p: String) = property(p).toString()
fun Project.optional(p: String) = findProperty(p)?.toString()

repositories {
	// NeoForge
	maven("https://maven.neoforged.net/releases")
	// Libraries
	maven("https://maven.shedaniel.me") // ClothConfig
	maven("https://maven.terraformersmc.com") // ModMenu
	maven("https://maven.nucleoid.xyz") // Placeholder API (ModMenu dependency)
}

// Common
val minecraft = stonecutter.current.version
val loader = loom.platform.get().name.lowercase()
val mcType = required("mc.type")
val mcVersion = required("mc.version")
val isFabric = loader == "fabric"
val isForge = loader == "forge"
val isNeoForge = loader == "neoforge"
val isForgeLike = isForge || isNeoForge
val isRemapped = stonecutter.eval(mcVersion, "<26")
// Java
val javaSrcVersion = JavaVersion.VERSION_25
val javaDstVersion = if (stonecutter.eval(mcVersion, ">=26")) JavaVersion.VERSION_25
	else if (stonecutter.eval(mcVersion, ">=1.20.5")) JavaVersion.VERSION_21
	else if (stonecutter.eval(mcVersion, ">=1.18")) JavaVersion.VERSION_17
	else if (stonecutter.eval(mcVersion, ">=1.17")) JavaVersion.VERSION_16
	else JavaVersion.VERSION_1_8
// Build Behavior
val isPrimaryBuild = isFabric && optional("mc.latest") == "true" // Dumb. Better somehow ask Stonecutter if we're in a chiseled context.
val shadowLibs = true; //(javaSrcVersion != javaDstVersion) || (isForge && stonecutter.eval(mcVersion, "<1.19"))
// Versions & Targets
val versionNumbers = required("mod.version")
val targetsLatest = optional("mc.latest") == "true"
val actualTargets = required("mc.targets").trim().split(' ')
val displayTargets = actualTargets; //.map { if (it.count { c -> c == '.' } == 1) "${it}.0" else it }
val multipleVersions = displayTargets.count() > 1 || targetsLatest
val displayedLatest = if (targetsLatest) "plus" else displayTargets.last()
val actualTarget = "${loader}+mc.${displayTargets.first()}${if (multipleVersions) "-${displayedLatest}" else ""}"
val displayTarget = "${loader}+mc[${displayTargets.first()}${if (multipleVersions) "-${displayedLatest}" else ""}]"
val actualVersion = "${versionNumbers}-${actualTarget}"
val displayVersion = "v${versionNumbers}-${displayTarget}"
val targetsRange = ">=${actualTargets.first()}" + (if (targetsLatest) "" else " <=${actualTargets.last()}")
// Changelog
fun parseChangelog(full: String, version: String)
	= Regex("(?:#\\s+${version.replace(".", "\\.")}\\s*)(\\S[\\s\\S]*?)(?:\\s*(?:# |$))", RegexOption.IGNORE_CASE).find(full)?.groupValues?.get(1)
val fullChangelog = rootProject.file("CHANGELOG.md").readText()
val versionChangelog = parseChangelog(fullChangelog, versionNumbers) ?: parseChangelog(fullChangelog, "work in progress") ?: ""

base {
	group = required("maven_group")
	version = displayVersion
	archivesName.set(required("archives_base_name"))
}

// Configure Java & Java Downgrader constants.
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}
java {
	sourceCompatibility = javaSrcVersion
	targetCompatibility = javaSrcVersion
}
jvmdg {
	downgradeTo = javaDstVersion
}
// Prepare Shadow to inline libraries right into our JAR on legacy Forge.
val shade: Configuration by project.configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
	isTransitive = false
}
tasks.shadowJar {
	archiveClassifier.set(if (this != lastTask) "shadow" else null)
	configurations = listOf(shade)
	minimize()
	// Relocate shadowed dependencies to avoid conflicts.
	enableAutoRelocation = true
	relocationPrefix = "${required("root_package")}.shadow"
}
// Downgrade classes for older JVM versions.
tasks.downgradeJar {
	archiveClassifier.set(if (this != lastTask) "downgrade" else null)
	inputFile = tasks.shadowJar.get().archiveFile
	dependsOn(tasks.shadowJar)
}
// Remapping is the last step, if done at all.
val lastTask: Task = (if (isRemapped) tasks.named<Task>("remapJar") else tasks.downgradeJar).get()
if (isRemapped) {
	// Convoluted setters that avoid compilation errors.
	tasks.matching { it.name == "remapJar" }.configureEach {
		(this as AbstractArchiveTask).archiveClassifier.set(if (this != lastTask) "remap" else null)
		(this as Any).setProperty("input", tasks.downgradeJar.get().archiveFile)
		dependsOn(tasks.downgradeJar)
	}
}

// Setup preprocessor.
stonecutter {
	constants["FABRIC"] = isFabric
	constants["FORGE"] = isForge
	constants["NEOFORGE"] = isNeoForge
	constants["FORGE_LIKE"] = isForgeLike

	constants["MC_RELEASE"] = mcType == "release"
	constants["MC_BETA"] = mcType == "beta"
	constants["MC_ALPHA"] = mcType == "alpha"
	constants["false"] = false
}

// To change any versions see the gradle.properties files under root and "/versions/*/"
dependencies {
	minecraft("com.mojang:minecraft:${mcVersion}")
    // Apply Mojang Mappings on obfuscated versions
    loomx.applyMojangMappings()

	// Common libraries
	implementation("io.hotmoka:toml4j:0.7.3") { if (shadowLibs) shade(this) else include(this) }
	if (stonecutter.eval(mcVersion, "<1.19.3")) {
		implementation("org.joml:joml:1.10.5") { if (shadowLibs) shade(this) else include(this) }
	}

	// Cloth Config
	val clothConfigVersion: String = required("mods.clothconfig.ref")
	val clothConfigMajor: Int = if (clothConfigVersion != "[VERSIONED]") clothConfigVersion.split(".")[0].toInt() else 0
	modApi("me.shedaniel.cloth:${if (clothConfigMajor <= 2) "config-2" else "cloth-config-${loader}"}:${clothConfigVersion}") {
		// Prevent preparing two loader versions in cache. Not needed.
		exclude(group = "net.fabricmc")
		exclude(group = "net.fabricmc.fabric-api")
	}

	if (loader == "fabric") {
		modImplementation("net.fabricmc:fabric-loader:${required("deps.fabric_loader")}")

		// ModMenu API
		modImplementation("com.terraformersmc:modmenu:${required("mods.modmenu.ref")}")
	}
	// Note: String invocation means that the function resolution is delayed to the buildscript's runtime.
	if (loader == "forge") {
		"forge"("net.minecraftforge:forge:${required("deps.forge_loader")}")
	}
	if (loader == "neoforge") {
		"neoForge"("net.neoforged:neoforge:${required("deps.neoforge_loader")}")
	}
}

loom {
	//accessWidenerPath = rootProject.file("src/main/resources/${required("mod.id")}.accesswidener")

	decompilers {
		get("vineflower").apply { // Adds names to lambdas - useful for mixins
			options.put("mark-corresponding-synthetics", "1")
		}
	}
	if (loader == "forge") {
		forge.mixinConfigs("${required("mod.id")}.mixins.json")
	}
}

tasks.processResources {
	fun plainList(str: String) = str.lines().joinToString(", ") { it.trim() }
	fun fancyList(str: String) = str.lines().joinToString("\n") { "- ${it.trim()}" }
	fun jsonList(str: String) = str.lines().joinToString(", ") { "\"${it.trim()}\"" }

    var properties = mapOf(
		"mod_id" to required("mod.id"),
		"mod_name" to required("mod.name"),
		"mod_description" to required("mod.description"),
		"mod_description_esc" to required("mod.description").replace("\n", "\\n"),
		"mod_version" to actualVersion,
		"mod_authors" to plainList(required("mod.authors")),
		"mod_authors_list" to fancyList(required("mod.authors")),
		"mod_authors_jarray" to jsonList(required("mod.authors")),
		"mod_contributors" to plainList(required("mod.contributors")),
		"mod_contributors_list" to fancyList(required("mod.contributors")),
		"mod_contributors_jarray" to jsonList(required("mod.contributors")),
		"mod_forgeupdatecheckurl" to required("mod.forgeupdatecheckurl"),
		"mc_version_range" to targetsRange,
		// Contact (Mod)
		"contact_homepage" to required("contact.homepage"),
		"contact_sources" to required("contact.sources"),
		"contact_issues" to required("contact.issues"),
		"contact_modrinth" to optional("contact.modrinth"),
		"contact_curseforge" to optional("contact.curseforge"),
		// Contact (Author)
		"contact_email" to required("contact.email"),
		"contact_discord" to optional("contact.discord"),
		"contact_patreon" to optional("contact.patreon"),
		"contact_youtube" to optional("contact.youtube"),
		// Libraries
		"mods_clothconfig_range" to required("mods.clothconfig.range"),
	)
	if (isFabric) properties = properties.plus(mapOf(
		"mods_modmenu_range" to required("mods.modmenu.range"),
	))

	fun expandLoaderFile(include: Boolean, pattern: String) = filesMatching(pattern) { if (!include) exclude() else expand(properties) }

	expandLoaderFile(isFabric, "fabric.mod.json")
	expandLoaderFile(isForge, "META-INF/mods.toml")
	expandLoaderFile(isNeoForge, "META-INF/neoforge.mods.toml")
	expandLoaderFile(isForgeLike, "pack.mcmeta")

	inputs.properties(properties)
}

// Copy produced jars into /out/
val copyJars = tasks.register<Copy>("copyJars") {
	val dir = "../../out/v${versionNumbers}/"
	project.delete(fileTree(mapOf("dir" to dir, "include" to listOf("${required("archives_base_name")}-*.jar"))))
	from(lastTask)
	into(dir)
}
tasks.getByName("build").finalizedBy(copyJars)
// Also maintain /out/latest/
val copyLatest = tasks.register<Copy>("copyLatest") {
	val dir = "../../out/latest/"
	project.delete(fileTree(mapOf("dir" to dir, "include" to listOf("${required("archives_base_name")}-*.jar"))))
	from(lastTask)
	into(dir)
	rename { "${required("archives_base_name")}-${displayTarget}.jar" }
}
tasks.getByName("build").finalizedBy(copyLatest)

// Publishing
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
	localProperties.load(localPropertiesFile.inputStream())
}
publishMods {
	modLoaders.add(loader)
	if (loader == "fabric") modLoaders.add("quilt")
	if (loader == "forge" && stonecutter.eval(actualTargets.max(), ">=1.20.2")) modLoaders.add("neoforge")

	val isDryRun = optional("publish.enabled")?.trim()?.lowercase() == "false"

	file.set((lastTask as AbstractArchiveTask).archiveFile)

	dryRun = isDryRun
	version = actualVersion
	displayName = displayVersion
	changelog = versionChangelog
	type = when (required("mod.version_type").lowercase()) {
		"alpha" -> ALPHA; "beta" -> BETA; "release" -> STABLE
		else -> throw Exception("Invalid version type")
	}

	val modrinthToken = localProperties.getProperty("publish.modrinth.token", "")
	val curseforgeToken = localProperties.getProperty("publish.curseforge.token", "")
	val githubToken = localProperties.getProperty("publish.github.token", "")
	val discordWebhook = localProperties.getProperty("publish.discord.webhook${if (dryRun.get()) "_dry" else ""}", "")

	fun filterFormat(str: String) = str.replace("{{version}}", versionNumbers).replace("{{changelog}}", versionChangelog)

	if (dryRun.get() || !modrinthToken.isNullOrBlank()) modrinth {
		projectId = required("publish.modrinth.id")
		accessToken = modrinthToken
		actualTargets.forEach(minecraftVersions::add)

		// Relations
		requires("cloth-config")
		if (loader == "fabric") {
			optional("modmenu")
		}
	}
	if (isDryRun || !curseforgeToken.isNullOrBlank()) curseforge {
		projectId = required("publish.curseforge.id")
		accessToken = curseforgeToken
		actualTargets.forEach(minecraftVersions::add)
		changelogType = "markdown"

		// Relations
		requires("cloth-config")
		if (loader == "fabric") {
			optional("modmenu")
		}
	}
	// Only ran once even when chiseled.
	if (isPrimaryBuild) {
		if (isDryRun || !githubToken.isNullOrBlank()) github {
			repository = required("publish.github.repository")
			accessToken = githubToken
			commitish = required("publish.github.branch")
			tagName = versionNumbers
			changelog = filterFormat(required("publish.github.format"))
		}
		if (isDryRun || !discordWebhook.isNullOrBlank()) discord {
			webhookUrl = discordWebhook
			dryRunWebhookUrl = discordWebhook
			username = required("publish.discord.username")
			avatarUrl = required("publish.discord.avatar")
			content = filterFormat(required("publish.discord.format")).replace("\r\n\r\n", "\r\n").replace("\n\n", "\n")
			setPlatforms(*emptyArray<me.modmuss50.mpp.Platform>())
		}
	}
}

// Pull up relevant tasks.
listOf(tasks.build, tasks.clean, tasks.publishMods).forEach {
	it.get().group = "_project"
}
