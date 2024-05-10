package de.miraculixx.mtimer.vanilla.utils

import de.miraculixx.mcommons.text.cHighlight
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.msgString
import de.miraculixx.mcommons.text.plus
import net.kyori.adventure.text.Component
import java.util.Locale

private val separator = Component.text("-")
fun Locale.msgClick() = cmp(msgString("common.click"), cHighlight) + cmp(" ≫ ")
fun Locale.msgClickRight() = cmp(msgString("common.right"), cHighlight).append(separator) + msgClick()
fun Locale.msgShiftClickRight() = cmp(msgString("common.sneak"), cHighlight).append(separator) + msgClickRight()
fun Locale.msgClickLeft() = cmp(msgString("common.left"), cHighlight).append(separator) + msgClick()
fun Locale.msgShiftClickLeft() = cmp(msgString("common.sneak"), cHighlight).append(separator) + msgClickLeft()
fun Locale.msgShiftClick() = cmp(msgString("common.sneak"), cHighlight).append(separator) + msgClick()
fun Locale.msgTrue() = msgString("common.boolTrue")
fun Locale.msgFalse() = msgString("common.boolFalse")
fun Locale.msgNone() = msgString("common.none")
