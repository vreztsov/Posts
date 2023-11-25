package ru.netology.attachments

data class DocumentAttachment(
    val document: Document
) : Attachment("Document")

data class Document(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val size: Int,
    val ext: String,
    val url: String,
    val date: Int,
    val type: Int,
    val preview: DocumentPreview
)

data class DocumentPreview(
    val photo: DocumentPhoto,
    val graffiti: DocumentGraffiti,
    val audioMessage: AudioMessage
)

data class DocumentPhoto(
    val sizes: Array<DocumentPhotoCopy>
)

data class DocumentPhotoCopy(
    val url: String,
    val width: Int,
    val height: Int,
    val type: String
)

data class DocumentGraffiti(
    val src: String,
    val width: Int,
    val height: Int
)

data class AudioMessage(
    val duration: Int,
    val waveform: Array<Int>,
    val linkOgg: String,
    val linkMp3: String
)