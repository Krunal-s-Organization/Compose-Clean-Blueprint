package com.example.composeclean.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Exercises the [Resource] helper extensions and the [Resource] → [UiState] folding. */
class ResourceTest {

    @Test
    fun `error preserves cached data when folded into UiState`() {
        val resource: Resource<List<Int>> = Resource.Error(
            message = "offline",
            data = listOf(1, 2, 3),
        )

        val uiState = resource.toUiState()

        assertFalse(uiState.isLoading)
        assertEquals("offline", uiState.errorMessage)
        assertEquals(listOf(1, 2, 3), uiState.data)
    }

    @Test
    fun `map transforms payload but keeps the variant`() {
        val mapped = Resource.Success(2).map { it * 10 }

        assertTrue(mapped is Resource.Success)
        assertEquals(20, mapped.getOrNull())
    }

    @Test
    fun `loading without cache reports loading and null data`() {
        val resource: Resource<String> = Resource.Loading()

        assertTrue(resource.isLoading)
        assertNull(resource.getOrNull())
    }
}
