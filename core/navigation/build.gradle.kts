plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

// Pure Kotlin/JVM module holding the type-safe NavRoute destinations. Kept separate from :app so
// that feature modules can read navigation arguments (see UserDetailViewModel) without depending on
// the application module, which would create a feature -> app circular dependency.

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
