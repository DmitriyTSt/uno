pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("jvm").version("2.1.10")
        id("org.jetbrains.compose").version("1.8.0-alpha04")
        id("org.jetbrains.kotlin.plugin.compose").version("2.1.10")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "uno"

include(":app")
include(":desktopApp")
include(":core")

