package ru.netology.entities

import ru.netology.attachments.Attachment

data class Comment(
    override val id: Int,
    val fromId: Int,
    val date: Int,
    val text: String,
    val donut: CommentDonut,
    val replyToUser: Int,
    val replyToComment: Int,
    val attachments: Array<Attachment>,
    val parentsStack: Array<Int>,
    val thread: CommentThread
): Entity

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