package io.rsbox.engine.model.`interface`

enum class DisplayMode(val id: Int) {

    FIXED(0),

    RESIZABLE_NORMAL(1),

    RESIZABLE_LIST(2),

    MOBILE(3),

    FULLSCREEN(4);

    fun isResizable(): Boolean = this == RESIZABLE_NORMAL || this == RESIZABLE_LIST

    companion object {
        val values = enumValues<DisplayMode>()
    }
}