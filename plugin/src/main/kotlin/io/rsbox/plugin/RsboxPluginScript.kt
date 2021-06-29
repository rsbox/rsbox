@file:Suppress("FunctionName")

package io.rsbox.plugin

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

@KotlinScript(
    fileExtension = "kts",
    compilationConfiguration = RsboxPluginConfiguration::class
)
open class RsboxPluginScript {

}

object RsboxPluginConfiguration : ScriptCompilationConfiguration({
    defaultImports(

    )
})