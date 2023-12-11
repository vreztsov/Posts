package ru.netology.entities

import ru.netology.attachments.Attachment

data class Post(
    override val id: Int = 0,
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
): Entity

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

