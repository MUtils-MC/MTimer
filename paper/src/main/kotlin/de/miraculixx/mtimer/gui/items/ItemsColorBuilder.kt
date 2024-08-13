package de.miraculixx.mtimer.gui.items

import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcommons.statics.KHeads
import de.miraculixx.mcommons.text.*
import de.miraculixx.mtimer.vanilla.data.ColorBuilder
import de.miraculixx.mtimer.vanilla.data.ColorType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class ItemsColorBuilder(private val data: ColorBuilder, private val locale: Locale) : ItemProvider {
    private val msgTypeName = cmp(locale.msgString("items.color.type.n"), cHighlight, bold = true)
    private val msgTypeLore = locale.msgList("items.color.type.l", inline = "<grey>")
    private val msgSettings = cmp("∙ ") + cmp("Settings", cHighlight, underlined = true)
    private val msgOutput = cmp("∙ ") + cmp("Output", cHighlight, underlined = true)

    override fun getSlotMap(): Map<Int, ItemStack> {
        val currentColor = data.getColor()
        return buildMap {
            put(11, itemStack(Material.MAGMA_CREAM) {
                meta {
                    name = msgTypeName
                    lore(msgTypeLore + buildLore(currentColor) + (locale.msgClick() + cmp("Switch")))
                    customModel = 1
                }
            })
            when (data.type) {
                ColorType.VANILLA -> {
                    put(13, itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { customModel = 99; emptyComponent() } }) //TODO
                    put(15, itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { customModel = 98; emptyComponent() } })
                    put(14, itemStack(Material.LEATHER_CHESTPLATE) {
                        meta<LeatherArmorMeta> {
                            name = cmp(locale.msgString("items.color.vanilla.n"), cHighlight)
                            lore(buildLore(currentColor) + (locale.msgClick() + cmp("Switch")))
                            customModel = 2
                            setColor(Color.fromRGB(currentColor.value()))
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        }
                    })
                }

                ColorType.RGB -> {
                    val clickLore = listOf(locale.msgClickLeft() + cmp("+1"), locale.msgClickRight() + cmp("-1"), locale.msgShiftClickLeft() + cmp("+10"), locale.msgShiftClickRight() + cmp("-10"))
                    put(13, itemStack(Material.RED_DYE) {
                        meta {
                            name = cmp(locale.msgString("items.color.red.n"), cHighlight)
                            lore(buildLore(currentColor) + clickLore)
                            customModel = 3
                        }
                    })
                    put(14, itemStack(Material.GREEN_DYE) {
                        meta {
                            name = cmp(locale.msgString("items.color.green.n"), cHighlight)
                            lore(buildLore(currentColor) + clickLore)
                            customModel = 4
                        }
                    })
                    put(15, itemStack(Material.BLUE_DYE) {
                        meta {
                            name = cmp(locale.msgString("items.color.blue.n"), cHighlight)
                            lore(buildLore(currentColor) + clickLore)
                            customModel = 5
                        }
                    })
                }

                ColorType.HEX_CODE -> {
                    put(13, itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { customModel = 99; emptyComponent() } })
                    put(15, itemStack(Material.GRAY_STAINED_GLASS_PANE) { meta { customModel = 98; emptyComponent() } })
                    put(14, itemStack(Material.LEATHER_CHESTPLATE) {
                        meta<LeatherArmorMeta> {
                            name = cmp(locale.msgString("items.color.hex.n"), cHighlight)
                            lore(buildLore(currentColor) + (locale.msgClick() + cmp("Enter")))
                            customModel = 6
                            setColor(Color.fromRGB(currentColor.value()))
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE)
                        }
                    })
                }
            }
            put(22, itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(locale.msgString("event.finish"), cHighlight)
                    customModel = 10
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(KHeads.CHECKMARK_GREEN)
            })
        }
    }

    private fun buildLore(color: TextColor): List<Component> {
        return buildList {
            add(emptyComponent())
            add(msgSettings)
            when (data.type) {
                ColorType.RGB -> {
                    add(cmp("   R: ") + cmp("${data.r}", cHighlight))
                    add(cmp("   G: ") + cmp("${data.g}", cHighlight))
                    add(cmp("   B: ") + cmp("${data.b}", cHighlight))
                }

                ColorType.VANILLA -> add(cmp("   Color: ") + cmp(data.input, cHighlight))
                ColorType.HEX_CODE -> add(cmp("   Hex: ") + cmp(data.input, cHighlight))
            }
            add(emptyComponent())
            add(msgOutput)
            add(cmp("   (╯°□°）╯︵ ┻━┻", color))
            add(emptyComponent())
        }
    }
}