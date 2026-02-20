plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.example.practicecameraapp"
  compileSdk {
    version = release(36) {
      minorApiLevel = 1
    }
  }

  defaultConfig {
    applicationId = "com.example.practicecameraapp"
    minSdk = 30
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
  }
}

dependencies {

  implementation("com.google.mlkit:barcode-scanning:17.3.0") // ML Kit barcode-scanning
  implementation("com.google.mlkit:text-recognition-japanese:16.0.1") // ML Kit text-recognition

  implementation("androidx.camera:camera-camera2:1.5.2")  // CameraX
  implementation("androidx.camera:camera-core:1.5.2")
  implementation("androidx.camera:camera-lifecycle:1.5.2")
  implementation("androidx.camera:camera-view:1.5.2")
  implementation("androidx.compose.material:material-icons-extended") // material-icons-extended

  implementation("com.google.accompanist:accompanist-permissions:0.37.3") // Permissions
  implementation("androidx.navigation:navigation-compose:2.9.6") // Navigation

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}