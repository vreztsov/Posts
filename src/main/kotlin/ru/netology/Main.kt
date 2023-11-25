package ru.netology

import ru.netology.attachments.*
import ru.netology.exceptions.*
import ru.netology.entities.*
import ru.netology.util.*

fun main() {
    val service = WallService()
    service.add(
        Post(
            ownerId = 2_323_445,
            fromId = 234_845,
            createdBy = 0,
            date = 1_698_000_333,
            text = "Hello Kotlin!"
        )
    )
    val attachment: Attachment = PhotoAttachment(
        Photo(
            0,
            0,
            0,
            0,
            "Фото",
            1_688_777_888,
            emptyArray(),
            1024,
            768
        )
    )
    whenSealed(attachment)
}

fun whenSealed(attachment: Attachment) {
    when (attachment) {
        is AudioAttachment -> println("This is AudioAttachment")
        is VideoAttachment -> println("This is VideoAttachment")
        is PhotoAttachment -> println("This is PhotoAttachment")
        is DocumentAttachment -> println("This is DocumentAttachment")
        is LinkAttachment -> println("This is LinkAttachment")
    }
}

class WallService {

    private var id: Int = 0
    private var posts = emptyList<Post>().toMutableList()
    private var comments = emptyList<Comment>().toMutableList()
    private var reports = emptyList<Report>().toMutableList()

    fun clear() {
        id = 0
        posts.clear()
        comments.clear()
        reports.clear()
    }

    fun createComment(postId: Int, comment: Comment): Comment {
        if (VkUtils.findIndexById(posts, postId) != null) {
            comments += comment.copy(id = ++id)
            return comments.last()
        }
        throw PostNotFoundException("No post with id $postId")
    }

    fun report(commentId: Int, reason: Int): Report{
        val comment = comments[VkUtils.findIndexById(comments,commentId) ?: throw CommentNotFoundException("No comment with id $commentId")]
        reports += Report(comment.fromId, comment.id, reason)
        return reports.last()
    }

    fun add(post: Post): Post {
        posts += post.copy(id = ++id)
        return posts.last()
    }

    fun update(post: Post): Boolean {
        for ((index, postFromArray) in posts.withIndex()) {
            if (postFromArray.id == post.id) {
                posts[index] = post
                return true
            }
        }
        return false
    }
}

data class Report(
    val ownerId: Int,
    val commentId: Int
) {
    private var reason: Int? = null
    private val reasons = arrayOf(0,1,2,3,4,5,6,8)

    constructor(ownerId: Int, commentId: Int, reason: Int): this(ownerId, commentId){
        if (reasons.contains(reason)) {
            this.reason = reason
        } else {
            throw ReasonNotFoundException("Reason #$reason is not found")
        }
    }
}