plugins {
    id("net.fabricmc.fabric-loom") version "1.18.4"
    id("com.modrinth.minotaur") version "2.10.0"
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
    options.encoding = "UTF-8"
}

tasks.processResources {
    val version = project.version
    inputs.property("version", version)
    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}

tasks.jar {
    from("LICENSE")
}

modrinth {
    projectId = "antistutter-loka"
    uploadFile.set(tasks.jar)
    gameVersions.add(property("minecraft_version").toString())
    loaders.add("fabric")
    environment = "client_only"
    syncBodyFrom = file("README.md").readText()
    dependencies {
        required.project("fabric-api")
    }
}
