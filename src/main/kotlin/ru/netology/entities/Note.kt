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
    ): Int {
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
        notes += note
        return notes.last().id
    }

    fun createComment(noteId: Int, message: String): Int {
        VkUtils.findIndexById(notes, noteId) ?: throw NoteNotFoundException("Note #$noteId does not exist")
        if (message.length < 2) {
            throw CommentException("Comment must be at least 2 characters in length")
        }
        val comment = NoteComment(++id, noteId, message)
        comments += comment
        return comments.last().id
    }

    fun delete(noteId: Int): Boolean {
        val note =
            notes[VkUtils.findIndexById(notes, noteId) ?: throw NoteNotFoundException("Note #$noteId does not exist")]
        if (notes.remove(note)) {
            for (comment in comments) {
                if (comment.noteId == noteId) {
                    deleteComment(comment.id)
                }
            }
            return true
        }
        return false
    }

    fun deleteComment(commentId: Int): Boolean {
        val comment = comments[VkUtils.findIndexById(comments, commentId)
            ?: throw CommentNotFoundException("Comment #$commentId does not exist")]
        if (comment.isPresent) {
            comment.isPresent = false
            return true
        }
        throw CommentNotFoundException("Comment #$commentId was already deleted")
    }

    fun edit(
        id: Int,
        title: String,
        text: String,
        privacy: Int = 0,
        commentPrivacy: Int = 0,
    ): Boolean {
        val note =
            notes[VkUtils.findIndexById(notes, id) ?: throw NoteNotFoundException("Note #$id does not exist")]
        val newNote = note.copy(title = title, text = text)
        newNote.privacy = privacy
        newNote.commentPrivacy = commentPrivacy
        return (notes.set(notes.indexOf(note), newNote) === note)
    }

    fun editComment(commentId: Int, message: String): Boolean {
        val commentIndex = VkUtils.findIndexById(comments, commentId)
            ?: throw CommentNotFoundException("Comment #$commentId does not exist")
        val comment = comments[commentIndex]
        if (comment.isPresent) {
            if (message.length < 2) {
                throw CommentException("Comment must be at least 2 characters in length")
            }
            val newComment = comment.copy(message = message)
            comments[commentIndex] = newComment
            return true
        }
        throw CommentNotFoundException("Comment #$commentId was already deleted")
    }

    fun get(noteList: String, sort: Int = 0): List<Note> {
        val notes = mutableListOf<Note>()
        val nonExistingNoteIds = StringBuilder()
        for (num in noteList.split(",")) {
            val noteId = Integer.parseInt(num)
            val noteIndex = VkUtils.findIndexById(this.notes, noteId)
            if (noteIndex != null) {
                notes += this.notes[noteIndex]
            } else {
                if (nonExistingNoteIds.isNotEmpty()) {
                    nonExistingNoteIds.append(", ")
                }
                nonExistingNoteIds.append("#$noteId")
            }
        }
        if (nonExistingNoteIds.isNotEmpty()) {
            throw NoteNotFoundException("Note(s) $nonExistingNoteIds does not exist")
        }
        notes.sortWith(NoteDateComparator(sort))
        return notes
    }

    fun getById(noteId: Int): Note {
        val index =
            VkUtils.findIndexById(notes, noteId) ?: throw NoteNotFoundException("Note(s) #$noteId does not exist")
        return notes[index]
    }

    fun getComments(noteId: Int, sort: Int = 0): List<NoteComment> {
        VkUtils.findIndexById(notes, noteId) ?: throw NoteNotFoundException("Note(s) #$noteId does not exist")
        val list = mutableListOf<NoteComment>()
        for (comment in comments) {
            if (comment.noteId == noteId && comment.isPresent) {
                list += comment
            }
        }
        list.sortWith(NoteCommentDateComparator(sort))
        return list
    }

    fun restoreComment(commentId: Int): Boolean {
        val index = VkUtils.findIndexById(comments, commentId)
            ?: throw CommentNotFoundException("Comment #$commentId does not exist")
        val comment = comments[index]
        if (!comment.isPresent) {
            VkUtils.findIndexById(notes, comment.noteId)
                ?: throw NoteNotFoundException("Note for comment #$commentId was deleted")
            comment.isPresent = true
            return true
        }
        return false
    }
}

class NoteDateComparator(
    sort: Int = 0
) : Comparator<Note> {
    private val comparator = DateComparator(sort)

    override fun compare(o1: Note, o2: Note): Int {
        return comparator.compare(o1.date, o2.date)
    }
}

class NoteCommentDateComparator(
    sort: Int = 0
) : Comparator<NoteComment> {

    private val comparator = DateComparator(sort)

    override fun compare(o1: NoteComment, o2: NoteComment): Int {
        return comparator.compare(o1.date, o2.date)
    }
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

    override fun compare(o1: Int, o2: Int): Int {
        return when {
            o1 == o2 -> 0
            o1 > o2 -> 1 * sign
            else -> -1 * sign
        }
    }
}