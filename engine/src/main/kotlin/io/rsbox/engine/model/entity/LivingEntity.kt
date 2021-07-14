package io.rsbox.engine.model.entity

import io.rsbox.engine.model.entity.update.UpdateStates

abstract class LivingEntity : Entity() {

    val updates = UpdateStates()

}