pluginManagement {
	repositories {
		maven ("https://maven.fabricmc.net/")
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.7.10"
}

stonecutter {
	create(rootProject) {
		versions("1.21.9", "1.21.8", "1.21.5", "1.21.4", "1.21.1")
		vcsVersion = "1.21.8"
	}
}