package io.rsbox.engine.model.entity

enum class Gender(val id: Int) {

    MALE(id = 0),

    FEMALE(id = 1);

    companion object {
        val values = enumValues<Gender>()

        fun fromId(id: Int): Gender {
            return values.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Unknown gender with ID: $id.")
        }
    }
}