import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
        }
    }

    androidLibrary {
        compileSdk = libs.versions.compileSdk.get().toInt()
        namespace = "dev.vicart.pixelcount.shared"

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }

        minSdk = libs.versions.minSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.json)

            implementation(libs.sqldelight.coroutines)
        }

        jvmMain.dependencies {
            implementation(libs.sqldelight.jvm)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android)
        }
    }
}

sqldelight {
    databases {
        create("PixelCountDatabase") {
            packageName = "dev.vicart.pixelcount.shared.data.database"
            srcDirs("src/commonMain/sqldelight")
        }
    }
}