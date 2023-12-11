package ru.netology.entities

import ru.netology.exceptions.*
import ru.netology.util.VkUtils
import java.lang.StringBuilder

data class Note(
    override val id: Int,
    val title: String,
    val text: String,
    val date: Int,
    val comments: Int,
    val readComments: Int,
    val viewURL: String
) : Entity {
    private val privacyLevels = listOf(0, 1, 2, 3)
    var privacy: Int = 0
        set(value) {
            if (!privacyLevels.contains(value)) {
                throw WrongCodeOfPrivacyException("Неверный код уровня доступа к заметке ($value)")
            }
            field = value
        }
    var commentPrivacy: Int = 0
        set(value) {
            if (!privacyLevels.contains(value)) {
                throw WrongCodeOfPrivacyException("Неверный код уровня доступа к комментированию заметки ($value)")
            }
            field = value
        }
}

data class NoteComment(
    override val id: Int,
    val noteId: Int,
    val message: String
) : Entity {
    val date = (System.currentTimeMillis() / 1000).toInt()
    var isPresent = true
}

class NoteService {
    private var id: Int = 0
    private val notes = mutableListOf<Note>()
    private val comments = mutableListOf<NoteComment>()

    fun add(
        title: String,
        text: String,
        privacy: Int = 0,
        commentPrivacy: Int = 0,
    ): Int =
        notes.apply {
            val note = Note(
                id = ++id,
                title,
                text,
                (System.currentTimeMillis() / 1000).toInt(),
                0,
                0,
                "https://vk.com/note33546456"
            )
            note.privacy = privacy
            note.commentPrivacy = commentPrivacy
            add(note)
        }.last().id


    fun createComment(noteId: Int, message: String): Int {
        if (notes.none { it.id == noteId }) throw NoteNotFoundException("Note #$noteId does not exist")
        if (message.length < 2) {
            throw CommentException("Comment must be at least 2 characters in length")
        }
        return comments
            .apply {
                add(NoteComment(++id, noteId, message))
            }
            .last().id
    }

    fun delete(noteId: Int): Boolean =
        notes
            .removeIf { it.id == noteId }
            .also { removed ->
                if (!removed) {
                    throw NoteNotFoundException("Note #$noteId does not exist")
                } else {
                    comments
                        .filter { it.noteId == noteId }
                        .forEach { deleteComment(it.id) }
                }
            }


    fun deleteComment(commentId: Int): Boolean =
        comments.find { it.id == commentId }
            ?.let {
                if (it.isPresent) {
                    it.isPresent = false
                    true
                } else throw CommentNotFoundException("Comment #$commentId was already deleted")
            } ?: throw CommentNotFoundException("Comment #$commentId does not exist")


    fun edit(
        id: Int,
        title: String,
        text: String,
        privacy: Int = 0,
        commentPrivacy: Int = 0,
    ): Boolean {
        val index = notes
            .indexOfFirst { it.id == id }
            .also { if (it == -1) throw NoteNotFoundException("Note #$id does not exist") }
        return notes
            .let {
                notes[index] = notes[index]
                    .copy(title = title, text = text)
                    .also {
                        it.privacy = privacy
                        it.commentPrivacy = commentPrivacy
                    }
                true
            }
    }

    fun editComment(commentId: Int, message: String): Boolean {
        val index = comments.indexOfFirst { it.id == commentId }
            .also { if (it == -1) throw CommentNotFoundException("Comment #$commentId does not exist") }
        return comments.let {
            if (comments[index].isPresent) {
                if (message.length < 2) {
                    throw CommentException("Comment must be at least 2 characters in length")
                }
                comments[index] = comments[index].copy(message = message)
                true
            } else throw CommentNotFoundException("Comment #$commentId was already deleted")
        }
    }

    fun get(noteList: String, sort: Int = 0): List<Note> =
        noteList
            .split(",")
            .map { Integer.parseInt(it) }
            .partition { i ->
                notes.any { it.id == i }
            }.apply {
                second
                    .joinToString(separator = ", ", transform = { it.toString() })
                    .also {
                        if (it.isNotEmpty()) {
                            throw NoteNotFoundException("Note(s) $it does not exist")
                        }
                    }
            }.first
            .map { notes.find { note -> note.id == it }!! }
            .toMutableList()
            .apply {
                sortWith(NoteDateComparator(sort))
            }

    fun getById(noteId: Int): Note =
        notes.find { it.id == noteId } ?: throw NoteNotFoundException("Note(s) #$noteId does not exist")


    fun getComments(noteId: Int, sort: Int = 0): List<NoteComment> {
        if (notes.none { it.id == noteId }) throw NoteNotFoundException("Note(s) #$noteId does not exist")
        return comments.filter { it.noteId == noteId && it.isPresent }
            .toMutableList()
            .apply { sortWith(NoteCommentDateComparator(sort)) }
    }

    fun restoreComment(commentId: Int): Boolean =
        comments.find { it.id == commentId }
            ?.let {
                if (!it.isPresent) {
                    if (notes.none { note -> note.id == it.noteId }) {
                        throw NoteNotFoundException("Note for comment #$commentId was deleted")
                    }
                    it.isPresent = true
                    true
                } else false
            } ?: throw CommentNotFoundException("Comment #$commentId does not exist")

}

class NoteDateComparator(
    sort: Int = 0
) : Comparator<Note> {
    private val comparator = DateComparator(sort)

    override fun compare(o1: Note, o2: Note): Int =
        comparator.compare(o1.date, o2.date)

}

class NoteCommentDateComparator(
    sort: Int = 0
) : Comparator<NoteComment> {

    private val comparator = DateComparator(sort)

    override fun compare(o1: NoteComment, o2: NoteComment): Int =
        comparator.compare(o1.date, o2.date)

}

class DateComparator(
    sort: Int
) : Comparator<Int> {
    private val sign: Int = when (sort) {
        0 -> -1
        1 -> 1
        else -> throw WrongCodeOfComparatorException(
            "Код сортировки результатов должен быть:\n" +
                    "0 — по дате создания в порядке убывания\n" +
                    "1 - по дате создания в порядке возрастания"
        )
    }

    override fun compare(o1: Int, o2: Int): Int =
        when {
            o1 == o2 -> 0
            o1 > o2 -> 1 * sign
            else -> -1 * sign
        }

}