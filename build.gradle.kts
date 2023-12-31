import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
	id("io.gitlab.arturbosch.detekt") version "1.23.1"
	id("com.google.protobuf") version "0.9.4"
	kotlin("jvm") version "1.9.0"
	kotlin("plugin.spring") version "1.9.0"
}

group = "com.ajax"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
	maven("https://packages.confluent.io/maven/")
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

dependencies {
	implementation(project(":cryptocurrency-subdomain"))
	implementation(project(":common-subdomain"))
	implementation(project(":nats-api"))
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.1.4")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.1.4")
	implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.4")
	implementation("io.projectreactor:reactor-core:3.5.10")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.1.4")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
	implementation("org.json:json:20230227")
	implementation("io.nats:jnats:2.16.14")
	implementation("io.grpc:grpc-protobuf:1.58.0")
	implementation("io.grpc:grpc-netty:1.58.0")
	implementation("io.grpc:grpc-stub:1.58.0")
	implementation("com.salesforce.servicelibs:reactor-grpc:1.2.4")
	implementation("com.salesforce.servicelibs:reactive-grpc-common:1.2.4")
	implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.4")
	implementation("com.google.protobuf:protobuf-java:3.24.2")
	implementation("com.google.protobuf:protobuf-java-util:3.20.1")
	implementation("org.springframework.kafka:spring-kafka:3.0.11")
	implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
	implementation("io.confluent:kafka-protobuf-serializer:7.4.0")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.4")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.13.7")
	testImplementation("io.projectreactor:reactor-test:3.5.10")
	testImplementation("io.grpc:grpc-testing:1.38.1")
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

allprojects {
	group = "com.ajax"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
		maven("https://packages.confluent.io/maven/")
	}
}

subprojects {
	apply(plugin = "kotlin")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "io.gitlab.arturbosch.detekt")
	apply(plugin = "com.google.protobuf")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-validation:3.1.4")
		implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.1.4")
		implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.1.4")
		implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.4")
		implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
		implementation("com.google.protobuf:protobuf-java:3.24.2")
		implementation("com.google.protobuf:protobuf-java-util:3.20.1")
		implementation("io.grpc:grpc-protobuf:1.58.0")
		implementation("io.grpc:grpc-netty:1.58.0")
		implementation("io.grpc:grpc-stub:1.58.0")
		implementation("com.salesforce.servicelibs:reactor-grpc:1.2.4")
		implementation("com.salesforce.servicelibs:reactive-grpc-common:1.2.4")
		implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.4")
		implementation("io.nats:jnats:2.16.14")
		implementation("io.projectreactor:reactor-core:3.5.10")
		implementation("io.confluent:kafka-protobuf-serializer:7.4.0")
		implementation("org.springframework.kafka:spring-kafka:3.0.11")
		implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
		implementation("org.json:json:20230227")
		annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.4")
	}

	java {
		sourceCompatibility = JavaVersion.VERSION_17
	}

	configurations {
		compileOnly {
			extendsFrom(configurations.annotationProcessor.get())
		}
	}

	tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
		enabled = false
	}

	tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
		enabled = false
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-Xjsr305=strict"
			jvmTarget = "17"
		}
	}
}
