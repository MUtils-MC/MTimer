package de.miraculixx.mtimer

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.main.KPaper
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.mcommons.debug
import de.miraculixx.mcommons.extensions.loadConfig
import de.miraculixx.mcommons.majorVersion
import de.miraculixx.mcommons.minorVersion
import de.miraculixx.mcommons.text.*
import de.miraculixx.mtimer.command.HelperCommand
import de.miraculixx.mtimer.command.TimerCommand
import de.miraculixx.mtimer.module.ChallengeSync
import de.miraculixx.mtimer.module.GlobalListener
import de.miraculixx.mtimer.module.TimerAPI
import de.miraculixx.mtimer.module.load
import de.miraculixx.mtimer.vanilla.data.Settings
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.settings
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class MTimer : KPaper() {
    companion object {
        lateinit var INSTANCE: KPaper
        val configFolder = File("plugins/MUtils/Timer")
        lateinit var localization: Localization
        //lateinit var bridgeAPI: MUtilsBridge
    }

    private val configFile = File("${configFolder.path}/settings.json")

    override fun load() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true).beLenientForMinorVersions(true))
    }

    override fun startup() {
        INSTANCE = this
        consoleAudience = console
        debug = false

        CommandAPI.onEnable()
        val versionSplit = server.minecraftVersion.split('.')
        majorVersion = versionSplit.getOrNull(1)?.toIntOrNull() ?: 0
        minorVersion = versionSplit.getOrNull(2)?.toIntOrNull() ?: 0

        if (!configFolder.exists()) configFolder.mkdirs()
        settings = configFile.loadConfig(Settings())
        val languages = listOf(Locale.ENGLISH, Locale.GERMAN, Locale.forLanguageTag("es")).map { it to javaClass.getResourceAsStream("/language/mtimer/$it.yml") }
        localization = Localization(File("${configFolder.path}/language"), settings.language, languages)
        GlobalListener

        // Connect Bridge
        //bridgeAPI = MUtilsBridge(MUtilsPlatform.PAPER, MUtilsModule.TIMER, server.version, server.port, debug)
        CoroutineScope(Dispatchers.Default).launch {
            //val version = bridgeAPI.versionCheck(description.version.toIntOrNull() ?: 0, File("plugins/update"))

            sync {
                TimerCommand()
                HelperCommand()
            }

            TimerManager.load(configFolder)
            TimerAPI
            ChallengeSync.connect()
        }
    }

    override fun shutdown() {
        CommandAPI.onDisable()
        TimerManager.save(configFolder)
    }
}

val PluginManager by lazy { MTimer.INSTANCE }