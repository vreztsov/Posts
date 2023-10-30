package ru.netology

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class MainTest {

    @Before
    fun clearBeforeTest() {
        WallService.clear()
    }

    @Test
    fun addPostTest() {
        val postId = 0
        val addedPost = WallService.add(
            Post(
                postId,
                2_323_445,
                234_845,
                0,
                1_698_000_333,
                "Hello Kotlin!"
            )
        )
        assertEquals(postId + 1, addedPost.id)
    }

    @Test
    fun updatePostWithExistingIdTest() {
        val firstPost = WallService.add(
            Post(
                ownerId = 2_323_445,
                fromId = 234_845,
                createdBy = 0,
                date = 1_698_000_333,
                text = "Hello Kotlin!"
            )
        )
        val postId = firstPost.id
        val secondPost = Post(
            postId,
            2_323_445,
            234_845,
            0,
            1_698_000_444,
            "Kotlin is wonderful!"
        )
        assertTrue(WallService.update(secondPost))
    }

    @Test
    fun updatePostWithNonExistingIdTest() {
        val firstPost = WallService.add(
            Post(
                ownerId = 2_323_445,
                fromId = 234_845,
                createdBy = 0,
                date = 1_698_000_333,
                text = "Hello Kotlin!"
            )
        )
        val postId = -firstPost.id
        val secondPost = Post(
            postId,
            2_323_445,
            234_845,
            0,
            1_698_000_444,
            "Kotlin is wonderful!"
        )
        assertFalse(WallService.update(secondPost))
    }
}