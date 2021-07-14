package io.rsbox.api.plugin

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

@KotlinScript(
    displayName = "Plugin Script",
    fileExtension = "kts",
    compilationConfiguration = PluginScriptConfiguration::class
)
abstract class PluginScript : Plugin() {

    private var onEnableAction: ((PluginScript) -> Unit)? = null
    private var onDisableAction: ((PluginScript) -> Unit)? = null

    internal val hasEnableAction: Boolean get() = onEnableAction != null
    internal val hasDisableAction: Boolean get() = onDisableAction != null

    internal fun invokeEnable(ctx: PluginScript) { onEnableAction!!.invoke(ctx) }
    internal fun invokeDisable(ctx: PluginScript) { onDisableAction!!.invoke(ctx) }

    fun on_enable(action: (PluginScript) -> Unit) {
        this.onEnableAction = action
    }

    fun on_disable(action: (PluginScript) -> Unit) {
        this.onDisableAction = action
    }
}

object PluginScriptConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        "io.rsbox.api.plugin.Plugin"
    )
})