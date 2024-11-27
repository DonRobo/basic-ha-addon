plugins {
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "2.3.12"
    id("com.google.cloud.tools.jib") version "3.4.3"
}

group = "at.robert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:1.5.8")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("example.com.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

jib {
    from {
        platforms {
            platform {
                os = "linux"
                architecture = "amd64"
            }
            platform {
                os = "linux"
                architecture = "arm64"
            }
            platform {
                os = "linux"
                architecture = "arm"
            }
        }
    }
    to {
        image = "ghcr.io/donrobo/basic-ha-addon"
        tags = setOf("latest", version.toString())
        auth {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GH_USER")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GH_TOKEN")
        }
    }
    container {
        labels.set(
            mapOf(
                "org.opencontainers.image.description" to "Basic Java HA Addon",
                "io.hass.name" to "basic-ha-addon",
                "io.hass.version" to version.toString(),
                "io.hass.type" to "addon",
                "io.hass.arch" to "amd64|armv7|aarch64",
            )
        )
    }
}
