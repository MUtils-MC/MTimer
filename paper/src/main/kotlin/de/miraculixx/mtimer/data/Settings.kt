package de.miraculixx.mtimer.data

import de.miraculixx.mcommons.serializer.LocaleSerializer
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class Settings(
    var language: @Serializable(with = LocaleSerializer::class) Locale = Locale.ENGLISH,
    var displaySlot: TimerDisplaySlot = TimerDisplaySlot.HOTBAR
)

@Serializable
data class Rules(
    var freezeWorld: Boolean = false,
    var announceSeed: Boolean = true,
    var announceLocation: Boolean = true,
    var announceBack: Boolean = true,
    var specOnDeath: Boolean = true,
    var specOnJoin: Boolean = false,
    var punishmentSetting: PunishmentSetting = PunishmentSetting(),
    var syncWithChallenge: Boolean = true,
)

@Serializable
data class Goals(
    var enderDragon: Boolean = true,
    var wither: Boolean = false,
    var elderGuardian: Boolean = false,
    var warden: Boolean = false,
    var playerDeath: Boolean = true,
    var emptyServer: Boolean = true,
)

@Serializable
data class PunishmentSetting(
    var active: Boolean = false,
    var type: Punishment = Punishment.BAN
)