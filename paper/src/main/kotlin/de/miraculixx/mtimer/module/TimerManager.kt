package de.miraculixx.mtimer.module

import de.miraculixx.mcommons.debug
import de.miraculixx.mcommons.extensions.saveConfig
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.consoleAudience
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import de.miraculixx.mtimer.data.Goals
import de.miraculixx.mtimer.data.Rules
import de.miraculixx.mtimer.data.Settings
import de.miraculixx.mtimer.data.*
import java.io.File
import java.util.*

lateinit var rules: Rules
lateinit var goals: Goals
lateinit var settings: Settings

object TimerManager {
    lateinit var globalTimer: Timer
    val personalTimer: MutableMap<UUID, Timer> = mutableMapOf()
    private val designs: MutableMap<UUID, TimerDesign> = mutableMapOf()

    fun getDesign(uuid: UUID): TimerDesign? {
        return designs[uuid]
    }

    fun getDesigns(): Map<UUID, TimerDesign> {
        return designs
    }

    fun addDesign(design: TimerDesign, uuid: UUID) {
        designs[uuid] = design
    }

    fun removeDesign(uuid: UUID, dataFolder: File): Boolean {
        File("${dataFolder.path}/$uuid.json").delete()
        return designs.remove(uuid) != null
    }

    fun getPersonalTimer(uuid: UUID): Timer? {
        return personalTimer[uuid]
    }

    fun addPersonalTimer(uuid: UUID, timer: Timer): Timer {
        personalTimer[uuid] = timer
        return timer
    }

    fun removePersonalTimer(uuid: UUID): Boolean {
        val timer = personalTimer[uuid] ?: return false
        timer.disableTimer()
        return personalTimer.remove(uuid) != null
    }

    private fun toDataObj(timer: Timer): TimerData {
        return TimerData(designs.filter { it.value == timer.design }.keys.firstOrNull() ?: TimerPresets.CLASSIC.uuid, timer.time, timer.visible, timer.countUp, timer.playerID)
    }

    fun save(folder: File) {
        if (debug) consoleAudience.sendMessage(prefix + cmp("Save all data to disk..."))
        val designFolder = File("${folder.path}/designs")
        if (!designFolder.exists()) designFolder.mkdirs()
        val skipIDs = TimerPresets.entries.map { it.uuid }
        designs.forEach { (id, data) ->
            if (skipIDs.contains(id)) return@forEach
            File("${designFolder.path}/$id.json").saveConfig(data)
        }

        File("${folder.path}/global-timer.json").saveConfig(toDataObj(globalTimer))
        File("${folder.path}/personal-timers.json").saveConfig(personalTimer.map { toDataObj(it.value) })
        File("${folder.path}/rules.json").saveConfig(rules)
        File("${folder.path}/goals.json").saveConfig(goals)
    }
}