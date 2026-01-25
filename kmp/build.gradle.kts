import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.sqldelight)
}

group = "dev.vicart.pixelcount"
version = providers.gradleProperty("app.version").get()

kotlin {

    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidLibrary {
        compileSdk = libs.versions.compileSdk.get().toInt()
        namespace = "dev.vicart.pixelcount.kmp"
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11 // Max supported version for Android
        }
        androidResources {
            enable = true
        }

        minSdk = libs.versions.minSdk.get().toInt()
    }

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptive.navigation3)
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.navigation3.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.lifecycle.navigation3)
            implementation(libs.lifecycle.runtime)

            implementation(libs.kotlinx.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.sqldelight.coroutines)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.sqldelight.jvm)
            implementation(libs.kotlinx.coroutines.swing)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "dev.vicart.pixelcount.resources"
    generateResClass = always
}

compose.desktop {
    application {
        mainClass = "dev.vicart.pixelcount.MainKt"
        buildTypes {
            release {
                proguard {
                    isEnabled = false
                }
            }
        }

        nativeDistributions {
            packageName = "PixelCount"
            vendor = "ClementVicart"
            packageVersion = version.toString()

            modules("java.sql")

            targetFormats(TargetFormat.Exe, TargetFormat.Deb)

            linux {
                iconFile.set(project.file("assets/icon-192.png"))
            }
            windows {
                iconFile.set(project.file("assets/favicon.ico"))
            }
        }
    }
}

sqldelight {
    databases {
        create("PixelCountDatabase") {
            packageName = "dev.vicart.pixelcount.data.database"
            srcDirs("src/commonMain/sqldelight")
        }
    }
}