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
        return users.any { it.id == userId }
    }

    val containsUnread = fun(currentUserId: Int): Boolean {
        return messages.any { it.peerId == currentUserId && !it.isRead }
    }

    val getLastMessage = fun(currentUserId: Int): String {
        return users.last { it.id != currentUserId }.toString() +
                " \n " +
                (messages.lastOrNull()?.text ?: "Нет сообщений")
    }
}

class ChatService(var currentUser: User = VkUtils.USER1) {
    private var id = 0
    private val chats = mutableListOf<Chat>()

    private fun createChat(userId: Int): Chat =
        chats.apply {
            add(
                Chat(
                    ++id,
                    listOf(currentUser, VkUtils.getUserById(userId)),
                    mutableListOf()
                )
            )
        }.last()


    private fun List<Chat>.getByUserId(userId: Int): Chat? =
        lastOrNull { it.isWithUser(userId) }


    private fun List<Chat>.getLastMessages(): List<String> =
        map { it.getLastMessage(currentUser.id) }


    fun sendMessage(userId: Int, text: String): MessageChatIds =
        (chats.getByUserId(userId) ?: createChat(userId))
            .let { chat ->
                MessageChatIds(
                    chat.id,
                    chat.messages.apply {
                        add(
                            Message(
                                ++id,
                                (System.currentTimeMillis() / 1000).toInt(),
                                userId,
                                currentUser.id,
                                text
                            )
                        )
                    }.last().id
                )
            }


    fun editMessage(chatId: Int, messageId: Int, text: String): Boolean {
        val index = getMessagesList(chatId)
            .indexOfFirst { it.id == messageId }
            .also { if (it == -1) throw MessageNotFoundException("Message #$messageId does not exist") }
        return getMessagesList(chatId)
            .let {
                if (it[index].fromId == currentUser.id) {
                    it[index] = it[index].copy(text = text)
                    true
                } else false
            }
    }

    fun readMessage(chatId: Int, messageId: Int): Boolean =
        getMessagesList(chatId)
            .find { it.id == messageId }
            ?.let {
                if (!it.isRead && it.peerId == currentUser.id) {
                    it.isRead = true
                    true
                } else false
            } ?: throw MessageNotFoundException("Message #$messageId does not exist")


    fun deleteMessage(chatId: Int, messageId: Int): Boolean =
        getMessagesList(chatId)
            .removeIf { it.id == messageId }
            .also { if (!it) throw MessageNotFoundException("Message #$messageId does not exist") }


    private fun getMessagesList(chatId: Int): MutableList<Message> =
        chats.find { it.id == chatId }?.messages
            ?: throw ChatNotFoundException("Chat #$chatId does not exist")


    fun getChats(): List<Chat> = chats

    fun getUnreadChatsCount(): Int =
        chats.filter { it.containsUnread(currentUser.id) }.size


    fun delete(chatId: Int): Boolean =
        chats
            .removeIf { it.id == chatId }
            .also { if (!it) throw ChatNotFoundException("Chat #$chatId does not exist") }


    fun getLastMessages(): List<String> =
        chats.getLastMessages()


    fun getMessages(userId: Int, msgQuantity: Int): List<String> =
        chats.getByUserId(userId)
            ?.messages
            ?.asReversed()
            ?.asSequence()
            ?.take(msgQuantity)
            ?.onEach { it.isRead = true }
            ?.map { it.text }
            ?.toList()
            ?: throw ChatNotFoundException("Chat with user #$userId does not exist")
}

data class MessageChatIds(
    val chatId: Int,
    val messageId: Int
)