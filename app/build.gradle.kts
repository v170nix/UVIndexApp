@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
//    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization") version "1.8.21"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        apiVersion = "1.8"
        languageVersion = "1.8"
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    namespace = "uv.index"

    packaging {
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

    val composeBom = platform("androidx.compose:compose-bom:2023.05.00")
    implementation (composeBom)
    androidTestImplementation (composeBom)

    implementation("com.github.v170nix:place-selector-library:1.0.0-alpha03")

    // 'com.github.User.Repo:Module:Tag'
  //  implementation("com.github.v170nix.place-selector-library:library:1.0.0-alpha01")

    detektPlugins("com.twitter.compose.rules:detekt:0.0.26")

    implementation("androidx.appcompat:appcompat:1.6.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    implementation(platform("com.google.firebase:firebase-bom:31.5.0"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("com.google.dagger:hilt-android:2.45")
    kapt("com.google.dagger:hilt-android-compiler:2.45")

    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation("androidx.navigation:navigation-common-ktx:2.6.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.collection:collection-ktx:1.2.0")

    implementation("androidx.work:work-runtime-ktx:2.8.1")

    implementation("io.ktor:ktor-client-core:2.3.0")
    implementation("io.ktor:ktor-client-okhttp:2.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")

    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime-livedata:1.4.3")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

//    implementation("com.google.accompanist:accompanist-insets:0.25.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.28.0")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.28.0")

    implementation("com.google.android.libraries.places:places:3.2.0")
    implementation("com.google.maps.android:maps-ktx:3.4.0")
    implementation("com.google.maps.android:maps-utils-ktx:3.4.0")
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
//    implementation("com.google.firebase:firebase-functions-ktx")
//    implementation("com.google.firebase:firebase-crashlytics")
//    implementation("com.google.android.gms:play-services-ads:21.1.0")

    implementation("com.android.billingclient:billing-ktx:6.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("com.github.v170nix:arwix-common-library:0.4.3")
    implementation("com.github.v170nix:uv-index-library:1.1.8")
//    implementation("ui.index.lib:uv-index-library:1.1.7")
//    implementation("com.github.v170nix.astronomy-core:astronomy-core:1.0.0-alpha22")
    implementation("net.arwix.urania:astronomy-core:1.0.0-alpha23")

    implementation("com.halilibo.compose-richtext:richtext-ui-material3:0.16.0")
    implementation("com.halilibo.compose-richtext:richtext-commonmark:0.16.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.room:room-testing:2.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.3")
}