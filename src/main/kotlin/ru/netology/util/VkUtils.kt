package ru.netology.util

import ru.netology.entities.*

object VkUtils {

    private var userId = 0
    private const val alphabet = "АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЭЮЯ"
    val USER1 = User(++userId, "Ваня", "Петров")
    val USER2 = User(++userId, "Вася", "Пупкин")

    fun createNewUser(id: Int = ++userId): User {
        return User(id, "Толя", alphabet[userId % (alphabet.length)].toString() + ".")
    }

    fun findIndexById(entities: Collection<Entity>, entityId: Int): Int? {
        val entity = try {
            entities.first { it.id == entityId }
        } catch (e: NoSuchElementException) {
            return null
        }
        return entities.indexOf(entity)
    }

    fun getUserById(id: Int): User {
        return when (id) {
            1 -> USER1
            2 -> USER2
            else -> createNewUser(id)
        }
    }
}

