package de.miraculixx.mtimer.module

import de.miraculixx.mcommons.debug
import de.miraculixx.mcommons.extensions.loadConfig
import de.miraculixx.mcommons.extensions.toUUID
import de.miraculixx.mcommons.text.*
import de.miraculixx.mtimer.data.Goals
import de.miraculixx.mtimer.data.Rules
import de.miraculixx.mtimer.data.TimerData
import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.data.TimerPresets
import java.io.File
import kotlin.time.Duration

fun TimerManager.load(folder: File) {
    if (debug) consoleAudience.sendMessage(prefix + cmp("Load all data from disk..."))
    val designsFolder = File("${folder.path}/designs")
    if (!designsFolder.exists()) designsFolder.mkdirs()
    TimerPresets.entries.forEach {
        if (it == TimerPresets.PRESET) return@forEach
        addDesign(it.design, it.uuid)
    }
    designsFolder.listFiles()?.forEach { file ->
        if (file.extension != "json") return@forEach
        val fileOut = file.loadConfig(TimerDesign(null, null))
        if (fileOut.running == null) {
            consoleAudience.sendMessage(prefix + cmp("Invalid file: ${file.name}", cError))
            return@forEach
        }
        addDesign(fileOut, file.nameWithoutExtension.toUUID() ?: return@forEach)
    }

    globalTimer = try {
        resolveTimer(File("${folder.path}/global-timer.json").loadConfig(TimerData()))
    } catch (e: Exception) {
        if (debug) consoleAudience.sendMessage(prefix + cmp("Malformed global timer save file! Creating a default timer..."))
        resolveTimer(TimerData(TimerPresets.CLASSIC.uuid, Duration.ZERO, true, true))
    }

    val pTimerOut = File("${folder.path}/personal-timers.json").loadConfig(listOf(TimerData()))
    personalTimer.forEach { (_, timer) -> timer.disableTimer() }
    personalTimer.clear()
    pTimerOut.forEach { pt -> pt.playerUUID?.let { personalTimer[it] = resolveTimer(pt) } }
    rules = File("${folder.path}/rules.json").loadConfig(Rules())
    goals = File("${folder.path}/goals.json").loadConfig(Goals())

    if (!globalTimer.running) {
        globalTimer.running = false
    }
}

private fun resolveTimer(data: TimerData): PaperTimer {
    val timer = PaperTimer(data.playerUUID != null, data.playerUUID, data.timerDesign, data.isVisible)
    timer.design = TimerManager.getDesign(data.timerDesign) ?: TimerManager.getDesign(TimerPresets.CLASSIC.uuid) ?: TimerPresets.error
    timer.time = data.time
    return timer
}