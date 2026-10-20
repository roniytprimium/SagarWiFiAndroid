plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Firebase setup step: uncomment this after placing app/google-services.json:
    // id("com.google.gms.google-services")
}

android {
    namespace = "com.sagarwifi.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sagarwifi.app"
        minSdk = 23
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // Firebase Messaging SDK. Configure Firebase and google-services.json for live push.
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-messaging")
}
