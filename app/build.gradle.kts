import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.57"
}
hilt {
    enableAggregatingTask = false
}

android {
    namespace = "com.example.cycles"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cycles"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("11")
        }
    }
    buildFeatures {
        compose = true
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Jetpack Compose core
    implementation(libs.androidx.activity.compose)
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)


    // ViewModel + Compose integration
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Para observar estados (StateFlow, LiveData, etc.)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Optional - Preview en IDE
    debugImplementation(libs.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Hilt con Jetpack Compose
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.javapoet)
    kapt(libs.javapoet)

    //ktor
    implementation(libs.ktor.client.okhttp)

    //retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.logging.interceptor) //logs http

    //datastore para mantener sesion iniciada (guardar tokens de autenticacion)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)


}