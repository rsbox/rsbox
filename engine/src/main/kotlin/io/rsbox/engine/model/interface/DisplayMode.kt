package io.rsbox.engine.model.`interface`

enum class DisplayMode(val id: Int, val component: Int) {

    FIXED(0, 548),

    RESIZABLE_NORMAL(1, 161),

    RESIZABLE_LIST(2, 164),

    MOBILE(3, 601),

    FULLSCREEN(4, 165);

    fun isResizable(): Boolean = this == RESIZABLE_NORMAL || this == RESIZABLE_LIST

    companion object {
        val values = enumValues<DisplayMode>()

        fun fromId(id: Int): DisplayMode {
            return values.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Unknown display mode with ID: $id.")
        }
    }
}