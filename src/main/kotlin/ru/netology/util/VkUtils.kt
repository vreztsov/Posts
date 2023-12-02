package ru.netology.util

import ru.netology.entities.*

object VkUtils {

    private var userId = 0
    val USER1 = User(++userId)
    val USER2 = User(++userId)

    fun createNewUser(): User {
        return User(++userId)
    }

    fun findIndexById(entities: Collection<Entity>, entityId: Int): Int? {
        val entity = try {
            entities.first{ it.id == entityId }
        } catch (e: NoSuchElementException){
            return null
        }
        return entities.indexOf(entity)
    }
}

