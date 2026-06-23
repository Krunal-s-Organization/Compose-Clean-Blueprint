package com.example.composeclean.core.common

import javax.inject.Qualifier

/**
 * Qualifies the [kotlinx.coroutines.CoroutineDispatcher] used for IO-bound work (disk, network).
 *
 * Injecting the dispatcher rather than referencing `Dispatchers.IO` directly lets tests substitute
 * a deterministic test dispatcher, which is essential for reliable repository unit tests.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/** Qualifies the main/UI [kotlinx.coroutines.CoroutineDispatcher]. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher
