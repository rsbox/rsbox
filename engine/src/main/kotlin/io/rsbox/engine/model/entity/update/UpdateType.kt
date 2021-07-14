package io.rsbox.engine.model.entity.update

enum class UpdateType(val mask: Int) {

    APPEARANCE(mask = 0x4),

    HIT_MARKERS(mask = 0x2),

    CONTEXT_MENU(mask = 0x800),

    GRAPHIC(mask = 0x200),

    FORCE_CHAT(mask = 0x8),

    PUBLIC_CHAT(mask = 0x40),

    FACE_ENTITY(mask = 0x10),

    MOVEMENT_SPEED(mask = 0x1000),

    ANIMATION(mask = 0x20),

    FORCE_MOVEMENT(mask = 0x2000),

    MOVEMENT(mask = 0x100),

    FACE_TILE(mask = 0x1);

    companion object {

        val values = enumValues<UpdateType>()

        const val EXCESS_MASK = 0x80

    }
}