package de.miraculixx.mtimer.vanilla.data

import de.miraculixx.mcommons.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.bossbar.BossBar
import java.util.*
import kotlin.time.Duration

@Serializable
data class TimerData(
    val timerDesign: @Serializable(with = UUIDSerializer::class) UUID = UUID.randomUUID(),
    val time: Duration = Duration.ZERO,
    val isVisible: Boolean = true,
    val countingUp: Boolean = true,
    val playerUUID: @Serializable(with = UUIDSerializer::class) UUID? = null
)

@Serializable
data class TimerDesign(
    val running: TimerDesignPart?,
    val idle: TimerDesignPart?,
    var name: String = "New Design",
    var owner: String = "MUtils",
    var barColor: BossBar.Color = BossBar.Color.BLUE,
    var stopSound: TimerSound = TimerSound("minecraft:entity.ender_dragon.growl")
)

@Serializable
data class TimerDesignPart(
    var syntax: String,
    val days: TimerDesignValue,
    val hours: TimerDesignValue,
    val minutes: TimerDesignValue,
    val seconds: TimerDesignValue,
    val millis: TimerDesignValue,
    var prefix: String,
    var suffix: String,
    var animationSpeed: Float
)

@Serializable
data class TimerDesignValue(
    var forcedTwoDigits: Boolean,
    var visibleOnNull: Boolean,
    var prefix: String,
    var suffix: String
)

@Serializable
data class TimerSound(
    var key: String,
    var pitch: Float = 1f
)