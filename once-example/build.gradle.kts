plugins {
    id("com.android.application")
    id("org.jlleitschuh.gradle.ktlint") version libs.versions.ktlint
}

android {
    compileSdk = 36
    namespace = "eu.khonsu.onceexample"

    defaultConfig {
        applicationId = "eu.khonsu.onceexample"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
        }
    }
    lint {
        warningsAsErrors = true
        abortOnError = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":once"))
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
}
