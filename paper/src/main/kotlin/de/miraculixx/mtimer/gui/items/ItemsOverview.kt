package de.miraculixx.mtimer.gui.items

import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcommons.text.*
import de.miraculixx.mtimer.vanilla.module.Timer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.Locale

class ItemsOverview(private val timer: Timer, private val isPersonal: Boolean, private val locale: Locale) : ItemProvider {
    private val msgActive = cmp(locale.msgString("event.active"), cSuccess)
    private val msgDisabled = cmp(locale.msgString("event.disabled"), cError)

    override fun getSlotMap(): Map<Int, ItemStack> {
        val time = timer.buildSimple()
        val setupLore = listOf(
            emptyComponent(),
            cmp("∙ ") + cmp("Settings", cHighlight, underlined = true),
            cmp("   ${locale.msgString("items.time")}: ") + cmp(time, cHighlight),
            cmp("   ${locale.msgString("items.isActive.s")}: ") + cmp(timer.visible.toString(), cHighlight),
            cmp("   ${locale.msgString("items.direction.s")}: ") + cmp(timer.countUp.toString(), cHighlight),
            emptyComponent()
        )

        return mapOf(
            10 to itemStack(Material.SUNFLOWER) {
                meta {
                    customModel = 1
                    name = cmp(locale.msgString("event.seconds"), cHighlight)
                    lore(timeLore("s", time))
                }
            },
            11 to itemStack(Material.GOLD_INGOT) {
                meta {
                    customModel = 2
                    name = cmp(locale.msgString("event.minutes"), cHighlight)
                    lore(timeLore("min", time))
                }
            },
            19 to itemStack(Material.RAW_GOLD) {
                meta {
                    customModel = 3
                    name = cmp(locale.msgString("event.hours"), cHighlight)
                    lore(timeLore("h", time))
                }
            },
            20 to itemStack(Material.GOLD_BLOCK) {
                meta {
                    customModel = 4
                    name = cmp(locale.msgString("event.days"), cHighlight)
                    lore(timeLore("d", time))
                }
            },
            15 to itemStack(Material.CLOCK) {
                meta {
                    customModel = 7
                    name = cmp(locale.msgString("items.isActive.n"), cHighlight)
                    lore(buildList {
                        addAll(locale.msgList("items.isActive.l", inline = "<grey>"))
                        addAll(setupLore)
                        add(locale.msgClick() + cmp("Toggle"))
                    })
                }
            },
            15 + 9 to itemStack(if (timer.visible) Material.LIME_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE) {
                meta {
                    customModel = 7
                    name = if (timer.visible) msgActive else msgDisabled
                }
            },
            16 to itemStack(Material.MAGENTA_GLAZED_TERRACOTTA) {
                meta {
                    customModel = 6
                    name = cmp(locale.msgString("items.direction.n"), cHighlight)
                    lore(buildList {
                        addAll(locale.msgList("items.direction.l", inline = "<grey>"))
                        addAll(setupLore)
                        add(locale.msgClick() + cmp("Toggle"))
                    })
                }
            },
            16 + 9 to itemStack(if (timer.countUp) Material.LIME_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE) {
                meta {
                    customModel = 6
                    name = if (timer.countUp) cmp("Count Up", cSuccess) else cmp("Count Down", cError)
                }
            },
            13 to itemStack(Material.NAME_TAG) {
                meta {
                    customModel = 5
                    name = cmp(locale.msgString("items.design.n"), cHighlight)
                    lore(buildList {
                        addAll(locale.msgList("items.design.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(cmp("∙ ") + cmp("Current Design", cHighlight, underlined = true))
                        add(cmp("   ∙ ", NamedTextColor.DARK_GRAY) + timer.buildFormatted(true))
                        add(emptyComponent())
                        add(locale.msgClick() + cmp("Open Menu"))
                    })
                }
            }
        ).plus(buildMap {
            if (!isPersonal) {
                put(14, itemStack(Material.SHEARS) {
                    meta {
                        customModel = 8
                        name = cmp(locale.msgString("items.rules.n"), cHighlight)
                        lore(buildList {
                            addAll(locale.msgList("items.rules.l", inline = "<grey>"))
                            addAll(setupLore)
                            add(locale.msgClick() + cmp("Open Menu"))
                        })
                    }
                })
                put(23, itemStack(Material.DRAGON_EGG) {
                    meta {
                        customModel = 9
                        name = cmp(locale.msgString("items.goals.n"), cHighlight)
                        lore(buildList {
                            addAll(locale.msgList("items.goals.l", inline = "<grey>"))
                            addAll(setupLore)
                            add(locale.msgClick() + cmp("Open Menu"))
                        })
                    }
                })
            }
        })
    }

    private fun timeLore(change: String, time: String): List<Component> {
        return listOf(
            cmp(">> ") + cmp(time, cMark),
            emptyComponent(),
            cmp("1$change", cHighlight) + cmp(" • Left-Click"),
            cmp("10$change", cHighlight) + cmp(" • Shift-Left-Click"),
            cmp("                            ", NamedTextColor.DARK_GRAY, strikethrough = true),
            cmp("-1$change", cHighlight) + cmp(" • Right-Click"),
            cmp("-10$change", cHighlight) + cmp(" • Shift-Right-Click")
        )
    }
}