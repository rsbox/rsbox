package io.rsbox.engine.model.`interface`

import io.rsbox.engine.model.entity.Client

class InterfaceManager(val client: Client) {

    private val displayMode: DisplayMode get() = client.displayMode
    private val interfaces = hashMapOf<Int, Int>()
    private var activeModal: Int = -1

    fun openInterface(parent: Int, child: Int, interfaceId: Int) {
        val hash = (parent shl 16) or child
        if(interfaces.containsKey(hash)) {
            //closeInterface(hash)
        }
        interfaces[hash] = interfaceId
    }
}