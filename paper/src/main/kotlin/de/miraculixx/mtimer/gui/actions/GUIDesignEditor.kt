package de.miraculixx.mtimer.gui.actions

import de.miraculixx.kpaper.await.implementations.AwaitChatMessage
import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.data.CustomInventory
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.async
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.mcommons.extensions.click
import de.miraculixx.mcommons.extensions.soundEnable
import de.miraculixx.mcommons.extensions.soundStone
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.emptyComponent
import de.miraculixx.mcommons.text.msg
import de.miraculixx.mtimer.MTimer
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.items.ItemsDesignPartEditor
import de.miraculixx.mtimer.gui.items.ItemsDesigns
import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.data.TimerGUI
import de.miraculixx.mtimer.module.TimerManager
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.*

class GUIDesignEditor(
    private val design: TimerDesign,
    private val uuid: UUID,
    private val isPersonal: Boolean,
) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem
        val locale = player.language()
        when (item?.itemMeta?.customModel ?: 0) {
            1 -> AwaitChatMessage(player, "design name", 30, design.name, false, cmp("\n"), {
                design.name = if (it.length > 30) it.dropLast(it.length - 30) else it
                player.soundEnable()
            }) {
                TimerGUI.DESIGN_EDITOR.buildInventory(player, player.uniqueId.toString(), inv.itemProvider, this)
            }

            2 -> {
                player.closeInventory()
                player.click()
                TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, player.uniqueId.toString(), ItemsDesignPartEditor(design, uuid, true, locale), GUIDesignPartEditor(design, uuid, true, isPersonal))
            }

            3 -> {
                player.closeInventory()
                player.click()
                TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, player.uniqueId.toString(), ItemsDesignPartEditor(design, uuid, false, locale), GUIDesignPartEditor(design, uuid, false, isPersonal))
            }

            4 -> {
                player.closeInventory()
                player.soundEnable()
                val timer = if (isPersonal) TimerManager.getPersonalTimer(player.uniqueId) ?: return@event else TimerManager.globalTimer
                async { TimerManager.save(MTimer.configFolder) }
                TimerGUI.DESIGN.buildInventory(player, player.uniqueId.toString(), ItemsDesigns(timer, locale), GUIDesigns(isPersonal, timer))
            }

            5 -> {
                when (it.hotbarButton) {
                    0 -> {
                        val entries = BossBar.Color.entries
                        val index = entries.indexOf(design.barColor) + 1
                        design.barColor = if (index >= entries.size) entries[0] else entries[index]
                        player.click()
                        inv.update()
                    }

                    1 -> {
                        AwaitChatMessage(player, "End Sound", 120, design.stopSound.key, false, locale.msg("event.soundEnd"), {
                            design.stopSound.key = it
                            player.soundEnable()
                        }) {
                            sync {
                                inv.update()
                                inv.open(player)
                            }
                        }
                    }

                    2 -> {
                        AwaitChatMessage(player, "End Sound Pitch", 30, design.stopSound.pitch.toString(), false, emptyComponent(), {
                            design.stopSound.pitch = (it.toFloatOrNull() ?: 0f).coerceIn(0f, 2f)
                            player.soundEnable()
                        }) {
                            sync {
                                inv.update()
                                inv.open(player)
                            }
                        }
                    }

                    else -> player.soundStone()
                }
            }
        }
    }
}