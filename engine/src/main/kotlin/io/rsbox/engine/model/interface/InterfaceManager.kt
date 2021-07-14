package io.rsbox.engine.model.`interface`

import io.rsbox.engine.model.`interface`.TopInterfaceType.Companion.child
import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.net.packet.outbound.IfOpenSub
import io.rsbox.engine.net.packet.outbound.IfOpenTop
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap

class InterfaceManager(val client: Client) {

    private val interfaces = Int2IntOpenHashMap()
    private var currentModal = -1
    private var currentDisplayMode = client.displayMode

    private fun open(parent: Int, child: Int, interfaceId: Int) {
        val hash = (parent shl 16) or child
        if(interfaces.containsKey(hash)) {
            /*
             * Close the interface
             */
            closeByHash(hash)
        }

        interfaces[hash] = interfaceId
    }

    private fun closeByHash(hash: Int): Int {
        val found = interfaces.remove(hash)
        if(found != interfaces.defaultReturnValue()) {
            return found
        }
        return -1
    }

    private fun close(parent: Int): Int {
        val found = interfaces.filterValues { it == parent }.keys.firstOrNull()
        if(found != null) {
            closeByHash(found)
            return found
        }
        return -1
    }

    private fun close(parent: Int, child: Int): Int = closeByHash((parent shl 16) or child)

    private fun openModal(parent: Int, child: Int, interfaceId: Int) {
        open(parent, child, interfaceId)
        currentModal = interfaceId
    }

    private fun isOccupied(parent: Int, child: Int): Boolean = interfaces.containsKey((parent shl 16) or child)

    private fun isVisible(interfaceId: Int): Boolean = interfaces.values.contains(interfaceId)

    private fun setVisible(parent: Int, child: Int, visible: Boolean) {
        val hash = (parent shl 16) or child
        if(visible) {
            this.interfaces[hash] = parent
        } else {
            this.interfaces.remove(hash)
        }
    }

    private fun getInterfaceAt(parent: Int, child: Int): Int = interfaces.getOrDefault((parent shl 16) or child, -1)

    /**
     * API Available Methods for managing interfaces for the player's client.
     */

    fun openTopInterface() {
        if(client.displayMode != currentDisplayMode) {
            setVisible(currentDisplayMode.component, TopInterfaceType.MAIN_SCREEN.child(currentDisplayMode), false)
        }
        setVisible(client.displayMode.component, 0, true)
        client.write(IfOpenTop(client.displayMode.component))
    }

    fun openInterface(parent: Int, child: Int, interfaceId: Int, type: Int = 0, isModal: Boolean = false) {
        if(isModal) {
            openModal(parent, child, interfaceId)
        } else {
            open(parent, child, interfaceId)
        }
        client.write(IfOpenSub(parent, child, interfaceId, type))
    }

    fun openInterface(interfaceId: Int, top: TopInterfaceType, fullscreen: Boolean = false) {
        val displayMode = when {
            !fullscreen || top.fullscreenChild == -1 -> currentDisplayMode
            else -> DisplayMode.FULLSCREEN
        }

        val child = top.child(displayMode)
        val parent = displayMode.component

        if(displayMode == DisplayMode.FULLSCREEN) {
            openTopInterface()
        }

        openInterface(parent, child, interfaceId, type = if(top.clickThrough) 1 else 0, isModal = top == TopInterfaceType.MAIN_SCREEN)
    }

    fun openInterface(top: TopInterfaceType, autoClose: Boolean = false) {
        val displayMode = when {
            !autoClose || top.fullscreenChild == -1 -> currentDisplayMode
            else -> DisplayMode.FULLSCREEN
        }

        val child = top.child(displayMode)
        val parent = displayMode.component

        if(displayMode == DisplayMode.FULLSCREEN) {
            openTopInterface()
        }

        openInterface(parent, child, top.interfaceId, type = if(top.clickThrough) 1 else 0, isModal = top == TopInterfaceType.MAIN_SCREEN)
    }
}