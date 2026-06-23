package com.example.composeclean.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Application interceptor that attaches common headers (e.g. an auth token and JSON content
 * negotiation) to every outgoing request.
 *
 * In a real project [tokenProvider] would read the current session token from secure storage
 * (DataStore / EncryptedSharedPreferences). It is modelled as a lambda so the networking layer
 * stays decoupled from any particular storage mechanism and remains trivially testable.
 */
@Singleton
class NetworkInterceptor @Inject constructor(
    private val tokenProvider: AuthTokenProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
            .header(HEADER_ACCEPT, CONTENT_TYPE_JSON)

        tokenProvider.currentToken()?.let { token ->
            requestBuilder.header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$token")
        }

        return chain.proceed(requestBuilder.build())
    }

    private companion object {
        const val HEADER_ACCEPT = "Accept"
        const val HEADER_AUTHORIZATION = "Authorization"
        const val CONTENT_TYPE_JSON = "application/json"
        const val BEARER_PREFIX = "Bearer "
    }
}

/**
 * Abstraction over wherever the current auth token lives. Provided by Hilt so it can be swapped
 * for a fake in tests. Returns `null` when the user is unauthenticated.
 */
fun interface AuthTokenProvider {
    fun currentToken(): String?
}
