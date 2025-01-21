plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.sdk.lulupay"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sdk.lulupay"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation "androidx.constraintlayout:constraintlayout:2.1.4"
    implementation "androidx.sqlite:sqlite:2.3.0"
    implementation "androidx.sqlite:sqlite-framework:2.3.0"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1" // Or the latest version
    implementation "androidx.appcompat:appcompat:1.6.1"
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.8.0' // Update Kotlin version if possible
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
    implementation "androidx.core:core-ktx:1.12.0"
    implementation 'com.squareup.retrofit2:retrofit:2.9.0' // Updated Retrofit version
    implementation "com.squareup.retrofit2:converter-gson:2.9.0" // Keep one instance
//implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"
//implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.1'
    implementation 'com.google.crypto.tink:tink-android:1.7.0'
    implementation 'com.google.android.material:material:1.9.0'
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}