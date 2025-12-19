import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.57.2"
    id("com.google.gms.google-services")
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
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    // usar
    // buildConfigField("String", "AUTH_BASE_URL", "\"http://192.168.1.8:8080/\"")
    // buildConfigField("String", "RECS_BASE_URL", "\"http://192.168.1.8:8000/\"")
    //para probar localmente
    buildTypes {
        debug {
            buildConfigField("String", "AUTH_BASE_URL", "\"https://cycles-backend.onrender.com\"")
            buildConfigField("String", "RECS_BASE_URL", "\"https://knn-ann-algorithm-377792293762.southamerica-west1.run.app/\"")
        }
        release {
            buildConfigField( "String", "AUTH_BASE_URL", "\"https://cycles-backend.onrender.com\"")
            buildConfigField( "String", "RECS_BASE_URL", "\"https://knn-ann-algorithm-377792293762.southamerica-west1.run.app/\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
        android.buildFeatures.buildConfig = true
        compose = true
    }
    kotlinOptions {
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }

}
configurations.all {
    // Excluye la dependencia duplicada de todas las configuraciones
    exclude(group = "com.google.guava", module = "listenablefuture")
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
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.benchmark.common)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.ui.tooling.v192)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.compiler)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
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
    implementation(libs.accompanist.navigation.animation.v0340)

    // Asegúrate de tener también la dependencia de Compose Navigation


    // ViewModel + Compose integration
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Para observar estados (StateFlow, LiveData, etc.)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Optional - Preview en IDE
    debugImplementation(libs.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    debugImplementation(libs.androidx.ui.tooling.v192)
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
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.coil.compose) //coil: libreria para el proceso de carga de imagenes desde api
    // 👇 ESTA ES LA DEPENDENCIA FALTANTE
    debugImplementation(libs.androidx.ui.tooling)

    // También es buena práctica añadir esto si quieres interactuar con la preview:
    debugImplementation(libs.androidx.ui.tooling.preview)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.play.services.auth)
    implementation(libs.glide)
    // Para Google Sign-In

}
