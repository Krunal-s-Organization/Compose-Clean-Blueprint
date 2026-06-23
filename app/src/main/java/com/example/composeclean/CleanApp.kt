package com.example.composeclean

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point.
 *
 * The [HiltAndroidApp] annotation triggers Hilt's code generation and creates the
 * application-level dependency container that every other Hilt component is built on top of.
 * Every Hilt-enabled Android app must declare exactly one annotated [Application] class and
 * register it via `android:name` in the manifest.
 */
@HiltAndroidApp
class CleanApp : Application()
