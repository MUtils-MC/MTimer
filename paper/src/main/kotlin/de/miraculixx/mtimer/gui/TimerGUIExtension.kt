package de.miraculixx.mtimer.gui

import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.data.InventoryManager
import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcommons.statics.KHeads
import de.miraculixx.mcommons.text.*
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta

fun TimerGUI.buildInventory(player: Player, id: String, itemProvider: ItemProvider?, clickAction: GUIEvent) {
    InventoryManager.get(id)?.open(player) ?: when (this) {
        TimerGUI.OVERVIEW -> InventoryManager.inventoryBuilder(id) {
            this.size = 4
            this.title = this@buildInventory.title
            this.player = player
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }

        TimerGUI.DESIGN -> InventoryManager.storageBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
            this.headers = listOf(
                itemStack(Material.PLAYER_HEAD) {
                    meta {
                        name = cmp(defaultLocale.msgString("items.createDesign.n"), cHighlight)
                        lore(defaultLocale.msgList("items.createDesign.l", inline = "<grey>"))
                        customModel = 1
                    }
                    itemMeta = (itemMeta as SkullMeta).skullTexture(KHeads.PLUS_GREEN)
                },
                itemStack(Material.PAINTING) {
                    meta {
                        name = cmp(defaultLocale.msgString("items.changeDisplay.n"), cHighlight)
                        lore(defaultLocale.msgList("items.changeDisplay.l", inline = "<grey>"))
                        customModel = 2
                    }
                }
            )
        }

        TimerGUI.DESIGN_EDITOR, TimerGUI.COLOR -> InventoryManager.inventoryBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.size = 3
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }

        TimerGUI.DESIGN_PART_EDITOR -> InventoryManager.inventoryBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.size = 4
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }

        TimerGUI.GOALS, TimerGUI.RULES -> InventoryManager.scrollBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }
    }
}