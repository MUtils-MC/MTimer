package de.miraculixx.mtimer.module

import de.miraculixx.challenge.api.MChallengeAPI
import de.miraculixx.challenge.api.modules.challenges.ChallengeStatus
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.pluginManager
import de.miraculixx.mcommons.debug
import de.miraculixx.mcommons.text.cError
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import de.miraculixx.mtimer.vanilla.module.rules

object ChallengeSync {
    var challengeAPI: MChallengeAPI? = null

    fun connect() {
        if (challengeAPI != null) {
            if (debug) console.sendMessage(prefix + cmp("Challenge API already connected!", cError))
            return
        }

        if (pluginManager.isPluginEnabled("MChallenge")) {
            console.sendMessage(prefix + cmp("MChallenge found, syncing with challenges...", cError))
            challengeAPI = MChallengeAPI.instance
            if (challengeAPI == null) console.sendMessage(prefix + cmp("Failed to find MChallenge API implementation!", cError))
            else {
                TimerAPI.onStartLogic {
                    if (rules.syncWithChallenge) {
                        when (challengeAPI?.getChallengeStatus()) {
                            ChallengeStatus.PAUSED -> challengeAPI?.resumeChallenges()
                            ChallengeStatus.STOPPED -> challengeAPI?.startChallenges()
                            else -> Unit
                        }
                    }
                }
                TimerAPI.onStopLogic {
                    if (rules.syncWithChallenge) {
                        when (challengeAPI?.getChallengeStatus()) {
                            ChallengeStatus.RUNNING -> challengeAPI?.pauseChallenges()
                            else -> Unit
                        }
                    }
                }
            }
        } else {
            if (debug) console.sendMessage(prefix + cmp("MChallenge not found, sync will not work", cError))
        }
    }
}