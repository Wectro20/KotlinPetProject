import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
	id("io.gitlab.arturbosch.detekt").version("1.23.1")
	id("com.google.protobuf") version "0.9.4"
	kotlin("jvm") version "1.9.0"
	kotlin("plugin.spring") version "1.9.0"
	application
}

group = "com.ajax"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.json:json:20230227")
	implementation("io.nats:jnats:2.16.14")
	implementation("com.google.protobuf:protobuf-java:3.24.2")
	implementation("com.google.protobuf:protobuf-java-util:3.20.1")
	implementation(project(":nats-api"))

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.13.7")
	testImplementation("io.projectreactor:reactor-test:3.5.10")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

subprojects {
    apply(plugin = "kotlin")
    repositories {
        mavenCentral()
    }

    dependencies{
        implementation("com.google.protobuf:protobuf-java:3.24.2")
        implementation("com.google.protobuf:protobuf-java-util:3.20.1")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }
}
