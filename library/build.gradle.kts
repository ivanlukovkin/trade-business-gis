plugins {
    id("java-library")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("jakarta.persistence:jakarta.persistence-api:4.0.0-M2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}
