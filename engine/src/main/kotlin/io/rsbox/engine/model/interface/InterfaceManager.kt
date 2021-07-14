package io.rsbox.engine.model.`interface`

import io.rsbox.engine.model.`interface`.TopInterfaceType.Companion.child
import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.net.packet.outbound.IfOpenSub
import io.rsbox.engine.net.packet.outbound.IfOpenTop

class InterfaceManager(val client: Client) {

    private val displayMode: DisplayMode get() = client.displayMode
    private val interfaces = hashMapOf<Int, Int>()

    private var modal: Int = -1

    private fun open(parent: Int, child: Int, interfaceId: Int) {
        val hash = (parent shl 16) or child
        if(interfaces.containsKey(hash)) {
            close(hash)
        }
        interfaces[hash] = interfaceId
    }

    private fun close(hash: Int): Int {
        return interfaces.remove(hash) ?: -1
    }

    private fun close(parent: Int, child: Int) = close((parent shl 16) or child)

    private fun openModal(parent: Int, child: Int, interfaceId: Int) {
        open(parent, child, interfaceId)
        modal = interfaceId
    }

    private fun isOccupied(parent: Int, child: Int): Boolean = interfaces.containsKey((parent shl 16) or child)

    private fun isVisible(interfaceId: Int): Boolean = interfaces.values.contains(interfaceId)

    private fun setVisible(parent: Int, child: Int, visible: Boolean) {
        val hash = (parent shl 16) or child
        if(visible) {
            interfaces[hash] = parent
        } else {
            interfaces.remove(hash)
        }
    }

    private fun getInterfaceAt(parent: Int, child: Int): Int = interfaces.getOrDefault((parent shl 16) or child, -1)

    fun openTopInterface(displayMode: DisplayMode) {
        val component = displayMode.component
        setVisible(component, 0, true)
        client.write(IfOpenTop(component))
    }

    fun openInterface(parent: Int, child: Int, interfaceId: Int, type: Int = 0, isModal: Boolean = false) {
        if(isModal) {
            openModal(parent, child, interfaceId)
        } else {
            open(parent, child, interfaceId)
        }
        client.write(IfOpenSub(parent, child, interfaceId, type))
    }

    fun openInterface(interfaceId: Int, root: TopInterfaceType, fullscreen: Boolean = false) {
        val displayMode = if(!fullscreen || root.fullscreenChild == -1) client.displayMode else DisplayMode.FULLSCREEN
        val child = root.child(displayMode)
        val parent = displayMode.component
        if(displayMode == DisplayMode.FULLSCREEN) {
            openTopInterface(displayMode)
        }
        openInterface(parent, child, interfaceId, 0, isModal = root == TopInterfaceType.MAIN_SCREEN)
    }
}