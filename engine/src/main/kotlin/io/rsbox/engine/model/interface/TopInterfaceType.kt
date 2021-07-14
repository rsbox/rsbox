package io.rsbox.engine.model.`interface`

enum class TopInterfaceType(
    val interfaceId: Int,
    val fixedModeChild: Int,
    val resizableNormalChild: Int,
    val resizableListChild: Int,
    val mobileChild: Int,
    val fullscreenChild: Int = -1,
    val clickThrough: Boolean = true
) {
    CHAT_BOX(interfaceId = 162, fixedModeChild = 27, resizableNormalChild = 32, resizableListChild = 31, mobileChild = 33, fullscreenChild = 1),
    USERNAME(interfaceId = 163, fixedModeChild = 20, resizableNormalChild = 11, resizableListChild = 9, mobileChild = 13, fullscreenChild = 25),
    MINI_MAP(interfaceId = 160, fixedModeChild = 11, resizableNormalChild = 31, resizableListChild = 28, mobileChild = 27, fullscreenChild = 26),
    XP_COUNTER(interfaceId = 122, fixedModeChild = 18, resizableNormalChild = 8, resizableListChild = 7, mobileChild = 9, fullscreenChild = 6),
    INVENTORY(interfaceId = 149, fixedModeChild = 72, resizableNormalChild = 74, resizableListChild = 71, mobileChild = 76, fullscreenChild = 13),
    SKILLS(interfaceId = 320, fixedModeChild = 70, resizableNormalChild = 72, resizableListChild = 69, mobileChild = 74, fullscreenChild = 11),
    QUEST_ROOT(interfaceId = 629, fixedModeChild = 71, resizableNormalChild = 73, resizableListChild = 70, mobileChild = 75, fullscreenChild = 12),
    EQUIPMENT(interfaceId = 387, fixedModeChild = 73, resizableNormalChild = 75, resizableListChild = 72, mobileChild = 77, fullscreenChild = 14),
    PRAYER(interfaceId = 541, fixedModeChild = 74, resizableNormalChild = 76, resizableListChild = 73, mobileChild = 78, fullscreenChild = 15),
    MAGIC(interfaceId = 218, fixedModeChild = 75, resizableNormalChild = 77, resizableListChild = 74, mobileChild = 79, fullscreenChild = 16),
    ACCOUNT_MANAGEMENT(interfaceId = 109, fixedModeChild = 77, resizableNormalChild = 79, resizableListChild = 79, mobileChild = 81, fullscreenChild = 18),
    SOCIAL(interfaceId = 429, fixedModeChild = 78, resizableNormalChild = 80, resizableListChild = 77, mobileChild = 82, fullscreenChild = 19), // 432 = ignore
    LOG_OUT(interfaceId = 182, fixedModeChild = 79, resizableNormalChild = 81, resizableListChild = 78, mobileChild = 83, fullscreenChild = 20),
    SETTINGS(interfaceId = 116, fixedModeChild = 80, resizableNormalChild = 82, resizableListChild = 79, mobileChild = 84, fullscreenChild = 21),
    EMOTES(interfaceId = 216, fixedModeChild = 81, resizableNormalChild = 83, resizableListChild = 80, mobileChild = 85, fullscreenChild = 22),
    MUSIC(interfaceId = 239, fixedModeChild = 82, resizableNormalChild = 84, resizableListChild = 81, mobileChild = 86, fullscreenChild = 23),
    CLAN_CHAT(interfaceId = 7, fixedModeChild = 76, resizableNormalChild = 78, resizableListChild = 75, mobileChild = 80, fullscreenChild = 17),
    ATTACK(interfaceId = 593, fixedModeChild = 69, resizableNormalChild = 71, resizableListChild = 68, mobileChild = 73, fullscreenChild = 10),
    MAIN_SCREEN(interfaceId = -1, fixedModeChild = 21, resizableNormalChild = 13, resizableListChild = 13, mobileChild = 17, clickThrough = false),
    TAB_AREA(interfaceId = -1, fixedModeChild = 64, resizableNormalChild = 66, resizableListChild = 66, mobileChild = 71, clickThrough = false),
    WALKABLE(interfaceId = -1, fixedModeChild = 14, resizableNormalChild = 3, resizableListChild = 3, mobileChild = 4),
    WORLD_MAP(interfaceId = -1, fixedModeChild = 22, resizableNormalChild = 14, resizableListChild = 14, mobileChild = 18, fullscreenChild = 28);

    fun isSwitchable(): Boolean = when (this) {
        CHAT_BOX,
        MAIN_SCREEN,
        WALKABLE,
        TAB_AREA,
        ATTACK,
        SKILLS,
        QUEST_ROOT,
        INVENTORY,
        EQUIPMENT,
        PRAYER,
        MAGIC,
        CLAN_CHAT,
        ACCOUNT_MANAGEMENT,
        SOCIAL,
        LOG_OUT,
        SETTINGS,
        EMOTES,
        MUSIC,
        USERNAME,
        MINI_MAP,
        XP_COUNTER,
        WORLD_MAP -> true
        else -> false
    }

    companion object {
        val values = enumValues<TopInterfaceType>()

        fun TopInterfaceType.child(displayMode: DisplayMode): Int = when(displayMode) {
            DisplayMode.FIXED -> this.fixedModeChild
            DisplayMode.RESIZABLE_NORMAL -> this.resizableNormalChild
            DisplayMode.RESIZABLE_LIST -> this.resizableListChild
            DisplayMode.MOBILE -> this.mobileChild
            DisplayMode.FULLSCREEN -> this.fullscreenChild
        }
    }

}