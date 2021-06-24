package io.rsbox.engine.model.`interface`

enum class InterfaceContainer(
    val interfaceId: Int,
    val fixedChildId: Int,
    val resizeChildId: Int,
    val resizeListChildId: Int,
    val mobileChildId: Int,
    val fullscreenChildId: Int = -1,
    val clickThrough: Boolean = true
) {
    CHAT_BOX(interfaceId = 162, fixedChildId = 27, resizeChildId = 32, resizeListChildId = 31, mobileChildId = 33, fullscreenChildId = 1),
    USERNAME(interfaceId = 163, fixedChildId = 20, resizeChildId = 11, resizeListChildId = 9, mobileChildId = 13, fullscreenChildId = 25),
    MINI_MAP(interfaceId = 160, fixedChildId = 11, resizeChildId = 31, resizeListChildId = 28, mobileChildId = 27, fullscreenChildId = 26),
    XP_COUNTER(interfaceId = 122, fixedChildId = 18, resizeChildId = 8, resizeListChildId = 7, mobileChildId = 9, fullscreenChildId = 6),
    INVENTORY(interfaceId = 149, fixedChildId = 72, resizeChildId = 74, resizeListChildId = 71, mobileChildId = 76, fullscreenChildId = 13),
    SKILLS(interfaceId = 320, fixedChildId = 70, resizeChildId = 72, resizeListChildId = 69, mobileChildId = 74, fullscreenChildId = 11),
    QUEST_ROOT(interfaceId = 629, fixedChildId = 71, resizeChildId = 73, resizeListChildId = 70, mobileChildId = 75, fullscreenChildId = 12),
    EQUIPMENT(interfaceId = 387, fixedChildId = 73, resizeChildId = 75, resizeListChildId = 72, mobileChildId = 77, fullscreenChildId = 14),
    PRAYER(interfaceId = 541, fixedChildId = 74, resizeChildId = 76, resizeListChildId = 73, mobileChildId = 78, fullscreenChildId = 15),
    MAGIC(interfaceId = 218, fixedChildId = 75, resizeChildId = 77, resizeListChildId = 74, mobileChildId = 79, fullscreenChildId = 16),
    ACCOUNT_MANAGEMENT(interfaceId = 109, fixedChildId = 77, resizeChildId = 79, resizeListChildId = 79, mobileChildId = 81, fullscreenChildId = 18),
    SOCIAL(interfaceId = 429, fixedChildId = 78, resizeChildId = 80, resizeListChildId = 77, mobileChildId = 82, fullscreenChildId = 19), // 432 = ignore
    LOG_OUT(interfaceId = 182, fixedChildId = 79, resizeChildId = 81, resizeListChildId = 78, mobileChildId = 83, fullscreenChildId = 20),
    SETTINGS(interfaceId = 116, fixedChildId = 80, resizeChildId = 82, resizeListChildId = 79, mobileChildId = 84, fullscreenChildId = 21),
    EMOTES(interfaceId = 216, fixedChildId = 81, resizeChildId = 83, resizeListChildId = 80, mobileChildId = 85, fullscreenChildId = 22),
    MUSIC(interfaceId = 239, fixedChildId = 82, resizeChildId = 84, resizeListChildId = 81, mobileChildId = 86, fullscreenChildId = 23),
    CLAN_CHAT(interfaceId = 7, fixedChildId = 76, resizeChildId = 78, resizeListChildId = 75, mobileChildId = 80, fullscreenChildId = 17),
    ATTACK(interfaceId = 593, fixedChildId = 69, resizeChildId = 71, resizeListChildId = 68, mobileChildId = 73, fullscreenChildId = 10),
    PVP_OVERLAY(interfaceId = -1, fixedChildId = 15, resizeChildId = 4, resizeListChildId = 4, mobileChildId = 5, fullscreenChildId = 1),
    MAIN_SCREEN(interfaceId = -1, fixedChildId = 21, resizeChildId = 13, resizeListChildId = 13, mobileChildId = 17, fullscreenChildId = 1, clickThrough = false),
    TAB_AREA(interfaceId = -1, fixedChildId = 64, resizeChildId = 66, resizeListChildId = 66, mobileChildId = 71, clickThrough = false),
    WALKABLE(interfaceId = -1, fixedChildId = 14, resizeChildId = 3, resizeListChildId = 3, mobileChildId = 4),
    WORLD_MAP(interfaceId = -1, fixedChildId = 22, resizeChildId = 14, resizeListChildId = 14, mobileChildId = 18, fullscreenChildId = 28);
    //WORLD_MAP_FULL(interfaceId = -1, fixedChildId = 27, resizeChildId = 27, resizeListChildId = 27, mobileChildId = 27,//  fullscreenChildId = 27, clickThrough = false);

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
        PVP_OVERLAY,
        USERNAME,
        MINI_MAP,
        XP_COUNTER,
        WORLD_MAP -> true
    }

    companion object {
        val values = enumValues<InterfaceContainer>()
    }

}