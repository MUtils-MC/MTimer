package de.miraculixx.mtimer.gui.items

import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.kpaper.gui.items.skullTexture
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mcommons.statics.KHeads
import de.miraculixx.mcommons.text.*
import de.miraculixx.mtimer.module.PaperTimer
import de.miraculixx.mtimer.vanilla.data.TimerDesign
import de.miraculixx.mtimer.vanilla.module.TimerManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ItemsDesignEditor(
    private val design: TimerDesign,
    private val uuid: UUID,
    private val locale: Locale
) : ItemProvider {
    override fun getSlotMap(): Map<Int, ItemStack> {
        val dummyTimer = PaperTimer(true, null, null, false)
        dummyTimer.time = (1.days + 10.hours + 5.minutes + 20.seconds + 500.milliseconds) // (1d 10h 5m 20s)
        val converter = ItemDesignConverter(TimerManager.globalTimer as PaperTimer, dummyTimer)
        return mapOf(
            10 to itemStack(Material.KNOWLEDGE_BOOK) {
                meta {
                    name = cmp(locale.msgString("items.designSetting.n"), cHighlight)
                    customModel = 5
                    lore(buildList {
                        val msgButton = cmp(locale.msgString("common.button") + " ", cHighlight)
                        addAll(locale.msgList("items.designSetting.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
                        add(cmp("   Bar Color: ") + cmp(design.barColor.name, cMark))
                        add(cmp("   End Sound: ") + cmp(design.stopSound.key, cMark))
                        add(cmp("   End Sound Pitch: ") + cmp(design.stopSound.pitch.toString(), cMark))
                        add(emptyComponent())
                        add(msgButton + cmpTranslatableVanilla("key.hotbar.1", cHighlight) + cmp(" ≫ ") + cmp("Change Bar Color"))
                        add(msgButton + cmpTranslatableVanilla("key.hotbar.2", cHighlight) + cmp(" ≫ ") + cmp("Change End Sound"))
                        add(msgButton + cmpTranslatableVanilla("key.hotbar.3", cHighlight) + cmp(" ≫ ") + cmp("Change End Sound Pitch"))
                    })
                }
            },
            11 to itemStack(Material.BOOK) {
                meta {
                    name = cmp(locale.msgString("items.designName.n"), cHighlight)
                    customModel = 1
                    lore(buildList {
                        addAll(locale.msgList("items.designName.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
                        add(cmp("   ${design.name}"))
                        add(emptyComponent())
                        add(locale.msgClick() + cmp("Change"))
                    })
                }
            },
            13 to converter.getItem(design, uuid),
            15 to itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(locale.msgString("items.designRunning.n"), cHighlight)
                    customModel = 2
                    lore(buildList {
                        addAll(locale.msgList("items.designRunning.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(locale.msgClick() + cmp("Open Settings"))
                    })
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(KHeads.RESUME_GREEN)
            },
            16 to itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(locale.msgString("items.designIdle.n"), cHighlight)
                    customModel = 3
                    lore(buildList {
                        addAll(locale.msgList("items.designIdle.l", inline = "<grey>"))
                        add(emptyComponent())
                        add(locale.msgClick() + cmp("Open Settings"))
                    })
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(KHeads.PAUSE_RED)
            },
            22 to itemStack(Material.PLAYER_HEAD) {
                meta {
                    name = cmp(locale.msgString("event.finish"), cSuccess)
                    customModel = 4
                }
                itemMeta = (itemMeta as SkullMeta).skullTexture(KHeads.CHECKMARK_GREEN)
            },
        )
    }
}