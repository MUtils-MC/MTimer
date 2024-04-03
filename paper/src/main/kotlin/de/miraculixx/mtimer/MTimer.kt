package de.miraculixx.mtimer

import de.miraculixx.challenge.api.MChallengeAPI
import de.miraculixx.challenge.api.modules.challenges.ChallengeStatus
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.pluginManager
import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.mbridge.MUtilsBridge
import de.miraculixx.mbridge.MUtilsModule
import de.miraculixx.mbridge.MUtilsPlatform
import de.miraculixx.mtimer.command.HelperCommand
import de.miraculixx.mtimer.command.TimerCommand
import de.miraculixx.mtimer.module.TimerAPI
import de.miraculixx.mtimer.module.load
import de.miraculixx.mtimer.vanilla.data.Settings
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.rules
import de.miraculixx.mtimer.vanilla.module.settings
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mvanilla.messages.*
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import java.io.File

class MTimer : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
        val configFolder = File("plugins/MUtils/Timer")
        lateinit var localization: Localization
        lateinit var bridgeAPI: MUtilsBridge
        var challengeAPI: MChallengeAPI? = null
    }

    override fun load() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true))
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
        settings = json.decodeFromString<Settings>(File("${configFolder.path}/settings.json").readJsonString(true))
        val languages = listOf("en_US", "de_DE", "es_ES").map { it to javaClass.getResourceAsStream("/language/$it.yml") }
        localization = Localization(File("${configFolder.path}/language"), settings.language, languages, timerPrefix)

        // Connect Bridge
        bridgeAPI = MUtilsBridge(MUtilsPlatform.PAPER, MUtilsModule.TIMER, server.version, server.port)
        CoroutineScope(Dispatchers.Default).launch {
            val version = bridgeAPI.versionCheck(description.version.toInt(), File("plugins/update"))
            if (!version) return@launch

            sync {
                TimerCommand()
                HelperCommand()
            }

            TimerManager.load(configFolder)
            TimerAPI
            if (pluginManager.isPluginEnabled("MUtils-Challenge")) {
                challengeAPI = MChallengeAPI.instance
                if (challengeAPI == null) console.sendMessage(prefix + cmp("Failed to load MChallenge API while it's loaded!", cError))
                else {
                    TimerAPI.onStartLogic {
                        if (rules.syncWithChallenge) {
                            when (challengeAPI?.getChallengeStatus()) {
                                ChallengeStatus.PAUSED -> challengeAPI?.resumeChallenges()
                                ChallengeStatus.STOPPED -> challengeAPI?.startChallenges()
                                else -> Unit
                            }
                        }
                    }
                    TimerAPI.onStopLogic {
                        if (rules.syncWithChallenge) {
                            when (challengeAPI?.getChallengeStatus()) {
                                ChallengeStatus.RUNNING -> challengeAPI?.pauseChallenges()
                                else -> Unit
                            }
                        }
                    }
                }
            }
        }
    }

    override fun shutdown() {
        CommandAPI.onDisable()
        TimerManager.save(configFolder)
    }
}

val PluginManager by lazy { MTimer.INSTANCE }