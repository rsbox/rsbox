package io.rsbox.engine.model

class Tile(val position: Position) {

    constructor(x: Int, y: Int, level: Int = 0) : this(Position(x, y, level))

}