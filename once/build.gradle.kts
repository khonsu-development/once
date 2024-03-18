plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android") version "1.9.23"
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 19
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            setProguardFiles(
                    listOf(
                            getDefaultProguardFile("proguard-android.txt"),
                            "proguard-rules.pro",
                    ),
            )
        }
    }
    namespace = "jonathanfinerty.once"
    lint {
        warningsAsErrors = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.core:core-ktx:1.12.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core:1.5.0")
}

repositories {
    mavenCentral()
}
