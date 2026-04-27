plugins {
    id("application")
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.google.protobuf") version "0.9.5"
}

group = "org.lkvkn.gistrade.users"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":users-service"))
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.grpcProtobuf)
    implementation(libs.grpcStub)
    implementation(libs.protobufJavaUtil)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.github.lognet:grpc-spring-boot-starter:5.2.0")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
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
}

application {
    mainClass.set("org.lkvkn.gistrade.api.ApiGatewayApplication")
}