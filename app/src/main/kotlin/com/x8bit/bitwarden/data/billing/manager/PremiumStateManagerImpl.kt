package com.x8bit.bitwarden.data.billing.manager

import com.bitwarden.core.data.manager.dispatcher.DispatcherManager
import com.bitwarden.data.repository.model.Environment
import com.x8bit.bitwarden.data.auth.datasource.disk.AuthDiskSource
import com.x8bit.bitwarden.data.auth.repository.util.activeUserIdChangesFlow
import com.x8bit.bitwarden.data.platform.datasource.disk.SettingsDiskSource
import com.x8bit.bitwarden.data.platform.repository.EnvironmentRepository
import com.x8bit.bitwarden.data.platform.util.scanPairs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

/**
 * Default implementation of [PremiumStateManager].
 *
 * RJPW: Premium status is read only from the server-synced profile
 * (`hasPremiumPersonally` / `hasPremiumFromOrganization`). There is deliberately no billing
 * repository, no Play Billing dependency, and no subscription polling or upgrade lifecycle
 * state machine — the server (for example, the user's own Vaultwarden) is the single source
 * of truth for whether an account has Premium.
 */
class PremiumStateManagerImpl(
    private val authDiskSource: AuthDiskSource,
    private val settingsDiskSource: SettingsDiskSource,
    environmentRepository: EnvironmentRepository,
    dispatcherManager: DispatcherManager,
) : PremiumStateManager {

    private val unconfinedScope = CoroutineScope(dispatcherManager.unconfined)

    override val isPremiumFlow: StateFlow<Boolean> =
        authDiskSource
            .userStateFlow
            .map { userState ->
                val profile = userState?.activeAccount?.profile
                profile?.hasPremiumPersonally == true ||
                    profile?.hasPremiumFromOrganization == true
            }
            .distinctUntilChanged()
            .stateIn(
                scope = unconfinedScope,
                started = SharingStarted.Eagerly,
                initialValue = authDiskSource.userState
                    ?.activeAccount
                    ?.profile
                    ?.let {
                        it.hasPremiumPersonally == true ||
                            it.hasPremiumFromOrganization == true
                    }
                    ?: false,
            )

    override val isSelfHostedFlow: StateFlow<Boolean> =
        environmentRepository
            .environmentStateFlow
            .map { it is Environment.SelfHosted }
            .distinctUntilChanged()
            .stateIn(
                scope = unconfinedScope,
                started = SharingStarted.Eagerly,
                initialValue = environmentRepository.environment is Environment.SelfHosted,
            )

    /**
     * The card surfaces only while the active user holds personal Premium. This guards against
     * organization-granted premium marking the card pending for users with no personal
     * premium to celebrate.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override val isUpgradedToPremiumCardEligibleFlow: StateFlow<Boolean> =
        authDiskSource
            .activeUserIdChangesFlow
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(false)
                } else {
                    combine(
                        settingsDiskSource
                            .getUpgradedToPremiumCardPendingFlow(userId)
                            .map { it ?: false },
                        settingsDiskSource
                            .getUpgradedToPremiumCardConsumedFlow(userId)
                            .map { it ?: false },
                        authDiskSource
                            .userStateFlow
                            .map {
                                it?.activeAccount?.profile?.hasPremiumPersonally == true
                            },
                    ) { isPending, isConsumed, isPremiumFromSelf ->
                        isPending && !isConsumed && isPremiumFromSelf
                    }
                }
            }
            .distinctUntilChanged()
            .stateIn(
                scope = unconfinedScope,
                started = SharingStarted.Eagerly,
                initialValue = false,
            )

    init {
        // Sync-delta detection: observe the active user's personal premium flag transitioning
        // false → true as reported by the server during sync (there is no push or purchase
        // signal in the self-hosted build). Keyed on `hasPremiumPersonally` so that
        // organization-granted premium does not trigger the personal-upgrade card.
        authDiskSource
            .userStateFlow
            .map { state ->
                state?.activeAccount?.profile?.let {
                    it.userId to (it.hasPremiumPersonally == true)
                }
            }
            .distinctUntilChanged()
            .scanPairs()
            .onEach { (previous, current) ->
                if (current == null) return@onEach
                val (currentUserId, currentIsPremiumFromSelf) = current
                if (!currentIsPremiumFromSelf) return@onEach
                // Same user transitioning from non-personal-premium to personal-premium counts
                // as an upgrade.
                if (previous?.first == currentUserId && !previous.second) {
                    markUpgradedToPremiumCardPending(userId = currentUserId)
                }
            }
            .launchIn(unconfinedScope)
    }

    override val isSelfHosted: Boolean get() = isSelfHostedFlow.value

    override fun isInAppUpgradeAvailable(): Boolean = false

    override fun dismissPremiumUpgradeBanner() {
        val activeUserId = authDiskSource.userState?.activeUserId ?: return
        settingsDiskSource.storePremiumUpgradeBannerDismissed(
            userId = activeUserId,
            isDismissed = true,
        )
    }

    override fun dismissUpgradedToPremiumCard() {
        val activeUserId = authDiskSource.userState?.activeUserId ?: return
        settingsDiskSource.storeUpgradedToPremiumCardConsumed(
            userId = activeUserId,
            isConsumed = true,
        )
        settingsDiskSource.storeUpgradedToPremiumCardPending(
            userId = activeUserId,
            isPending = false,
        )
    }

    private fun markUpgradedToPremiumCardPending(userId: String) {
        // Don't re-arm the card if the user has already consumed it for this account.
        if (settingsDiskSource.getUpgradedToPremiumCardConsumed(userId = userId) == true) {
            return
        }
        settingsDiskSource.storeUpgradedToPremiumCardPending(
            userId = userId,
            isPending = true,
        )
    }
}
