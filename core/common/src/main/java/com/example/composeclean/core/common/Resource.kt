package com.example.composeclean.core.common

/**
 * A discriminated union describing the outcome of an asynchronous operation as it flows from the
 * data layer up to the UI.
 *
 * Each variant can optionally carry [data] so that, for example, a [Loading] or [Error] state can
 * still surface previously cached content — the cornerstone of an offline-first experience.
 *
 * @param T the type of the wrapped payload.
 */
sealed interface Resource<out T> {

    /** The (possibly stale) data currently associated with this state, or `null` if none. */
    val data: T?

    /** A successful result carrying non-null [data]. */
    data class Success<out T>(override val data: T) : Resource<T>

    /**
     * A failed result.
     *
     * @property message a human-readable description suitable for display.
     * @property throwable the underlying cause, retained for logging.
     * @property data the last known good data, if any, so the UI can keep showing it.
     */
    data class Error<out T>(
        val message: String,
        val throwable: Throwable? = null,
        override val data: T? = null,
    ) : Resource<T>

    /**
     * An in-progress result.
     *
     * @property data cached data to display while the fresh result loads, if available.
     */
    data class Loading<out T>(override val data: T? = null) : Resource<T>
}

/** `true` while the operation is still in flight. */
val Resource<*>.isLoading: Boolean
    get() = this is Resource.Loading

/** Returns the wrapped data regardless of variant, or `null` when none is present. */
fun <T> Resource<T>.getOrNull(): T? = data

/** Invokes [block] only when this resource represents a successful result. */
inline fun <T> Resource<T>.onSuccess(block: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) block(data)
    return this
}

/** Invokes [block] only when this resource represents an error. */
inline fun <T> Resource<T>.onError(block: (message: String, throwable: Throwable?) -> Unit): Resource<T> {
    if (this is Resource.Error) block(message, throwable)
    return this
}

/** Invokes [block] only while this resource represents a loading state. */
inline fun <T> Resource<T>.onLoading(block: (cached: T?) -> Unit): Resource<T> {
    if (this is Resource.Loading) block(data)
    return this
}

/** Transforms the payload of a [Resource] while preserving its variant. */
inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error -> Resource.Error(message, throwable, data?.let(transform))
    is Resource.Loading -> Resource.Loading(data?.let(transform))
}
