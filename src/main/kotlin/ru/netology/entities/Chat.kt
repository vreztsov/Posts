package ru.netology.entities

import ru.netology.exceptions.ChatNotFoundException
import ru.netology.exceptions.MessageNotFoundException
import ru.netology.util.VkUtils

data class Chat(
    override val id: Int,
    val users: List<User>,
    val messages: MutableList<Message>
) : Entity {
    val isWithUser = fun(userId: Int): Boolean {
        return VkUtils.findIndexById(users, userId) != null
    }

    val containsUnread = fun(currentUserId: Int): Boolean {
        return messages.any { it.peerId == currentUserId && !it.isRead }
    }

    val getLastMessage = fun(currentUserId: Int): String {
        val msg = try {
            messages.last().text
        } catch (e: NoSuchElementException) {
            "Нет сообщений"
        }
        val user = users.last { it.id != currentUserId }
        return "$user \n $msg"
    }
}

class ChatService(var currentUser: User = VkUtils.USER1) {
    private var id = 0
    private val chats = mutableListOf<Chat>()

    private fun createChat(userId: Int): Chat {
        val chat = Chat(
            ++id,
            listOf(currentUser, User(userId)),
            mutableListOf()
        )
        chats += chat
        return chats.last()
    }

    private fun List<Chat>.getByUserId(userId: Int): Chat? {
        return try {
            last { it.isWithUser(userId) }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    private fun List<Chat>.getLastMessages(): List<String> {
        return map { it.getLastMessage(currentUser.id) }
    }

    fun sendMessage(userId: Int, text: String): MessageChatIds {
        val chat = chats.getByUserId(userId) ?: createChat(userId)
        val message = Message(
            ++id,
            (System.currentTimeMillis() / 1000).toInt(),
            userId,
            currentUser.id,
            text
        )
        chat.messages += message
        return MessageChatIds(chat.id, chat.messages.last().id)
    }

    fun editMessage(chatId: Int, messageId: Int, text: String): Boolean {
        val messages = getMessagesList(chatId)
        val messageIndex = VkUtils.findIndexById(messages, messageId)
            ?: throw MessageNotFoundException("Message #$messageId does not exist")
        if (messages[messageIndex].fromId == currentUser.id) {
            messages[messageIndex] = messages[messageIndex].copy(text = text)
            return true
        }
        return false
    }

    fun readMessage(chatId: Int, messageId: Int): Boolean {
        val messages = getMessagesList(chatId)
        val messageIndex = VkUtils.findIndexById(messages, messageId)
            ?: throw MessageNotFoundException("Message #$messageId does not exist")
        if (!messages[messageIndex].isRead && messages[messageIndex].peerId == currentUser.id) {
            messages[messageIndex].isRead = true
            return true
        }
        return false
    }

    fun deleteMessage(chatId: Int, messageId: Int): Boolean {
        val messages = getMessagesList(chatId)
        val messageIndex = VkUtils.findIndexById(messages, messageId)
            ?: throw MessageNotFoundException("Message #$messageId does not exist")
        val deletingMessage = messages[messageIndex]
        return (messages.removeAt(messageIndex) === deletingMessage)
    }

    private fun getMessagesList(chatId: Int): MutableList<Message> {
        val chatIndex =
            VkUtils.findIndexById(chats, chatId) ?: throw ChatNotFoundException("Chat #$chatId does not exist")
        return chats[chatIndex].messages
    }

    fun getChats(): List<Chat> {
        return chats
    }

    fun getUnreadChatsCount(): Int {
        return chats.filter { it.containsUnread(currentUser.id) }.size
    }

    fun delete(chatId: Int): Boolean {
        val chatIndex =
            VkUtils.findIndexById(chats, chatId) ?: throw ChatNotFoundException("Chat #$chatId does not exist")
        val chat = chats[chatIndex]
        return chats.removeAt(chatIndex) === chat
    }

    fun getLastMessages(): List<String> {
        return chats.getLastMessages()
    }

    fun getMessages(userId: Int, msgQuantity: Int): List<String> =
        chats.getByUserId(userId)
            ?.messages
            ?.takeLast(msgQuantity)
            ?.onEach { it.isRead = true }
            ?.map { it.text }
            ?: throw ChatNotFoundException("Chat with user #$userId does not exist")
}

data class MessageChatIds(
    val chatId: Int,
    val messageId: Int
)