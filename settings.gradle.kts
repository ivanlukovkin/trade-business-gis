dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "GIS-for-trade-business-backend"
include(
    "proto-common",
    "database-service", 
)
