package com.example.composeclean.core.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Factory helpers for building the singleton [OkHttpClient] and [Retrofit] instances.
 *
 * These are intentionally plain functions rather than a stateful object: Hilt's [NetworkModule]
 * owns the actual singletons and calls into here. Centralising the construction logic keeps the
 * Retrofit/OkHttp setup in one place and makes it easy to unit-test the configuration.
 */
object ApiClient {

    /** Lenient JSON configured to ignore unknown server fields — resilient to API additions. */
    @OptIn(ExperimentalSerializationApi::class)
    val json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    /**
     * Builds a configured [Retrofit] instance.
     *
     * @param okHttpClient the shared client carrying interceptors and timeouts.
     * @param baseUrl the API base URL; defaults to the value baked into [BuildConfig].
     */
    fun create(
        okHttpClient: OkHttpClient,
        baseUrl: String = BuildConfig.BASE_URL,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(CONTENT_TYPE.toMediaType()))
        .build()

    private const val CONTENT_TYPE = "application/json"
}
