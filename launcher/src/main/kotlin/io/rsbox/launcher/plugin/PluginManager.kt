package io.rsbox.launcher.plugin

import org.pf4j.CompoundPluginDescriptorFinder
import org.pf4j.DefaultPluginManager
import org.pf4j.ManifestPluginDescriptorFinder
import org.pf4j.PluginDescriptorFinder

class PluginManager : DefaultPluginManager() {

    override fun createPluginDescriptorFinder(): PluginDescriptorFinder {
        return CompoundPluginDescriptorFinder().add(ManifestPluginDescriptorFinder())
    }
}