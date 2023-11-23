package ru.netology

import ru.netology.exceptions.*

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

data class CommentsInfo(
    val count: Int = 0,
    val canPost: Boolean = true,
    val groupsCanPost: Boolean = true,
    val canClose: Boolean = true,
    val canOpen: Boolean = true
)

data class LikesInfo(
    val count: Int = 0,
    val userLikes: Boolean = false,
    val canLike: Boolean = true,
    val canPublish: Boolean = true
)

data class RepostsInfo(
    val count: Int = 0,
    val userReposted: Boolean = false
)

data class ViewsInfo(
    val count: Int = 0
)

data class Copyright(
    val id: Int,
    val link: String,
    val name: String,
    val type: String
)

data class PostSource(
    val type: String,
    val platform: String,
    val data: String,
    val url: String
)

data class Geo(
    val type: String,
    val coordinates: String,
    val place: Place
)

data class Place(
    val id: Int,
    val title: String,
    val latitude: Int,
    val longitude: Int,
    val created: Int,
    val icon: String,
    val country: String,
    val city: String
)

data class Donut(
    val isDonut: Boolean,
    val paidDuration: Int,
    val placeholder: Any,
    val canPublishFreeCopy: Boolean,
    val editMode: String
)

data class Post(
    val id: Int = 0,
    val ownerId: Int,
    val fromId: Int,
    val createdBy: Int,
    val date: Int,
    val text: String,
    val replyOwnerId: Int? = null,
    val replyPostId: Int? = null,
    val friendsOnly: Boolean = false,
    val comments: CommentsInfo = CommentsInfo(),
    val copyright: Copyright? = null,
    val likes: LikesInfo = LikesInfo(),
    val reposts: RepostsInfo = RepostsInfo(),
    val views: ViewsInfo = ViewsInfo(),
    val postType: String = "post",
    val postSource: PostSource? = null,
    val attachments: Array<Attachment> = emptyArray(),
    val geo: Geo? = null,
    val signerId: Int? = null,
    val copyHistory: Array<Post> = emptyArray(),
    val canPin: Boolean = false,
    val canDelete: Boolean = true,
    val canEdit: Boolean = true,
    val isPinned: Boolean = false,
    val markedAsAds: Boolean = false,
    val isFavourite: Boolean = false,
    val donut: Donut? = null,
    val postponedId: Int? = null
)

data class Comment(
    val id: Int,
    val fromId: Int,
    val date: Int,
    val text: String,
    val donut: CommentDonut,
    val replyToUser: Int,
    val replyToComment: Int,
    val attachments: Array<Attachment>,
    val parentsStack: Array<Int>,
    val thread: CommentThread
)

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

data class CommentThread(
    val count: Int,
    val items: Array<Comment>,
    val canPost: Boolean,
    val showReplyButton: Boolean,
    val groupsCanPost: Boolean
)

data class CommentDonut(
    val isDon: Boolean,
    val placeholder: String
)


class WallService {

    private var id: Int = 0
    private var posts = emptyArray<Post>()
    private var comments = emptyArray<Comment>()
    private var reports = emptyArray<Report>()

    fun clear() {
        id = 0
        posts = emptyArray()
        comments = emptyArray()
    }

    fun findPostById(postId: Int): Post? {
        for (post in posts) {
            if (post.id == postId) {
                return post
            }
        }
        return null
    }
    fun findCommentById(commentId: Int): Comment? {
        for (comment in comments) {
            if (comment.id == commentId) {
                return comment
            }
        }
        return null
    }

    fun createComment(postId: Int, comment: Comment): Comment {
        if (findPostById(postId) != null) {
            comments += comment.copy(id = ++id)
            return comments.last()
        }
        throw PostNotFoundException("No post with id $postId")
    }

    fun report(commentId: Int, reason: Int): Report{
        val comment = findCommentById(commentId) ?: throw CommentNotFoundException("No comment with id $commentId")
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