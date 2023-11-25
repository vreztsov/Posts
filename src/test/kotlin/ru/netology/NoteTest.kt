package ru.netology

import org.junit.Test
import org.junit.Assert.*
import ru.netology.entities.*
import ru.netology.exceptions.*
import ru.netology.util.VkUtils
import java.lang.StringBuilder

class NoteTest {

    @Test
    fun testAddNote() {
        val title = "Title"
        val message = "Message"
        val service = NoteService()
        val id = service.add(title, message, 1, 1)
        assertEquals(1, id)
        assertEquals(title, service.getById(id).title)
        assertEquals(message, service.getById(id).text)
        val title2 = "Other Title"
        val message2 = "Other Message"
        val id2 = service.add(title2, message2)
        assertEquals(2, id2)
        assertEquals(title2, service.getById(id2).title)
        assertEquals(message2, service.getById(id2).text)
    }

    @Test(expected = WrongCodeOfPrivacyException::class)
    fun testAddNoteWithWrongPrivacy() {
        val title = "Title"
        val message = "Message"
        val service = NoteService()
        service.add(title, message, 10, 1)
    }

    @Test(expected = WrongCodeOfPrivacyException::class)
    fun testAddNoteWithWrongCommentPrivacy() {
        val title = "Title"
        val message = "Message"
        val service = NoteService()
        service.add(title, message, 1, -1)
    }

    @Test
    fun testCreateComment() {
        val message = "Hello Kotlin"
        val service = NoteService()
        val noteId = createNote(service)
        val commentId = service.createComment(noteId, message)
        val commentList = service.getComments(noteId)
        val commentIndex = VkUtils.findIndexById(commentList, commentId)
        assertEquals(message, commentList[commentIndex!!].message)
        assertEquals(2, commentId)
    }

    @Test(expected = CommentException::class)
    fun testCreateTooShortComment() {
        val message = "1"
        val service = NoteService()
        val noteId = createNote(service)
        service.createComment(noteId, message)
    }

    @Test(expected = NoteNotFoundException::class)
    fun testCreateCommentForNonExistingNote() {
        val message = "Hello Kotlin"
        val service = NoteService()
        val noteId = createNote(service)
        service.createComment(-noteId, message)
    }

    @Test
    fun testDelete() {
        val service = NoteService()
        val noteId = createNote(service)
        assertTrue(service.delete(noteId))
        assertThrows(NoteNotFoundException::class.java) {
            service.get("$noteId")
        }
        assertThrows(NoteNotFoundException::class.java) {
            service.getComments(noteId)
        }
    }

    @Test(expected = NoteNotFoundException::class)
    fun testDeleteNonExistingNote() {
        val service = NoteService()
        val noteId = createNote(service)
        service.delete(-noteId)
    }

    @Test
    fun testDeleteComment() {
        val message = "Hello Kotlin"
        val service = NoteService()
        val noteId = createNote(service)
        service.createComment(noteId, message)
        val commentId = service.createComment(noteId, "$message!!!")
        assertTrue(service.deleteComment(commentId))
        assertNull(VkUtils.findIndexById(service.getComments(noteId), commentId))
    }

    @Test(expected = CommentNotFoundException::class)
    fun testDeleteNonExistingComment() {
        val message = "Hello Kotlin"
        val service = NoteService()
        val noteId = createNote(service)
        val commentId = service.createComment(noteId, message)
        service.deleteComment(-commentId)
    }

    @Test
    fun testDeleteAlreadyDeletedComment() {
        val message = "Hello Kotlin"
        val service = NoteService()
        val noteId = createNote(service)
        service.createComment(noteId, message)
        val commentId = service.createComment(noteId, "$message!!!")
        service.deleteComment(commentId)
        assertThrows(CommentNotFoundException::class.java) {
            service.deleteComment(commentId)
        }
    }

    @Test
    fun testEdit() {
        val service = NoteService()
        val noteId = createNote(service)
        val title = "Edited Note"
        val text = "Edited by function testEdit"
        assertTrue(service.edit(noteId, title, text))
        assertEquals(title, service.getById(noteId).title)
        assertEquals(text, service.getById(noteId).text)
    }

    @Test(expected = NoteNotFoundException::class)
    fun testEditNonExistingNote() {
        val service = NoteService()
        val noteId = createNote(service)
        val title = "Edited Note"
        val text = "Edited by function testEdit"
        service.edit(-noteId, title, text)
    }

    @Test
    fun testEditComment() {
        val message = "Hello Kotlin"
        val service = NoteService()
        val noteId = createNote(service)
        service.createComment(noteId, message)
        val commentId = service.createComment(noteId, "$message!!!")
        val newMessage = "Edited Comment"
        assertTrue(service.editComment(commentId, newMessage))
        val comments = service.getComments(noteId)
        assertEquals(newMessage, comments[VkUtils.findIndexById(comments, commentId)!!].message)
    }

    @Test(expected = CommentNotFoundException::class)
    fun testEditNonExistingComment() {
        val message = "Hello Kotlin"
        val service = NoteService()
        val noteId = createNote(service)
        val commentId = service.createComment(noteId, message)
        service.editComment(-commentId, "error")
    }

