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
        versionName = providers.gradleProperty("app.version").get()
        versionCode = providers.gradleProperty("app.versionCode").get().toInt()
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    signingConfigs {
        providers.gradleProperty("android.sign").getOrElse("false").toBoolean().let {
            if(it) {
                create("release") {
                    storeFile = file(providers.gradleProperty("android.storeFile").get())
                    storePassword = providers.gradleProperty("android.storePassword").get()
                    keyAlias = providers.gradleProperty("android.alias").get()
                    keyPassword = providers.gradleProperty("android.keyPassword").get()
                    storeType = "JKS"
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt")
            )
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(project(":kmp"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
    implementation(libs.kotlinx.json)

    implementation(libs.play.services.wearable)
}