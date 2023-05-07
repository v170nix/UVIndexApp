buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44.2")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }

}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}