package de.miraculixx.mtimer.module

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mcommons.text.defaultLocale
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.Locale

object GlobalListener {

    private val onJoin = listen<PlayerJoinEvent> { checkDominantLanguage() }
    private val onQuit = listen<PlayerQuitEvent> { checkDominantLanguage() }

    private fun checkDominantLanguage() {
        val groups = onlinePlayers.groupBy { it.language() }
        var lastHighest = Locale.ENGLISH to 0
        groups.forEach { (locale, amount) ->
            if (lastHighest.second < amount.size) lastHighest = locale to amount.size
        }
        defaultLocale = lastHighest.first
    }
}