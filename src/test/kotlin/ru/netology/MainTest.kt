package ru.netology

import org.junit.Assert.*
import org.junit.Test
import ru.netology.exceptions.*
import ru.netology.entities.*

class MainTest {

    @Test
    fun testAddPost() {
        val service = WallService()
        val postId = 0
        val addedPost = service.add(
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
    fun testUpdatePostWithExistingId() {
        val service = WallService()
        val firstPost = service.add(
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
        assertTrue(service.update(secondPost))
    }

    @Test
    fun testUpdatePostWithNonExistingId() {
        val service = WallService()
        val firstPost = service.add(
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
        assertFalse(service.update(secondPost))
    }

    @Test
    fun testAddCommentToExistingPost() {
        val service = WallService()
        val firstPost = service.add(
            Post(
                ownerId = 2_323_445,
                fromId = 234_845,
                createdBy = 0,
                date = 1_698_000_333,
                text = "Hello Kotlin!"
            )
        )
        val postId = firstPost.id
        val firstComment = service.createComment(
            postId, Comment(
                0,
                222,
                1_698_000_444,
                "Hello",
                getCommentDonut(),
                0,
                0,
                emptyArray(),
                emptyArray(),
                getCommentThread()
            )
        )
        assertNotNull(firstComment)
    }

    @Test(expected = PostNotFoundException::class)
    fun testAddCommentToNonExistingPost() {
        val service = WallService()
        val firstPost = service.add(
            Post(
                ownerId = 2_323_445,
                fromId = 234_845,
                createdBy = 0,
                date = 1_698_000_333,
                text = "Hello Kotlin!"
            )
        )
        val postId = -firstPost.id
        service.createComment(
            postId, Comment(
                0,
                222,
                1_698_000_444,
                "Hello",
                getCommentDonut(),
                0,
                0,
                emptyArray(),
                emptyArray(),
                getCommentThread()
            )
        )
    }

    @Test
    fun testReportComment() {
        val service = WallService()
        val firstPost = service.add(
            Post(
                ownerId = 2_323_445,
                fromId = 234_845,
                createdBy = 0,
                date = 1_698_000_333,
                text = "Hello Kotlin!"
            )
        )
        val postId = firstPost.id
        val firstComment = service.createComment(
            postId, Comment(
                0,
                222,
                1_698_000_444,
                "This is spam",
                getCommentDonut(),
                0,
                0,
                emptyArray(),
                emptyArray(),
                getCommentThread()
            )
        )
        val report = service.report(firstComment.id, 0)
        assertNotNull(report)
    }

    @Test(expected = CommentNotFoundException::class)
    fun testReportNonExistingComment() {
        val service = WallService()
        val firstPost = service.add(
            Post(
                ownerId = 2_323_445,
                fromId = 234_845,
                createdBy = 0,
                date = 1_698_000_333,
                text = "Hello Kotlin!"
            )
        )
        val postId = firstPost.id
        val firstComment = service.createComment(
            postId, Comment(
                0,
                222,
                1_698_000_444,
                "This is spam",
                getCommentDonut(),
                0,
                0,
                emptyArray(),
                emptyArray(),
                getCommentThread()
            )
        )
        service.report(-firstComment.id, 0)
    }

    @Test(expected = ReasonNotFoundException::class)
    fun testReportCommentWithNonExistingReason() {
        val service = WallService()
        val firstPost = service.add(
            Post(
                ownerId = 2_323_445,
                fromId = 234_845,
                createdBy = 0,
                date = 1_698_000_333,
                text = "Hello Kotlin!"
            )
        )
        val postId = firstPost.id
        val firstComment = service.createComment(
            postId, Comment(
                0,
                222,
                1_698_000_444,
                "This is spam",
                getCommentDonut(),
                0,
                0,
                emptyArray(),
                emptyArray(),
                getCommentThread()
            )
        )
        service.report(firstComment.id, 10)
    }

    private fun getCommentDonut(): CommentDonut {
        return CommentDonut(false, "нет подписки VK Donut")
    }

    private fun getCommentThread(): CommentThread {
        return CommentThread(
            0,
            emptyArray(),
            canPost = true,
            showReplyButton = true,
            groupsCanPost = true
        )
    }
}