plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    id("androidx.navigation.safeargs")
    alias(libs.plugins.googleServices)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.compicomida"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.compicomida"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val mapsApiKey: String = project.findProperty("MAPS_API_KEY") as String? ?: ""
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.material.lists.listitem)

    // Maps
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.places)

    // Coil
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)

    // Room dependencies
    //val room_version = "2.6.1"
    implementation(libs.androidx.room.runtime)
    androidTestImplementation(libs.androidx.espresso.contrib)

    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore) // Remote DB

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    //Test
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.core.testing)

    // Base
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
