plugins {
    kotlin("jvm")
    id("application")
}

application {
    mainClass = "ru.dmitriyt.uno.presentation.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(19)
}