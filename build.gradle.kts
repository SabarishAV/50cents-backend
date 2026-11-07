plugins {
	java
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.2"   // formatting
}

group = "io.app.50cents"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat()
        removeUnusedImports()
    }
}

repositories {
	mavenCentral()
}

dependencies {
    // Database & Web core
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

    // Security framework
    implementation("org.springframework.boot:spring-boot-starter-security")

    // OAuth
    implementation("com.google.api-client:google-api-client:2.7.0")

    // JJWT libraries for token generation and parsing
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    //Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Dev Tools & Code reduction
	compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Swagger / OpenAPI 3 UI support for Spring Boot 4
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register<JavaExec>("seed") {
    group = "application"
    description = "Seeds the database without starting the app"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.app._cents.backend.seeders.SeederApplication")
}

tasks.register<JavaExec>("seedDown") {
    group = "application"
    description = "Deletes everything in the database"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.app._cents.backend.seeders.SeederDownApplication")
}

// Point git at the committed hooks folder automatically on every build
tasks.register<Exec>("installGitHooks") {
    commandLine("git", "config", "core.hooksPath", ".githooks")
    isIgnoreExitValue = true
}
tasks.named("compileJava") { dependsOn("installGitHooks") }