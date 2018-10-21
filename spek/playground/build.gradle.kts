import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.71"
}

group = "com.github.tapchicoma"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

val spek = "2.0.0-rc.1"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("org.amshove.kluent:kluent:1.42")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek")
}

tasks.withType(Test::class.java) {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}