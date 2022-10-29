import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
//    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization") version "1.6.21"
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("io.gitlab.arturbosch.detekt") version("1.21.0")
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("../config/detekt/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

tasks.withType<Detekt>().configureEach {
    reports {
        html {
            required.set(true)
            outputLocation.set(file("build/reports/detekt.html"))
        }
    }
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "uv.index"
        minSdk = 24
        targetSdk = 33
        versionCode = 1000
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        resourceConfigurations += setOf("ru", "en")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }

    namespace = "uv.index"

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

kapt {
    correctErrorTypes = true
}

dependencies {

    detektPlugins("com.twitter.compose.rules:detekt:0.0.12")

    implementation("androidx.appcompat:appcompat:1.5.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation(platform("com.google.firebase:firebase-bom:30.3.2"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    implementation("com.google.dagger:hilt-android:2.43.2")
    kapt("com.google.dagger:hilt-android-compiler:2.43.2")

    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.navigation:navigation-common-ktx:2.5.3")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation("androidx.work:work-runtime-ktx:2.7.1")

    implementation("io.ktor:ktor-client-core:2.0.3")
    implementation("io.ktor:ktor-client-okhttp:2.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")

    implementation("androidx.room:room-runtime:2.5.0-beta01")
    kapt("androidx.room:room-compiler:2.5.0-beta01")
    implementation("androidx.room:room-ktx:2.5.0-beta01")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.compose.ui:ui:1.3.0")
    implementation("androidx.compose.material3:material3:1.0.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.3.0")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")

    implementation("com.google.accompanist:accompanist-insets:0.25.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.25.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.24.13-rc")

    implementation("com.google.android.libraries.places:places:2.6.0")
    implementation("com.google.maps.android:maps-ktx:3.4.0")
    implementation("com.google.maps.android:maps-utils-ktx:3.4.0")
    implementation("com.google.maps.android:maps-compose:2.5.3")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:20.0.0")

    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
//    implementation("com.google.firebase:firebase-functions-ktx")
//    implementation("com.google.firebase:firebase-crashlytics")
//    implementation("com.google.android.gms:play-services-ads:21.1.0")

    implementation("com.android.billingclient:billing-ktx:5.0.0")

    implementation("com.android.billingclient:billing-ktx:5.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("com.github.v170nix:arwix-common-library:0.4.0")
    implementation("com.github.v170nix:uv-index-library:1.0.7")
    implementation("com.github.v170nix.astronomy-core:astronomy-core:1.0.0-alpha22")

    implementation("com.halilibo.compose-richtext:richtext-ui-material3:0.13.0")
    implementation("com.halilibo.compose-richtext:richtext-commonmark:0.13.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.room:room-testing:2.5.0-beta01")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.3.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.3.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.3.0")
}