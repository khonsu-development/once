plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("maven-publish")
}

group = "eu.khonsu.libraries"
version = "1.1-SNAPSHOT"

android {
    compileSdk = 34
    namespace = "eu.khonsu.libraries.once"

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
    implementation("androidx.core:core-ktx:1.13.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.12.2")
    testImplementation("androidx.test:core:1.5.0")
}

repositories {
    mavenCentral()
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.group as String
            artifactId = "material-cards"
            version = project.version as String

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
