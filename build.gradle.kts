@file:Suppress("UnstableApiUsage")

plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.9.22"
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
}

val mod = ModData(project)
val loader = LoaderData(project, loom.platform.get().name.lowercase())
val minecraftVersion = MinecraftVersionData(stonecutter)
val awName = "$minecraftVersion.accesswidener"

version = "${mod.version}-$loader+$minecraftVersion"
group = mod.group
base.archivesName.set(mod.name)

repositories {
    mavenLocal()
    mavenCentral()
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") }
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://cursemaven.com")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.ladysnake.org/releases")
    maven("https://maven.enjarai.dev/releases")
    maven("https://maven.shedaniel.me")
    maven("https://maven.blamejared.com/")
    maven("https://jitpack.io")
    maven("https://maven.bawnorton.com/releases")
    maven("https://maven.fallenbreath.me/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    modImplementation("com.bawnorton.runtimetrims:runtimetrims-$loader:${property("runtimetrims")}+$minecraftVersion") {
        exclude("com.bawnorton.allthetrims")
    }
    modImplementation("com.bawnorton.allthetrims:allthetrims-$loader:${property("allthetrims")}+$minecraftVersion") { isTransitive = false }

    modImplementation("maven.modrinth:iris:${property("iris")}")
    modImplementation("maven.modrinth:sodium:${property("sodium")}")
    modRuntimeOnly("org.antlr:antlr4-runtime:4.13.1")
    modRuntimeOnly("io.github.douira:glsl-transformer:2.0.1")
    modRuntimeOnly("org.anarres:jcpp:1.4.14")
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$awName"))

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }

    runConfigs["client"].apply {
        programArgs("--username=Bawnorton", "--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
    }

    runs {
        afterEvaluate {
            val mixinJarFile = configurations.runtimeClasspath.get().incoming.artifactView {
                componentFilter {
                    it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
                }
            }.files.first()

            configureEach {
                vmArg("-javaagent:$mixinJarFile")

                property("mixin.hotSwap", "true")
                property("mixin.debug.export", "true")
            }
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.release = 21
    }

    processResources {
        val modMetadata = mapOf(
            "description" to mod.description,
            "version" to mod.version,
            "minecraft_dependency" to mod.minecraftDependency,
            "minecraft_version" to minecraftVersion.toString(),
            "loader_version" to loader.getVersion()
        )

        inputs.properties(modMetadata)
        filesMatching("fabric.mod.json") { expand(modMetadata) }
        filesMatching("META-INF/neoforge.mods.toml") { expand(modMetadata) }
    }

    withType<AbstractCopyTask> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    clean {
        delete(file(rootProject.file("build")))
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
    targetCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

loader.fabric {
    dependencies {
        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
        modImplementation("net.fabricmc:fabric-loader:${loader.getVersion()}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api")}+$minecraftVersion")

        modImplementation("com.terraformersmc:modmenu:${property("mod_menu")}")

        modRuntimeOnly("maven.modrinth:elytra-trims:${property("elytra_trims")}")
        modRuntimeOnly("net.fabricmc:fabric-language-kotlin:1.12.3+kotlin.2.0.21")
        modRuntimeOnly("me.fallenbreath:conditional-mixin-fabric:0.6.3")

        modRuntimeOnly("maven.modrinth:show-me-your-skin:${property("show_me_your_skin")}")
        modRuntimeOnly("maven.modrinth:cicada:${property("cicada")}")
        modRuntimeOnly("org.ladysnake.cardinal-components-api:cardinal-components-base:${property("cca")}")
        modRuntimeOnly("org.ladysnake.cardinal-components-api:cardinal-components-entity:${property("cca")}")
    }
}

loader.neoforge {
    dependencies {
        mappings(loom.layered {
            mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
            mappings("dev.architectury:yarn-mappings-patch-neoforge:1.21+build.4")
        })
        neoForge("net.neoforged:neoforge:${loader.getVersion()}")
    }

    tasks {
        remapJar {
            atAccessWideners.add("$minecraftVersion.accesswidener")
        }
    }
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "bawnorton"
            url = uri("https://maven.bawnorton.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "${mod.group}.${mod.id}"
            artifactId = "${mod.id}-$loader"
            version = "${mod.version}+$minecraftVersion"

            from(components["java"])
        }
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    val tag = "$loader-${mod.version}+$minecraftVersion"
    val branch = "main"
    changelog = "[Changelog](https://github.com/Bawnorton/${mod.name}/blob/$branch/CHANGELOG.md)"
    displayName = "${mod.name} ${loader.toString().replaceFirstChar { it.uppercase() }} ${mod.version} for $minecraftVersion"
    type = STABLE
    modLoaders.add(loader.toString())

    github {
        accessToken = providers.gradleProperty("GITHUB_TOKEN")
        repository = "Bawnorton/${mod.name}"
        commitish = branch
        tagName = tag
    }

    modrinth {
        accessToken = providers.gradleProperty("MODRINTH_TOKEN")
        projectId = mod.modrinthProjId
        minecraftVersions.addAll(mod.supportedVersions)

        requires {
            slug = "runtimetrims"
        }
    }

    curseforge {
        accessToken = providers.gradleProperty("CURSEFORGE_TOKEN")
        projectId = mod.curseforgeProjId
        minecraftVersions.addAll(mod.supportedVersions)

        requires {
            slug = "runtimetrims"
        }
    }
}