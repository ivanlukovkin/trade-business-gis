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
    "users-service",
    "library",
    "api-gateway"
)
