package de.miraculixx.mtimer.vanilla.data

import de.miraculixx.mcommons.text.cHighlight
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.plus
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

enum class TimerGUI(val title: Component) {
    COLOR(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Color Picker", cHighlight)),

    RULES(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Rules", cHighlight)),
    GOALS(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Goals", cHighlight)),
    DESIGN_PART_EDITOR(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Design Editor", cHighlight)),
    DESIGN_EDITOR(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Design Editor", cHighlight)),
    DESIGN(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer Design", cHighlight)),
    OVERVIEW(cmp("• ", NamedTextColor.DARK_GRAY) + cmp("Timer", cHighlight));
}