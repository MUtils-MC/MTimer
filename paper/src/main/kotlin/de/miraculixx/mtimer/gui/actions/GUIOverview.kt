package de.miraculixx.mtimer.gui.actions

import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.data.CustomInventory
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mcommons.extensions.click
import de.miraculixx.mcommons.extensions.soundDisable
import de.miraculixx.mcommons.extensions.soundEnable
import de.miraculixx.mcommons.text.*
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.items.ItemsDesigns
import de.miraculixx.mtimer.gui.items.ItemsGoals
import de.miraculixx.mtimer.gui.items.ItemsRules
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class GUIOverview(
    private val timer: Timer,
    private val isPersonal: Boolean
) : GUIEvent {
    private val noPersonalTimer = prefix + defaultLocale.msg("event.noPersonalTimer")

    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem

        when (val id = item?.itemMeta?.customModel ?: 0) {
            5 -> {
                player.closeInventory()
                val guiID = if (isPersonal) player.uniqueId.toString() else "TIMER_GLOBAL_DESIGNS"
                player.click()
                TimerGUI.DESIGN.buildInventory(player, guiID, ItemsDesigns(timer, player.language()), GUIDesigns(isPersonal, timer))
            }

            6 -> if (timer.countUp) {
                timer.countUp = false
                player.soundDisable()
            } else {
                timer.countUp = true
                player.soundEnable()
            }

            7 -> if (timer.visible) {
                timer.visible = false
                player.soundDisable()
                player.sendActionBar(emptyComponent())
            } else {
                timer.visible = true
                player.soundEnable()
            }

            8 -> {
                player.closeInventory()
                player.click()
                TimerGUI.RULES.buildInventory(player, player.uniqueId.toString(), ItemsRules(player.language()), GUIRules())
                return@event
            }

            9 -> {
                player.closeInventory()
                player.click()
                TimerGUI.GOALS.buildInventory(player, player.uniqueId.toString(), ItemsGoals(player.language()), GUIGoals())
                return@event
            }

            1, 2, 3, 4 -> {
                // Time Settings
                val timeAdded = when (it.click) {
                    ClickType.LEFT -> {
                        when (id) {
                            1 -> timer.addTime(sec = 1)
                            2 -> timer.addTime(min = 1)
                            3 -> timer.addTime(hour = 1)
                            4 -> timer.addTime(day = 1)
                            else -> false
                        }
                    }

                    ClickType.RIGHT -> {
                        when (id) {
                            1 -> timer.addTime(sec = -1)
                            2 -> timer.addTime(min = -1)
                            3 -> timer.addTime(hour = -1)
                            4 -> timer.addTime(day = -1)
                            else -> false
                        }
                    }

                    ClickType.SHIFT_LEFT -> {
                        when (id) {
                            1 -> timer.addTime(sec = 10)
                            2 -> timer.addTime(min = 10)
                            3 -> timer.addTime(hour = 10)
                            4 -> timer.addTime(day = 10)
                            else -> false
                        }

                    }

                    ClickType.SHIFT_RIGHT -> {
                        when (id) {
                            1 -> timer.addTime(sec = -10)
                            2 -> timer.addTime(min = -10)
                            3 -> timer.addTime(hour = -10)
                            4 -> timer.addTime(day = -10)
                            else -> false
                        }
                    }

                    else -> false
                }

                if (timeAdded) player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1.5f)
                else player.playSound(player, Sound.BLOCK_STONE_FALL, 1f, 1f)
            }

            else -> return@event
        }

        inv.update()
    }
}