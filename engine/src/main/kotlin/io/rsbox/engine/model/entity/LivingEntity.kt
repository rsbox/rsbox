package io.rsbox.engine.model.entity

import io.rsbox.engine.model.Direction
import io.rsbox.engine.model.entity.update.UpdateContainer

abstract class LivingEntity : Entity() {

    val updates = UpdateContainer()

    var facingDirection: Direction = Direction.SOUTH

}