package ru.netology

import org.junit.Assert.*
import org.junit.Test
import ru.netology.entities.ChatService
import ru.netology.exceptions.*
import ru.netology.util.VkUtils
import kotlin.math.min

class ChatTest {

    @Test
    fun testSendMessage() {
        val service = ChatService(VkUtils.USER1)
        val otherUser = VkUtils.USER2
        val text = "Привет!"
        val ids = service.sendMessage(otherUser.id, text)
        val chat = service.getChats().last()
        assertEquals(chat.id, ids.chatId)
        val msg = chat.messages.last()
        assertEquals(msg.id, ids.messageId)
        assertEquals(text, msg.text)
    }

    @Test
    fun testEditMessage() {
        val service = ChatService(VkUtils.USER1)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Ghbdtn!")
        val newText = "Привет!"
        assertTrue(service.editMessage(ids.chatId, ids.messageId, newText))
        assertEquals(newText, service.getChats().last().messages.last().text)
    }

    @Test
    fun testEditMessageWithWrongIds() {
        val service = ChatService(VkUtils.USER1)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Ghbdtn!")
        val newText = "Привет!"
        assertThrows(ChatNotFoundException::class.java) {
            service.editMessage(-ids.chatId, ids.messageId, newText)
        }
        assertThrows(MessageNotFoundException::class.java) {
            service.editMessage(ids.chatId, -ids.messageId, newText)
        }
    }

    @Test
    fun testReadMessage() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Привет!")
        service.currentUser = otherUser
        assertTrue(service.readMessage(ids.chatId, ids.messageId))
    }

    @Test
    fun testReadMessageWithWrongIds() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Привет!")
        service.currentUser = otherUser
        assertThrows(ChatNotFoundException::class.java) {
            service.readMessage(-ids.chatId, ids.messageId)
        }
        assertThrows(MessageNotFoundException::class.java) {
            service.readMessage(ids.chatId, -ids.messageId)
        }
    }

    @Test
    fun testWrongReadMessage() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Привет!")
        assertFalse(service.readMessage(ids.chatId, ids.messageId)) // чтение отправителем своего же сообщения
        service.currentUser = otherUser
        service.readMessage(ids.chatId, ids.messageId)
        assertFalse(service.readMessage(ids.chatId, ids.messageId)) // чтение адресатом уже прочитанного сообщения
        service.getChats().last().messages.last().isRead = false // попытка пометить прочитанное сообщение непрочитанным
        assertTrue(service.getChats().last().messages.last().isRead)
    }

    @Test
    fun testDeleteMessage() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Привет!")
        assertTrue(service.deleteMessage(ids.chatId, ids.messageId))
        assertTrue(service.getChats().last().messages.isEmpty())
    }

    @Test
    fun testDeleteMessageWithWrongIds() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Привет!")
        assertThrows(ChatNotFoundException::class.java) {
            service.deleteMessage(-ids.chatId, ids.messageId)
        }
        assertThrows(MessageNotFoundException::class.java) {
            service.deleteMessage(ids.chatId, -ids.messageId)
        }
    }

    @Test
    fun testGetUnreadChatsCount() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Привет!")
        service.currentUser = otherUser
        assertEquals(1, service.getUnreadChatsCount())
        service.readMessage(ids.chatId, ids.messageId)
        assertEquals(0, service.getUnreadChatsCount())
    }

    @Test
    fun testDelete() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Привет!")
        val thirdUser = VkUtils.createNewUser()
        service.sendMessage(thirdUser.id, "Hello!")
        assertTrue(service.delete(ids.chatId))
        assertEquals(1, service.getChats().size)
        assertThrows(ChatNotFoundException::class.java) {
            service.readMessage(ids.chatId, ids.messageId)
        }
    }

    @Test(expected = ChatNotFoundException::class)
    fun testDeleteWithWrongId() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val ids = service.sendMessage(otherUser.id, "Привет!")
        service.delete(-ids.chatId)
    }

    @Test
    fun testGetLastMessage() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val noMsg = "Нет сообщений"
        val first1 = "Привет!"
        service.sendMessage(otherUser.id, first1)
        val text1 = "Как жизнь?"
        service.sendMessage(otherUser.id, text1)
        val thirdUser = VkUtils.createNewUser()
        val first2 = "Hello!"
        service.sendMessage(thirdUser.id, first2)
        val text2 = "How are you?"
        service.sendMessage(thirdUser.id, text2)
        val last = service.getLastMessages()
        assertEquals(2, last.size)
        assertFalse(last.any { it.contains(first1) })
        assertFalse(last.any { it.contains(first2) })
        assertTrue(last.any { it.contains(text1) })
        assertTrue(last.any { it.contains(text2) })
        assertFalse(last.any { it.contains(noMsg) })
    }

    @Test
    fun testGetLastMessageWithNoMsg() {
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val text = "Привет!"
        val noMsg = "Нет сообщений"
        val ids = service.sendMessage(otherUser.id, text)
        service.deleteMessage(ids.chatId, ids.messageId)
        val last = service.getLastMessages()
        assertEquals(1, last.size)
        assertFalse(last.any { it.contains(text) })
        assertTrue(last.any { it.contains(noMsg) })
    }

    @Test
    fun testGetMessages(){
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val text = "С добрым утром!"
        text.forEach { service.sendMessage(otherUser.id, it.toString()) }
        val length = min(text.length, 10)
        service.currentUser = otherUser
        val last = service.getMessages(otherUser.id, length)
        assertEquals(length, last.size)
        val messages = service.getChats().last().messages.takeLast(length)
        messages.forEach { assertTrue(it.isRead) }
    }

    @Test(expected = ChatNotFoundException::class)
    fun testGetMessagesWithWrongId(){
        val thisUser = VkUtils.USER1
        val service = ChatService(thisUser)
        val otherUser = VkUtils.USER2
        val text = "С добрым утром!"
        text.forEach { service.sendMessage(otherUser.id, it.toString()) }
        val length = min(text.length, 10)
        service.currentUser = otherUser
        service.getMessages(-otherUser.id, length)
    }
}