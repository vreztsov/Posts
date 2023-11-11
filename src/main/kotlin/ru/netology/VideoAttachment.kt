package ru.netology

class VideoAttachment(
    val video: Video
) : Attachment("video")

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val description: String,
    val duration: Int,
    val image: Array<Image>,
    val firstFrame: Array<Frame>,
    val date: Int,
    val addingDate: Int,
    val views: Int,
    val localViews: Int,
    val comments: Int,
    val player: String,
    val platform: String,
    val canAdd: Boolean,
    val isPrivate: Int = 1,
    val accessKey: String,
    val processing: Int = 1,
    val isFavourite: Boolean,
    val canComment: Boolean,
    val canEdit: Boolean,
    val canLike: Boolean,
    val canRepost: Boolean,
    val canSubscribe: Boolean,
    val canAddToFaves: Boolean,
    val canAttachLink: Boolean,
    val width: Int,
    val height: Int,
    val userId: Int,
    val converting: Boolean,
    val added: Boolean,
    val isSubscribed: Boolean,
    val repeat: Int = 1,
    val type: String,
    val balance: Int,
    val liveStatus: String,
    val live: Int = 1,
    val upcoming: Int = 1,
    val spectators: Int,
    val likes: Like,
    val reposts: Repost
)

data class Like(
    val count: Int,
    val userLikes: Boolean
)

data class Repost(
    val count: Int,
    val wallCount: Int,
    val mainCount: Int,
    val userReposted: Boolean
)

data class Image(
    val height: Int,
    val width: Int,
    val url: String,
    val withPadding: Int = 1
)

data class Frame(
    val height: Int,
    val width: Int,
    val url: String
)