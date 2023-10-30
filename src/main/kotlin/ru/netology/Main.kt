package ru.netology

fun main() {
    WallService.add(
        Post(
            ownerId = 2_323_445,
            fromId = 234_845,
            createdBy = 0,
            date = 1_698_000_333,
            text = "Hello Kotlin!"
        )
    )
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

data class Post(
    var id: Int = 0,
    val ownerId: Int,
    val fromId: Int,
    val createdBy: Int,
    val date: Int,
    val text: String,
    val friendsOnly: Boolean = false,
    val comments: CommentsInfo = CommentsInfo(),
    val likes: LikesInfo = LikesInfo(),
    val reposts: RepostsInfo = RepostsInfo(),
    val views: ViewsInfo = ViewsInfo(),
    val postType: String = "post",
    val canPin: Boolean = false,
    val canDelete: Boolean = true,
    val canEdit: Boolean = true
)

object WallService {

    private var id: Int = 0
    private var posts = emptyArray<Post>()

    fun clear() {
        id = 0
        posts = emptyArray()
    }

    fun add(post: Post): Post {
        post.id = ++id
        posts += post
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