    @Test(expected = CommentException::class)
    fun testEditCommentWithTooShortMessage() {
        val message = "Hello Kotlin"
        val service = NoteService()
        val noteId = createNote(service)
        val commentId = service.createComment(noteId, message)
        service.editComment(commentId, "a")
    }

    @Test(expected = CommentNotFoundException::class)
    fun testEditDeletedComment() {
        val message = "Hello Kotlin"
        val service = NoteService()
        val noteId = createNote(service)
        service.createComment(noteId, message)
        val commentId = service.createComment(noteId, "$message!!!")
        service.deleteComment(commentId)
        service.editComment(commentId, "deleted")
    }

    @Test
    fun testGet() {
        val service = NoteService()
        val sb = StringBuilder()
        for (i in 1..4) {
            if (sb.isNotEmpty()) {
                sb.append(',')
            }
            sb.append(service.add("Note #$i", "Message #$i"))
            Thread.sleep(1000)
        }
        val noteList = service.get(sb.toString(), 0)
        assertEquals(4, noteList.size)
        for (i in 0 until noteList.size - 1) {
            assertTrue(noteList[i].date > noteList[i + 1].date)
        }
        val noteListSort1 = service.get(sb.toString(), 1)
        for (i in 0 until noteListSort1.size - 1) {
            assertTrue(noteListSort1[i].date < noteListSort1[i + 1].date)
        }
    }

    @Test(expected = NoteNotFoundException::class)
    fun testGetWithWrongId() {
        val service = NoteService()
        val sb = StringBuilder()
        for (i in 1..4) {
            if (sb.isNotEmpty()) {
                sb.append(',')
            }
            when (i) {
                1, 2 -> sb.append(service.add("Note #$i", "Message #$i"))
                else -> sb.append(-service.add("Note #$i", "Message #$i"))
            }
        }
        service.get(sb.toString(), 0)
    }

    @Test
    fun testGetById() {
        val service = NoteService()
        var id = 0
        for (i in 1..4) {
            when (i) {
                2 -> id = service.add("Note #$i", "Message #$i")
                else -> service.add("Note #$i", "Message #$i")
            }
        }
        val note = service.getById(id)
        assertEquals("Note #2", note.title)
        assertEquals("Message #2", note.text)
    }

    @Test(expected = NoteNotFoundException::class)
    fun testGetByWrongId() {
        val service = NoteService()
        val noteId = createNote(service)
        service.getById(-noteId)
    }

    @Test
    fun testGetComments() {
        val service = NoteService()
        val noteId = createNote(service)
        val noteId1 = createNote(service)
        val num = 4
        var commentId = 0
        for (i in 1..num) {
            when (i) {
                2 -> commentId = service.createComment(noteId, "Comment #$i")
                else -> service.createComment(noteId, "Comment #$i")
            }
            Thread.sleep(1000)
        }
        service.createComment(noteId1, "One more comment")
        val commentList = service.getComments(noteId, 0)
        assertEquals(num, commentList.size)
        for (i in 0 until commentList.size - 1) {
            assertTrue(commentList[i].date > commentList[i + 1].date)
        }
        val commentList1 = service.getComments(noteId, 1)
        assertEquals(num, commentList1.size)
        for (i in 0 until commentList1.size - 1) {
            assertTrue(commentList1[i].date < commentList1[i + 1].date)
        }
        service.deleteComment(commentId)
        assertEquals(num-1, service.getComments(noteId).size)
    }

    @Test(expected = NoteNotFoundException::class)
    fun testGetCommentsByWrongNoteId() {
        val service = NoteService()
        val noteId = createNote(service)
        val noteId1 = createNote(service)
        for (i in 1..4) {
            service.createComment(noteId, "Comment #$i")
        }
        service.createComment(noteId1, "One more comment")
        service.getComments(-noteId, 0)
    }

    @Test
    fun testRestoreComment(){
        val service = NoteService()
        val noteId = createNote(service)
        val num = 4
        var commentId = 0
        for (i in 1..num) {
            when (i) {
                2 -> commentId = service.createComment(noteId, "Comment #$i")
                else -> service.createComment(noteId, "Comment #$i")
            }
        }
        service.deleteComment(commentId)
        assertTrue(service.restoreComment(commentId))
        assertNotNull(VkUtils.findIndexById(service.getComments(noteId), commentId))
        assertEquals(num, service.getComments(noteId).size)
    }

    @Test(expected = CommentNotFoundException::class)
    fun testRestoreNonExistingComment(){
        val service = NoteService()
        val noteId = createNote(service)
        val commentId = service.createComment(noteId, "Comment")
        service.deleteComment(commentId)
        service.restoreComment(-commentId)
    }

    @Test(expected = NoteNotFoundException::class)
    fun testRestoreCommentForDeletedNote(){
        val service = NoteService()
        val noteId = createNote(service)
        val commentId = service.createComment(noteId, "Comment")
        service.delete(noteId)
        service.restoreComment(commentId)
    }

    @Test
    fun testRestoreExistingComment(){
        val service = NoteService()
        val noteId = createNote(service)
        val commentId = service.createComment(noteId, "Comment")
        assertFalse(service.restoreComment(commentId))
    }

    private fun createNote(service: NoteService): Int {
        return service.add("Note", "Created by function createNote")
    }
}