plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.gmail.redballtoy.news.uikit"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        compose=true
    }

    composeOptions{
        kotlinCompilerExtensionVersion = "1.5.13"
    }


}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.ui)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling.preview)
    //api(libs.androidx.compose.runtime)
    api(libs.androidx.material3)
    debugApi(libs.androidx.ui.tooling)
    debugApi(libs.androidx.ui.test.manifest)
}