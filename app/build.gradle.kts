plugins {
    kotlin("jvm")
    id("application")
}

application {
    mainClass = "ru.dmitriyt.uno.presentation.MainKt"
}

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(19)
}