package de.miraculixx.mtimer.gui.items

import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcommons.extensions.msg
import de.miraculixx.mcommons.statics.KHeads
import de.miraculixx.mcommons.text.*
import de.miraculixx.mtimer.vanilla.data.ColorBuilder
import de.miraculixx.mtimer.vanilla.data.GradientBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class ItemsGradientBuilder(
    private val data: GradientBuilder,
    private val locale: Locale
) : ItemProvider {
    private val msgAnimateName = cmp(locale.msgString("items.color.animate.n"), cHighlight)
    private val msgAnimateLore = locale.msgList("items.color.animate.l", inline = "<grey>")
    private val msgNone = cmp(locale.msgString("common.none"), cError)
    private val msgSettings = cmp("∙ ") + cmp("Settings", cHighlight, underlined = true)
    private val msgOutput = cmp("∙ ") + cmp("Output", cHighlight, underlined = true)

    override fun getSlotMap(): Map<Int, ItemStack> {
        return buildMap {
            put(10, itemStack(Material.ENDER_EYE) {
                meta {
                    name = msgAnimateName
                    lore(
                        msgAnimateLore + listOf(
                            emptyComponent(),
                            msgSettings,
                            cmp("   " + locale.msgString("items.color.animate.s") + ": ") + cmp(this@ItemsGradientBuilder.data.isAnimated.msg(locale))
                        )
                    )
                    customModel = 1
                }
            })
            repeat(5) { index ->
                val current = data.colors.getOrNull(index)
                if (current == null) {
                    put(12 + index, itemStack(Material.STRUCTURE_VOID) {
                        meta {
                            name = msgNone
                            lore(listOf(emptyComponent(), locale.msgClick() + cmp("Add Color")))
                            customModel = 10 + index
                        }
                    })
                } else {
                    put(12 + index, itemStack(Material.LEATHER_CHESTPLATE) {
                        meta<LeatherArmorMeta> {
                            name = cmp("Color ${index + 1}", cHighlight)
                            lore(buildLore(current))
                            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                            setColor(Color.fromRGB(current.getColor().value()))
                            customModel = 10 + index
                        }
                    })
                }
            }
            put(22, itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(locale.msgString("event.finish"), cHighlight)
                    customModel = 2
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(KHeads.CHECKMARK_GREEN)
            })
        }
    }

    private fun buildLore(data: ColorBuilder): List<Component> {
        return buildList {
            add(msgOutput)
            add(cmp("   (╯°□°）╯︵ ┻━┻", data.getColor()))
            add(emptyComponent())
            add(locale.msgClickLeft() + cmp("Change Color"))
            add(locale.msgShiftClickRight() + cmp("Delete"))
        }
    }
}