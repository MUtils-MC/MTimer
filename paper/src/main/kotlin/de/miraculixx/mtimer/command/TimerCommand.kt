@file:Suppress("unused")

package de.miraculixx.mtimer.command

import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mcommons.debug
import de.miraculixx.mcommons.extensions.soundDisable
import de.miraculixx.mcommons.extensions.soundEnable
import de.miraculixx.mcommons.text.*
import de.miraculixx.mtimer.MTimer
import de.miraculixx.mtimer.gui.actions.GUIOverview
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.items.ItemsOverview
import de.miraculixx.mtimer.module.ChallengeSync
import de.miraculixx.mtimer.module.PaperTimer
import de.miraculixx.mtimer.module.load
import de.miraculixx.mtimer.data.TimerGUI
import de.miraculixx.mtimer.module.Timer
import de.miraculixx.mtimer.module.TimerManager
import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import kotlin.time.Duration

class TimerCommand {
    private val langFolder = File(MTimer.configFolder, "language")
    private val global = commandTree("timer") {
        withPermission("mutils.command.timer")
        playerExecutor { player, _ -> openSetup(player, false) }

        literalArgument("setup") {
            playerExecutor { player, _ -> openSetup(player, false) }
            literalArgument("visibility") {
                booleanArgument("visible") {
                    anyExecutor { player, args ->
                        val timer = TimerManager.globalTimer
                        timer.visible = args[0] as Boolean
                        player.sendMessage(prefix + cmp("Changed visibility to ${timer.visible}"))
                        onlinePlayers.forEach { it.hideBossBar(timer.bossBar); it.sendActionBar(cmp(" ")) }
                    }
                }
            }
        }

        literalArgument("start") {
            anyExecutor { sender, _ ->
                ChallengeSync.connect()
                sender.resume(false)
            }
        }

        literalArgument("pause") {
            anyExecutor { sender, _ -> sender.pause(false) }
        }

        literalArgument("reset") {
            anyExecutor { sender, _ -> sender.reset(false) }
        }

        literalArgument("config") {
            withPermission("mutils.command.timer-config")
            literalArgument("save") {
                anyExecutor { sender, _ ->
                    TimerManager.save(MTimer.configFolder)
                    sender.sendMessage(prefix + cmp("Saved all temporary data to disk"))
                }
            }
            literalArgument("load") {
                anyExecutor { sender, _ ->
                    TimerManager.load(MTimer.configFolder)
                    sender.sendMessage(prefix + cmp("Reloaded all temporary data from disk"))
                }
            }
        }

        literalArgument("time") {
            literalArgument("set") {
                greedyStringArgument("time") {
                    anyExecutor { sender, args -> sender.setTime(false, args[0] as String, false) }
                }
            }
            literalArgument("add") {
                greedyStringArgument("time") {
                    anyExecutor { sender, args -> sender.setTime(false, args[0] as String, true) }
                }
            }
            literalArgument("get") {
                anyResultingExecutor { sender, _ ->
                    val timer = getTimer(sender, false)
                    sender.sendMessage(prefix + cmp("Current time: ${timer.time}"))
                    return@anyResultingExecutor timer.time.inWholeSeconds.toInt()
                }
            }
        }
    }

