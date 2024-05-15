plugins {
    kotlin("jvm") version "1.9.23"
}

group = "ru.otus.otuskotlin.dsl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
}
