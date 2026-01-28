import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
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
        applicationId = "dev.vicart.pixelcount"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()

        versionName = providers.gradleProperty("app.version").getOrElse("0.0.0")
        versionCode = providers.gradleProperty("app.versionCode").getOrElse("1").toInt()
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
            signingConfig = signingConfigs.findByName("release")
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)

    implementation(libs.wear.compose.foundation)
    implementation(libs.wear.compose.material3)
    implementation(libs.wear.compose.navigation)

    implementation(libs.play.services.wearable)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.kotlinx.datetime)

    implementation(project(":shared"))
}