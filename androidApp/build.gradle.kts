import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

android {
    namespace = "dev.vicart.pixelcount"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        targetSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        applicationId = "dev.vicart.pixelcount"
        versionName = "1.0.0"
        versionCode = 1
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    signingConfigs {
//        create("release") {
//            storeFile = file(providers.gradleProperty("android.sign.storeFile").get())
//            keyAlias = providers.gradleProperty("android.sign.alias").get()
//            storePassword = providers.gradleProperty("android.sign.storePassword").get()
//            keyPassword = providers.gradleProperty("android.sign.keyPassword").get()
//        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt")
            )
            //signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(project(":kmp"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
}