plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    // Paging (Common library for pure Kotlin modules)
    implementation(libs.paging.common)
    
    // Dependency Injection
    implementation(libs.javax.inject)
}
