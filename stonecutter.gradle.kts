plugins {
	id("dev.kikugie.stonecutter")
	id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
	id("architectury-plugin") version "3.4-SNAPSHOT" apply false
	id("com.github.johnrengelman.shadow") version "8.1.1" apply false
	id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
}

stonecutter active "fabric-1.21.2" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("buildAll", stonecutter.chiseled) {
	group = "_project"
	ofTask("build")
}
stonecutter registerChiseled tasks.register("cleanAll", stonecutter.chiseled) {
	group = "_project"
	ofTask("clean")
}
stonecutter registerChiseled tasks.register("publishAll", stonecutter.chiseled) {
	group = "_project"
	ofTask("publishMods")
}
