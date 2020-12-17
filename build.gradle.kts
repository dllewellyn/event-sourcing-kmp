plugins {
    kotlin("multiplatform") version "1.4.21"
    id("com.android.library")
    id("kotlin-android-extensions")
}

group = "me.danielllewellyn"
version = "1.0-SNAPSHOT"

repositories {
    google()
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

kotlin {

    val okioVersion: String by project
    val junitVersion: String by project
    val coroutinesVersion: String by project
    val googleTruthVersion: String by project
    val kotlinFlowTest: String by project

    android()
    jvm()
    ios("ios") {
        binaries {
            framework {
                baseName = "library"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

            }
        }
        val jvmMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:$junitVersion")
            }
        }
        val iosMain by getting {
            dependencies {
            }
        }
        val iosTest by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:$junitVersion")
                implementation("com.google.truth:truth:$googleTruthVersion")
                implementation("com.github.ologe:flow-test-observer:$kotlinFlowTest")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

            }
        }
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
    }
}