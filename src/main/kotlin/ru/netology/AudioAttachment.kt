package ru.netology

data class AudioAttachment(
    val audio: Audio
) : Attachment("audio")

data class Audio(
    val id: Int,
    val ownerId: Int,
    val artist: String,
    val title: String,
    val duration: Int,
    val url: String,
    val lyricsId: Int,
    val albumId: Int,
    val genreId: Int,
    val date: Int,
    val noSearch: Int = 1,
    val isHq: Int = 1
)