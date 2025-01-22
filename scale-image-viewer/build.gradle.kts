plugins {
    id("com.android.library")
    id("kotlin-android")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.jvziyaoyao.scale.image"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    api(project(":scale-zoomable-view"))
    // androidx.exif
    implementation("androidx.exifinterface:exifinterface:1.3.7")

    // core-ktx
    implementation(libs.androidx.core.ktx)

    // appcompat
    implementation(libs.androidx.appcompat)

    // material
    implementation("com.google.android.material:material:1.12.0")

    // androidx.compose.ui
    implementation("androidx.compose.ui:ui:1.7.6")
    implementation("androidx.compose.material:material:1.7.6")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.6")

    // androidx.lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // androidx.activity.compose
    implementation("androidx.activity:activity-compose:1.10.0")

    // androidx.test.ext.junit
    androidTestImplementation(libs.androidx.junit)

    // espresso-core
    androidTestImplementation(libs.androidx.espresso.core)

    // junit
    testImplementation(libs.junit)
}