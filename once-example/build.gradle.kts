plugins {
    id("com.android.application")
}

android {
    compileSdk = 34
    namespace = "jonathanfinerty.onceexample"

    defaultConfig {
        applicationId = "jonathanfinerty.onceexample"
        minSdk = 19
        targetSdk = 34
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
    }
}

dependencies {
    implementation(project(":once"))
    implementation("androidx.appcompat:appcompat:1.6.1")
}
