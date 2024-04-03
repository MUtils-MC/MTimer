package de.miraculixx.mtimer.commands

import de.miraculixx.mtimer.gui.actions.ActionOverview
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.content.ItemsOverview
import de.miraculixx.mtimer.module.FabricTimer
import de.miraculixx.mtimer.server
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mutils.gui.utils.native
import de.miraculixx.mvanilla.extensions.soundDisable
import de.miraculixx.mvanilla.extensions.soundEnable
import de.miraculixx.mvanilla.messages.*
import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.entity.player.Player
import net.silkmc.silk.commands.LiteralCommandBuilder
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.broadcastText
import kotlin.time.Duration

object TimerCommand {
    val globalTimer = command("timer") {
        runs {
            val player = source.player
            if (player == null) {
                source.sendMessage(prefix + msg("command.noPlayer"))
                return@runs
            }
            openSetup(player, false, source)
        }
        resume(false)
        pause(false)
        reset(false)
    }

    val privatTimer = command("ptimer") {
        runs {
            val player = source.player
            if (player == null) {
                source.sendMessage(prefix + msg("command.noPlayer"))
                return@runs
            }
            openSetup(player, true, source)
        }
        resume(true)
        pause(true)
        reset(true)
    }

    private fun openSetup(player: Player, isPersonal: Boolean, sourceStack: CommandSourceStack) {
        val id = if (isPersonal) "${player.stringUUID}-OVERVIEW" else "TIMER_OVERVIEW"
        TimerGUI.OVERVIEW.buildInventory(player, id, ItemsOverview(getTimer(sourceStack, isPersonal), isPersonal), ActionOverview(isPersonal))
    }

    private fun getTimer(sender: CommandSourceStack, isPersonal: Boolean): Timer {
        val timer = if (isPersonal && (sender.player != null)) TimerManager.getPersonalTimer(sender.player!!.uuid) else TimerManager.globalTimer
        return if (timer == null) {
            val player = sender.player!!
            if (debug) consoleAudience.sendMessage(prefix + cmp("Creating new personal timer for ${sender.displayName}"))
            val newTimer = FabricTimer(true, player.uuid, null, playerList = server.playerList)
            newTimer.design = TimerManager.globalTimer.design
            newTimer.visible = false
            TimerManager.addPersonalTimer(player.uuid, newTimer)
            newTimer
        } else timer
    }

    private fun LiteralCommandBuilder<CommandSourceStack>.reset(isPersonal: Boolean) {
        literal("reset") {
            runs {
                val timer = getTimer(source, isPersonal)
                timer.running = false
                timer.time = Duration.ZERO
                source.soundDisable()
                source.sendMessage(prefix + msg("command.reset"))
            }
        }
    }

    private fun LiteralCommandBuilder<CommandSourceStack>.pause(isPersonal: Boolean) {
        literal("pause") {
            runs {
                val timer = getTimer(source, isPersonal)
                if (!timer.running) {
                    source.sendMessage(prefix + msg("command.alreadyOff"))
                } else {
                    timer.running = false
                    source.soundDisable()
                    val msg = prefix + msg("command.pause", listOf(source.textName))
                    if (isPersonal) source.sendMessage(msg)
                    else server.broadcastText(msg.native())
                }
            }
        }
    }

    private fun LiteralCommandBuilder<CommandSourceStack>.resume(isPersonal: Boolean) {
        literal("resume") {
            runs {
                val timer = getTimer(source, isPersonal)
                if (timer.running) {
                    source.sendMessage(prefix + msg("command.alreadyOn"))
                } else {
                    timer.running = true
                    source.soundEnable()
                    val msg = prefix + msg("command.resume", listOf(source.textName))
                    if (isPersonal) source.sendMessage(msg)
                    else server.broadcastText(msg.native())
                }
            }
        }
    }
}