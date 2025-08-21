package de.miraculixx.mtimer.module

import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mcommons.text.msg
import de.miraculixx.mtimer.data.TimerDisplaySlot.BOSSBAR
import de.miraculixx.mtimer.data.TimerDisplaySlot.HOTBAR
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import java.util.*
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PaperTimer(
    private val isPersonal: Boolean,
    playerID: UUID? = null,
    designID: UUID? = null,
    activate: Boolean = true,
) : Timer(designID, playerID) {
    private fun getPlayer() = playerID?.let { Bukkit.getPlayer(it) }
    private val listener = if (isPersonal) null else TimerListener()
    override var running = false
        set(value) {
            field = value

            if (value) {
                listener?.activateTimer()
                if (settings.displaySlot == BOSSBAR) getPlayer()?.showBossBar(bossBar)
                startLogics.forEach { it.invoke() }
            } else {
                listener?.deactivateTimer()
                stopLogics.forEach { it.invoke() }
            }
        }

    override fun disableListener() {
        listener?.disableAll()
        getPlayer()?.hideBossBar(bossBar)
    }

    private fun run() {
        task(false, 0, 1) {
            if (remove) it.cancel()
            if (!visible) return@task
            if (isPersonal && getPlayer() == null) return@task
            tickLogics.forEach { tick -> tick.invoke(time) }

            val target = if (isPersonal) listOf(getPlayer()?.player) else {
                if (running) onlinePlayers else onlinePlayers.filter { player ->
                    val p = TimerManager.getPersonalTimer(player.uniqueId)
                    if (p == null) true else !(p.visible)
                }
            }

            animator += (if (running) design.running?.animationSpeed else design.idle?.animationSpeed) ?: 0.05f
            if (animator > 1.0f) animator -= 2.0f
            else if (animator < -1.0f) animator += 2.0f

            val globalTimer = if (isPersonal) TimerManager.globalTimer else this
            if (!isPersonal || (!globalTimer.visible || !globalTimer.running)) {
                val component = buildFormatted(running)
                when (settings.displaySlot) {
                    HOTBAR -> target.forEach { t -> t?.sendActionBar(component) }
                    BOSSBAR -> bossBar.name(component)
                }
            }

            if (!running) return@task
            if (time < 0.seconds) {
                running = false
                target.forEach { p ->
                    if (p == null) return@forEach
                    p.playSound(Sound.sound(Key.key(design.stopSound.key), Sound.Source.MASTER, 1f, design.stopSound.pitch))
                    val locale = p.language()
                    p.showTitle(
                        Title.title(
                            msg("event.timeout.head", locale = locale), msg("event.timeout.sub", locale = locale),
                            Title.Times.times(java.time.Duration.ofMillis(300), java.time.Duration.ofMillis(5000), java.time.Duration.ofMillis(1000))
                        )
                    ) // 0,3s 5s 1s
                }
                time = ZERO
                return@task
            }

            time += if (countUp) 50.milliseconds else (-50).milliseconds
        }
    }

    init {
        if (activate || !isPersonal) run()
        visible = activate
    }
}