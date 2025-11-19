

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {

    buildFeatures {
        viewBinding = true
    }

    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 29
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // CameraX
    implementation(libs.androidx.camera.core)
// adapte la version
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view.v123)
// fournit PreviewView
// ML Kit - Barcode Scanning
    implementation(libs.barcode.scanning)

    implementation(libs.kotlinx.coroutines.android)


    implementation(libs.barcode.scanning)

    implementation ("com.github.bumptech.glide:glide:5.0.5")

    implementation(libs.osmdroid.android)

    implementation("org.maplibre.gl:android-sdk:12.1.3")
// Version stable MapLibre SDK



}
//
//dependencies {
//    // Core Android
//    implementation (libs.androidx.core.ktx.v1170)
//    implementation (libs.androidx.appcompat)
//    implementation (libs.material)
//    implementation (libs.androidx.constraintlayout)
//
//    // CameraX
//    implementation (libs.androidx.camera.core)
//    implementation (libs.androidx.camera.camera2)
//    implementation (libs.androidx.camera.lifecycle)
//    implementation (libs.androidx.camera.view.v123)
//
//    // ML Kit Barcode
//    implementation (libs.barcode.scanning)
//
//    // Kotlin Coroutines
//    implementation (libs.kotlinx.coroutines.android)
//
//    // Glide
//    implementation (libs.glide)
//    annotationProcessor (libs.compiler)
//
//    // MapLibre (OpenStreetMap)
//    implementation (libs.android.sdk)
//}
