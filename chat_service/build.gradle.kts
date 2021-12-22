val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val jbcrypt_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.30"
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("org.jetbrains.exposed:exposed:0.17.13")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("org.mindrot:jbcrypt:$jbcrypt_version")
    implementation(kotlin("stdlib"))
}