plugins {
    java
    id("org.springframework.boot") version "3.2.5"
}

group = "com.insureflow"
version = "0.0.1-SNAPSHOT"
description = "member-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
    compileOnly(platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
    developmentOnly(platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
    runtimeOnly(platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
    testCompileOnly(platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
    testRuntimeOnly(platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
    testAnnotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testCompileOnly("org.projectlombok:lombok")
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