    private val personal = commandTree("ptimer") {
        withPermission("mutils.command.ptimer")
        playerExecutor { player, _ -> openSetup(player, true) }
        literalArgument("setup") {
            playerExecutor { player, _ -> openSetup(player, true) }
            entitySelectorArgumentOnePlayer("target") {
                withPermission("mutils.command.ptimer-others")
                playerExecutor { player, args ->
                    val target = args[0] as Player
                    openSetup(player, true, target)
                }

                literalArgument("visibility") {
                    booleanArgument("visible") {
                        anyExecutor { player, args ->
                            val target = args[0] as Player
                            val timer = getTimer(target, true)
                            timer.visible = args[1] as Boolean
                            player.sendMessage(prefix + cmp("Changed visibility of ${target.name} to ${timer.visible}"))
                        }
                    }
                }
            }
        }

        literalArgument("start") {
            playerExecutor { sender, _ -> sender.resume(true) }
            entitySelectorArgumentOnePlayer("target") {
                withPermission("mutils.command.ptimer-others")
                anyExecutor { sender, args ->
                    val target = (args[0] as Player).resume(true)
                }
            }
        }

        literalArgument("pause") {
            playerExecutor { sender, _ -> sender.pause(true) }
            entitySelectorArgumentOnePlayer("target") {
                withPermission("mutils.command.ptimer-others")
                anyExecutor { sender, args ->
                    val target = (args[0] as Player).pause(true)
                }
            }
        }

        literalArgument("reset") {
            playerExecutor { sender, _ -> sender.reset(true) }
            entitySelectorArgumentOnePlayer("target") {
                withPermission("mutils.command.ptimer-others")
                anyExecutor { sender, args ->
                    val target = (args[0] as Player).reset(true)
                }
            }
        }

        literalArgument("time") {
            literalArgument("set") {
                stringArgument("time") {
                    anyExecutor { sender, args -> sender.setTime(true, args[0] as String, false) }
                    entitySelectorArgumentOnePlayer("target") {
                        withPermission("mutils.command.ptimer-others")
                        anyExecutor { sender, args ->
                            (args[0] as Player).setTime(true, args[1] as String, false)
                        }
                    }
                }
            }
            literalArgument("add") {
                stringArgument("time") {
                    anyExecutor { sender, args -> sender.setTime(true, args[0] as String, true) }
                    entitySelectorArgumentOnePlayer("target") {
                        withPermission("mutils.command.ptimer-others")
                        anyExecutor { sender, args ->
                            (args[0] as Player).setTime(true, args[1] as String, true)
                        }
                    }
                }
            }
            literalArgument("get") {
                anyResultingExecutor { sender, _ ->
                    val timer = getTimer(sender, true)
                    sender.sendMessage(prefix + cmp("Current time: ${timer.time}"))
                    return@anyResultingExecutor timer.time.inWholeSeconds.toInt()
                }
                entitySelectorArgumentOnePlayer("target") {
                    withPermission("mutils.command.ptimer-others")
                    anyResultingExecutor { sender, args ->
                        val target = args[0] as Player
                        val timer = getTimer(target, true)
                        sender.sendMessage(prefix + cmp("Current time of ${target.name}: ${timer.time}"))
                        return@anyResultingExecutor timer.time.inWholeSeconds.toInt()
                    }
                }
            }
        }
    }

    private fun CommandSender.setTime(isPersonal: Boolean, string: String, relative: Boolean) {
        val timer = getTimer(this, isPersonal)
        val time = try {
            Duration.parse(string)
        } catch (_: IllegalArgumentException) {
            sendMessage(prefix + cmp("Please enter a value like '5m 3s' (valid times: s,m,h,d). Negative and floating numbers are allowed", cError))
            return
        }
        if (relative) timer.time += time else timer.time = time
        soundEnable()
        sendMessage(prefix + cmp("Changed time to ${timer.time}", cSuccess))
    }

    private fun CommandSender.reset(isPersonal: Boolean) {
        val timer = getTimer(this, isPersonal)
        timer.running = false
        timer.time = Duration.ZERO
        soundDisable()
        sendMessage(prefix + msg("command.reset"))
    }

    private fun CommandSender.pause(isPersonal: Boolean) {
        val timer = getTimer(this, isPersonal)
        val running = timer.running
        if (!running) {
            sendMessage(prefix + msg("command.alreadyOff"))
            return
        } else {
            timer.running = false
            soundDisable()
            val msg = prefix + msg("command.pause", listOf(name))
            if (isPersonal) sendMessage(msg) else broadcast(msg)
        }
    }

    private fun CommandSender.resume(isPersonal: Boolean) {
        val timer = getTimer(this, isPersonal)
        val running = timer.running
        if (running) sendMessage(prefix + msg("command.alreadyOn"))
        else {
            timer.running = true
            soundEnable()
            val msg = prefix + msg("command.resume", listOf(name))
            if (isPersonal) sendMessage(msg) else broadcast(msg)
        }
    }

    private fun openSetup(player: Player, isPersonal: Boolean, target: Player = player) {
        val id = if (isPersonal) target.uniqueId.toString() else "TIMER_GLOBAL"
        val timer = getTimer(target, isPersonal)
        TimerGUI.OVERVIEW.buildInventory(
            player,
            id,
            ItemsOverview(timer, isPersonal, player.language()),
            GUIOverview(timer, isPersonal)
        )
    }

    private fun getTimer(sender: CommandSender, isPersonal: Boolean): Timer {
        val timer =
            if (isPersonal && sender is Player) TimerManager.getPersonalTimer(sender.uniqueId) else TimerManager.globalTimer
        return if (timer == null) {
            if (debug) consoleAudience.sendMessage(prefix + cmp("Creating new personal timer for ${sender.name}"))
            val newTimer = PaperTimer(true, (sender as Player).uniqueId, null)
            newTimer.design = TimerManager.globalTimer.design
            newTimer.visible = false
            TimerManager.addPersonalTimer(sender.uniqueId, newTimer)
            newTimer
        } else timer
    }
}