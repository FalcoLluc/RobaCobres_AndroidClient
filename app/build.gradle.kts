plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.robacobres_androidclient"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.robacobres_androidclient"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // HttpLoggingInterceptor
    implementation(libs.logging.interceptor)

    implementation (libs.glide)
    annotationProcessor (libs.compiler)

    implementation(libs.appcompat.v151)
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")

    implementation(files("libs/unityLibrary-debug.aar"))
}