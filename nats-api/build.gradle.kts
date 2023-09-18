plugins {
    id("com.google.protobuf") version "0.9.4"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.20.1"
    }
}

group = "com.ajax"
version = "0.0.1-SNAPSHOT"
