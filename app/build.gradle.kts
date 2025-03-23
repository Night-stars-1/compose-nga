plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.google.dagger.hilt.android)
}

android {
    namespace = "com.srap.nga"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.srap.nga"
        minSdk = 26
        targetSdk = 35
        versionCode = 5
        versionName = "测试版"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = "night_star"
            keyPassword = "james123"
            storeFile = file("night_star.jks")
            storePassword = "james123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".test"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.ui.tooling.preview.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ui
    implementation(libs.constraintlayout.compose)
    // UI预览
    implementation(libs.androidx.ui.tooling)

    // 网络请求
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // 依赖项注入
    implementation(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.android.compiler)

    // md3
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.size)

    // 导航
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    // 自适应导航
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    // 图标
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // 抓包
    debugImplementation(libs.user.certificate.trust)

    // 网络图像加载
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    // 网络请求库
    implementation(libs.okhttp)
    // 图片浏览库
    implementation(project(":scale-image-viewer"))
    implementation(project(":scale-zoomable-view"))

    // 二维码生成
    implementation(libs.zxing.core)

    // HTML解析库
    implementation(libs.jsoup)
}
