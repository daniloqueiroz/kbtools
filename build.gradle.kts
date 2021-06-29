import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "makesomenoise"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://m2proxy.atlassian.com/repository/public")
    }
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("AppKt")
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:3.2.0")

    implementation("com.atlassian.jira:jira-rest-java-client-api:5.2.2")
    implementation("com.atlassian.jira:jira-rest-java-client-core:5.2.2")
    implementation("io.atlassian.fugue:fugue:4.7.1")

    testImplementation(kotlin("test"))
}
