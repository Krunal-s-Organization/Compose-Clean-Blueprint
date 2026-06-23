package com.example.composeclean.core.network.di

import com.example.composeclean.core.network.AuthTokenProvider
import com.example.composeclean.core.network.ApiClient
import com.example.composeclean.core.network.BuildConfig
import com.example.composeclean.core.network.NetworkInterceptor
import com.example.composeclean.data.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt bindings for the networking stack: [OkHttpClient], [Retrofit], and each Retrofit API
 * interface. All are application-scoped singletons so connections, the thread pool, and the
 * response cache are shared across the app.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            // Verbose bodies in debug builds only; never leak payloads/headers in release.
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        networkInterceptor: NetworkInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(networkInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        ApiClient.create(okHttpClient)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    /**
     * Supplies the auth token to the network layer. Replace this stub with a real
     * implementation that reads the persisted session token (e.g. from DataStore).
     */
    @Provides
    @Singleton
    fun provideAuthTokenProvider(): AuthTokenProvider = AuthTokenProvider { null }

    private const val TIMEOUT_SECONDS = 30L
}
