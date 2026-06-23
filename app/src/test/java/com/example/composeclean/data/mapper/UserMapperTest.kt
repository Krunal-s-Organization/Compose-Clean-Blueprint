package com.example.composeclean.data.mapper

import com.example.composeclean.data.remote.dto.UserDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies the DTO ⇄ Entity ⇄ Domain mappings, including the null-phone coercion that protects the
 * domain model from the sample API's optional fields.
 */
class UserMapperTest {

    @Test
    fun `dto with null phone maps to entity with empty phone`() {
        val dto = UserDto(id = 1, name = "Ada", email = "ada@example.com", phone = null)

        val entity = dto.toEntity(cachedAt = 123L)

        assertEquals(1, entity.id)
        assertEquals("Ada", entity.name)
        assertTrue(entity.phone.isEmpty())
        assertEquals(123L, entity.cachedAt)
    }

    @Test
    fun `entity round-trips to domain preserving fields`() {
        val dto = UserDto(id = 7, name = "Grace", email = "grace@example.com", phone = "555")

        val domain = dto.toEntity(cachedAt = 0L).toDomain()

        assertEquals(7, domain.id)
        assertEquals("Grace", domain.name)
        assertEquals("grace@example.com", domain.email)
        assertEquals("555", domain.phone)
    }
}
