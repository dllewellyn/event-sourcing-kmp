buildscript {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
    }
}

plugins {
    id("com.android.library")
    kotlin("multiplatform") version "1.4.20"
    kotlin("plugin.serialization") version "1.4.20"
    `maven-publish`
}

publishing {
    repositories {
        maven {
            name = "EventSourcingKMP"
            url = uri("https://maven.pkg.github.com/dllewellyn/event-sourcing-kmp")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}

val versionString : String by project
group = "me.danielllewellyn"
version = versionString

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
    val serializationVersion: String by project

    val iosEnabledStr: String by project
    val iosEnabled = iosEnabledStr == "true"

    android {
        publishLibraryVariants("release", "debug")
    }

    jvm()

    if (iosEnabled) {
        iosArm64("ios"){
            binaries {
                framework {
                    baseName = "event-sourcing"
                }
            }
        }

        iosX64 {
            binaries {
                framework {
                    baseName = "event-sourcing"
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion-native-mt")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

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

        if (iosEnabled) {
            val iosMain by getting {
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-stdlib")
                }
            }
            val iosTest by getting
            val iosX64Main by getting {
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-stdlib")
                }
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:$junitVersion")
                implementation("com.google.truth:truth:$googleTruthVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
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
    buildTypes {
        getByName("release") {
            isShrinkResources = false
            isMinifyEnabled = false
        }
        getByName("debug") {}
    }
}