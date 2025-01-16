plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.maven.publish)
}

group = "eu.khonsu.libraries"
version = "1.1-SNAPSHOT"

android {
    compileSdk = 35
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
    implementation(libs.annotation)
    implementation(libs.core.ktx)

    testImplementation(libs.junit.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.android.test.core)
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
