plugins {
    id("com.google.protobuf") version "0.9.5"
    id("java-library")
}

group = "org.lkvkn.gistrade"

repositories {
    mavenCentral()
}

dependencies {
    api("io.grpc:grpc-protobuf:1.80.0")
    api("io.grpc:grpc-stub:1.80.0")
    api("com.google.protobuf:protobuf-java-util:3.25.8")
    api("javax.annotation:javax.annotation-api:1.3.2")
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
            srcDir("build/generated/sources/proto/main/java")
        }
    }
}