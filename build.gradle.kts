plugins {
	id("com.github.johnrengelman.shadow") version "7.1.2"
	id("org.jetbrains.kotlin.jvm") version "1.6.21"
	id("xyz.jpenilla.run-paper") version "1.0.6"
}

version = "0.1.0-SNAPSHOT"

repositories {
	mavenCentral()

	maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
	// Provided by Server
	compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

	// Loaded by Server
	compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
}

tasks {
	shadowJar {
		archiveFileName.set("../../build/${project.name}-${project.version}.jar")

		minimize()
	}

	runServer {
		minecraftVersion("1.18.2")
	}

	build {
		dependsOn(":shadowJar")
	}
}