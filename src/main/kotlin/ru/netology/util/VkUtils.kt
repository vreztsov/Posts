package ru.netology.util

import ru.netology.entities.Entity

object VkUtils {

    fun findIndexById(entities: Collection<Entity>, entityId: Int): Int? {
        for (entity in entities) {
            if (entity.id == entityId) {
                return entities.indexOf(entity)
            }
        }
        return null
    }
}

