plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.doan_sanpham_qr"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.doan_sanpham_qr"
        minSdk = 21
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.annotation:annotation:1.3.0")
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
}