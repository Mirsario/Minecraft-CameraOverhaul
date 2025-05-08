plugins {
	id("dev.kikugie.stonecutter")
	id("dev.architectury.loom") version "1.10.9999" apply false
	id("xyz.wagyourtail.jvmdowngrader") version "1.2.2" apply false
	id("architectury-plugin") version "3.4-SNAPSHOT" apply false
	id("com.gradleup.shadow") version "9.0.0-beta1" apply false
	id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
}

stonecutter active "fabric-1.21.5" /* [SC] DO NOT EDIT */

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
