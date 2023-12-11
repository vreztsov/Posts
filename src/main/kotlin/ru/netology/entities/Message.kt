package ru.netology.entities

data class Message(
    override val id: Int,
    val date: Int,
    val peerId: Int,
    val fromId: Int,
    val text: String,
): Entity {
    var isRead: Boolean = false
    set(value) {
        if (value) {
            field = value
        }
    }
}