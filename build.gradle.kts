plugins {
	id("com.github.johnrengelman.shadow") version "7.1.2"
	id("org.jetbrains.kotlin.jvm") version "1.7.10"
	id("xyz.jpenilla.run-paper") version "1.0.6"
}

version = "0.1.0-SNAPSHOT"

repositories {
	mavenCentral()

	maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
	// Provided by Server
	compileOnly("io.papermc.paper:paper-api:1.19.1-R0.1-SNAPSHOT")

	// Provided by LiveMap
	implementation("org.bstats:bstats-bukkit:3.0.0")
}

tasks {
	shadowJar {
		relocate("org.bstats", "io.github.petercrawley.livemap.libraries.org.bstats")

		archiveFileName.set("../../build/${project.name}-${project.version}.jar")

		minimize()
	}

	runServer {
		minecraftVersion("1.19.1")
	}

	build {
		dependsOn(":shadowJar")
	}
}