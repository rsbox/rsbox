package io.rsbox.engine.model.entity.update

class UpdateState(val container: UpdateContainer) {

    var teleported: Boolean = false

    var forceChatText = ""

    var faceAngle = 0

    var faceEntityIndex = -1

    /*
     * Animation fields.
     */

    var animation = 0

    var animationDelay = 0

    /*
     * Graphic fields
     */
    var graphic = 0

    var graphicHeight = 0

    var graphicDelay = 0
}