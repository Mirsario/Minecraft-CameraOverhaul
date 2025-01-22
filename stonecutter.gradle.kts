plugins {
	id("dev.kikugie.stonecutter")
	id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
	id("architectury-plugin") version "3.4-SNAPSHOT" apply false
}

stonecutter active "fabric-1.20.6" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("buildAllVersions", stonecutter.chiseled) {
	group = "_project"
	ofTask("build")
}
