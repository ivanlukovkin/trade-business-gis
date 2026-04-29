plugins {
    id("application")
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.google.protobuf") version "0.9.5"
}

group = "org.lkvkn.gistrade.users"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":library"))
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.grpcProtobuf)
    implementation(libs.grpcStub)
    implementation(libs.protobufJavaUtil)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("io.github.lognet:grpc-spring-boot-starter:5.2.0")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.8"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.80.0"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
            srcDir("build/generated/sources/proto/main/java")
            srcDir("build/generated/sources/proto/main/grpc")
        }
    }
    test {
        java {
            srcDir("src/test/java")
        }
    }
}

application {
    mainClass.set("org.lkvkn.gistrade.users.UsersServiceApplication")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
