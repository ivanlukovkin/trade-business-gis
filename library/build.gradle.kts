plugins {
    id("java-library")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("jakarta.persistence:jakarta.persistence-api:4.0.0-M2")
    implementation("com.google.protobuf:protobuf-java-util:4.34.1")
    implementation("com.google.protobuf:protobuf-java:4.34.1")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}
