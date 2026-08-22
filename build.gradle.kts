import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "org.rookieand"
version = "1.0-SNAPSHOT"

val mcApiVersion: String by project
val simpleMcApiVersion: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.0.0"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("io.insert-koin:koin-core:3.5.3")
    implementation("org.mongodb:mongodb-driver-sync:4.11.1") {
        exclude(group = "org.slf4j")
    }

    compileOnly("io.papermc.paper:paper-api:${mcApiVersion}")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(25)
    }

    processResources {
        val placeholders = mapOf(
            "version" to version,
            "apiVersion" to simpleMcApiVersion
        )
        filesMatching("plugin.yml") {
            expand(placeholders)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("kotlin", "shaded.kotlin")
        relocate("org.koin", "shaded.org.koin")
        relocate("org.intellij", "shaded.org.intellij")
        relocate("org.jetbrains", "shaded.org.jetbrains")
        mergeServiceFiles()
    }

    test {
        useJUnitPlatform()
    }

    // ponytail: 배포 경로 하드코딩(개인 서버 전용). 다른 PC에서 쓰면 pluginsDir만 수정
    register<Copy>("deployToRune") {
        val pluginsDir = "C:/Users/Admin/Desktop/Rune/plugins"
        from(shadowJar)
        into(pluginsDir)
    }
}
