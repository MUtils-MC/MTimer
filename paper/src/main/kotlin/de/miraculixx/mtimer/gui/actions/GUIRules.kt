package de.miraculixx.mtimer.gui.actions

import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.data.CustomInventory
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mcommons.extensions.click
import de.miraculixx.mcommons.extensions.nextValue
import de.miraculixx.mcommons.extensions.soundUp
import de.miraculixx.mcommons.extensions.toggle
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.items.ItemsOverview
import de.miraculixx.mtimer.vanilla.data.Punishment
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.rules
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class GUIRules : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel ?: 0) {
            1 -> rules.announceSeed = rules.announceSeed.toggle(player)
            2 -> rules.announceLocation = rules.announceLocation.toggle(player)
            3 -> rules.specOnDeath = rules.specOnDeath.toggle(player)
            4 -> rules.specOnJoin = rules.specOnJoin.toggle(player)
            5 -> {
                if (it.click.isLeftClick) rules.punishmentSetting.active = rules.punishmentSetting.active.toggle(player)
                else {
                    rules.punishmentSetting.type = Punishment.entries.nextValue(rules.punishmentSetting.type)
                    player.soundUp()
                    inv.update()
                }
            }

            6 -> rules.freezeWorld = rules.freezeWorld.toggle(player)
            7 -> rules.announceBack = rules.announceBack.toggle(player)
            8 -> rules.syncWithChallenge = rules.syncWithChallenge.toggle(player)

            0 -> {
                player.click()
                TimerGUI.OVERVIEW.buildInventory(player, "TIMER_GLOBAL", ItemsOverview(TimerManager.globalTimer, false, player.language()), GUIOverview(TimerManager.globalTimer, false))
                return@event
            }
        }
        inv.update()
    }
}