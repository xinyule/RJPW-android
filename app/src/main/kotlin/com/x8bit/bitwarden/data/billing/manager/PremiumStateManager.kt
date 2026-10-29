package com.x8bit.bitwarden.data.billing.manager

import kotlinx.coroutines.flow.StateFlow

/**
 * URL opened when the user taps the "Learn more" CTA on the "Upgraded to Premium" action card.
 */
const val UPGRADED_TO_PREMIUM_LEARN_MORE_URL: String =
    "https://bitwarden.com/help/password-manager-plans/"

/**
 * Manages Premium state for the active user.
 *
 * RJPW: There is no in-app billing or Premium purchase flow. Premium status is whatever the
 * server reports for the account during sync (for example, a self-hosted Vaultwarden server
 * granting Premium), surfaced here directly from the synced profile.
 */
interface PremiumStateManager {

    /**
     * Emits `true` when the active user has Premium according to the server-synced profile —
     * either granted personally or through an organization — and `false` otherwise.
     */
    val isPremiumFlow: StateFlow<Boolean>

    /**
     * Emits `true` while the active user is eligible to see the "Upgraded to Premium" action
     * card and `false` otherwise. Eligibility persists across app launches until the user
     * consumes the card via [dismissUpgradedToPremiumCard].
     */
    val isUpgradedToPremiumCardEligibleFlow: StateFlow<Boolean>

    /**
     * Emits whether the current environment is self-hosted. Reactive equivalent of
     * [isSelfHosted].
     */
    val isSelfHostedFlow: StateFlow<Boolean>

    /**
     * `true` when the current environment is self-hosted, or `false` otherwise.
     */
    val isSelfHosted: Boolean

    /**
     * Returns whether the in-app upgrade flow is available. Always `false`: upgrades are not
     * sold in-app; Premium is granted and managed by the server.
     */
    fun isInAppUpgradeAvailable(): Boolean

    /**
     * Marks the Premium upgrade banner as dismissed for the current user.
     */
    fun dismissPremiumUpgradeBanner()

    /**
     * Marks the "Upgraded to Premium" action card as consumed for the current user. This is
     * called for both the dismiss (X) and Learn more interactions — once consumed, the card
     * never re-appears for that user.
     */
    fun dismissUpgradedToPremiumCard()
}
