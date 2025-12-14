import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
    dependencies {
        classpath("dev.slne.surf:surf-api-gradle-plugin:1.21.10+")
    }
}

allprojects {
    group = "dev.slne.surf.chat"
    version = findProperty("version") as String

    tasks {
        withType<ShadowJar> {
            exclude("kotlin/**/*.class")
            exclude("kotlin/**/*.kotlin_builtins")
        }
    }

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group.startsWith("com.fasterxml.jackson")) {
                useVersion("2.18.2")
                because("OpenAI client requires Jackson 2.18+ for OptBoolean support")
            }
        }
    }
}