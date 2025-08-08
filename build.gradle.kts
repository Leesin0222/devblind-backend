plugins {
	java
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.yongjincompany"
version = "0.0.1-SNAPSHOT"

// Dependency versions
val firebaseVersion = "9.2.0"
val awsSdkVersion = "2.24.12"
val jwtVersion = "0.12.5"
val mockitoVersion = "5.8.0"
val springdocVersion = "2.3.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
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
	// Spring Boot Starter들 추가
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-websocket")

	// API 문서화 추가
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

	// 외부 서비스 추가
	implementation("com.google.firebase:firebase-admin:$firebaseVersion")
	implementation("software.amazon.awssdk:s3:$awsSdkVersion")
	
	// JWT 추가
	implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-gson:$jwtVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
	
	// DB
	runtimeOnly("org.postgresql:postgresql")
	testRuntimeOnly("com.h2database:h2")
	
	// Development Tool들 추가
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	
	// 테스트 의존성
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito:mockito-core:$mockitoVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
