plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Pure Kotlin/JVM module: framework-free utilities (Resource, UiState, dispatcher qualifiers)
// shared by every other module. No Android or Hilt dependency on purpose.

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
}
