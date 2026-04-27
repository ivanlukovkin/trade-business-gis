dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "trade-business-gis"

include(
    "proto-common",
    "users-service",
    "library",
    "api-gateway"
)
