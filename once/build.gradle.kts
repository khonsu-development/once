plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.maven.publish)
}

group = "eu.khonsu.libraries"
version = "1.0-SNAPSHOT"

android {
    compileSdk = 37
    namespace = "eu.khonsu.libraries.once"

    defaultConfig {
        minSdk = 21
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                ),
            )
        }
    }
    publishing {
        singleVariant("release") {}
    }
    lint {
        warningsAsErrors = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    implementation(libs.annotation)
    implementation(libs.core.ktx)

    testImplementation(libs.junit.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.android.test.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.group as String
            artifactId = "once"
            version = project.version as String

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
