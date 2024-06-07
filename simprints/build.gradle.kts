plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.simprints.simprints"
    compileSdk = libs.versions.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        vectorDrawables.useSupportLibrary = true

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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)

    api(libs.dhis2.android.sdk) {
        exclude("org.hisp.dhis", "core-rules")
        exclude("com.facebook.flipper")
        this.isChanging = true
    }

    api(libs.dhis2.ruleengine) {
        exclude("junit", "junit")
    }

    api(libs.dhis2.expressionparser)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.dagger.hilt.android)
    implementation(libs.libsimprints)
    implementation(libs.androidx.coreKtx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.liveDataKtx)
    implementation(libs.androidx.fragmentKtx)
    implementation(libs.androidx.viewModelKtx)
    implementation(libs.androidx.lifecycleExtensions)
    implementation(libs.androidx.recyclerView)

    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.test.espresso)
}