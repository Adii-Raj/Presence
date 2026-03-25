import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.application.presence"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.application.presence"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"${localProperties.getProperty("GOOGLE_WEB_CLIENT_ID")}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${localProperties.getProperty("SUPABASE_URL")}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_KEY",
            "\"${localProperties.getProperty("SUPABASE_KEY")}\""
        )
    }

    buildTypes {
        debug {
            // Keep these false while coding so your app compiles fast!
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            // The chainsaw! This will shrink the app before you publish.
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.googleid)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //Lifecycle ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    val ktorVersion = "3.2.1"
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    val supabaseVersion = "3.3.0"
    implementation(platform("io.github.jan-tennert.supabase:bom:$supabaseVersion"))

    implementation("io.github.jan-tennert.supabase:postgrest-kt")

    //Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    //Navigation Dependency
    implementation(libs.navigation.compose)
    //Serializable Dependency
    implementation(libs.kotlinx.serialization.json)

    //Extended Material 3 UI Icon
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    //Coil Dependency -> To Load image from internet using URL
    implementation("io.coil-kt:coil-compose:2.6.0")

    // CameraX Dependency
    implementation("androidx.camera:camera-core:1.5.0")
    implementation("androidx.camera:camera-camera2:1.5.0")
    implementation("androidx.camera:camera-lifecycle:1.5.0")
    implementation("androidx.camera:camera-view:1.5.0")
// ML Kit Barcode Scanner
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    //Osmodroid Dependency
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    // Lifecycle ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    //Google Play service Location for fast and exact loaction determination
    implementation("com.google.android.gms:play-services-location:21.2.0")

}