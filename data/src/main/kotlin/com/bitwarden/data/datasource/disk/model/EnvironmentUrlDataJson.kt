package com.bitwarden.data.datasource.disk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents URLs for various Bitwarden domains.
 *
 * @property base The overall base URL.
 * @property keyUri A Uri containing the alias and host of the key used for mutual TLS.
 * @property api Separate base URL for the "/api" domain (if applicable).
 * @property identity Separate base URL for the "/identity" domain (if applicable).
 * @property icon Separate base URL for the icon domain (if applicable).
 * @property notifications Separate base URL for the notifications domain (if applicable).
 * @property webVault Separate base URL for the web vault domain (if applicable).
 * @property events Separate base URL for the events domain (if applicable).
 */
@Serializable
data class EnvironmentUrlDataJson(
    @SerialName("base")
    val base: String,

    @SerialName("keyUri")
    val keyUri: String? = null,

    @SerialName("api")
    val api: String? = null,

    @SerialName("identity")
    val identity: String? = null,

    @SerialName("icons")
    val icon: String? = null,

    @SerialName("notifications")
    val notifications: String? = null,

    @SerialName("webVault")
    val webVault: String? = null,

    @SerialName("events")
    val events: String? = null,
) {
    /**
     * Indicates if this is a FedRamp environment.
     */
    val isFedRamp: Boolean
        get() = base.contains(other = BITWARDEN_FED_RAMP_DEV_BASE_URL) ||
            base.contains(other = GAME_WARDEN_FED_RAMP_DEV_BASE_URL) ||
            base.contains(other = GAME_WARDEN_FED_RAMP_STAGING_BASE_URL)

    /**
     * Indicates if this is an internal environment.
     */
    val isInternal: Boolean get() = base.contains(other = BITWARDEN_INTERNAL_DOMAIN)

    @Suppress("UndocumentedPublicClass")
    companion object {
        /**
         * The domain used for internal Bitwarden environments.
         */
        private const val BITWARDEN_INTERNAL_DOMAIN: String = "bitwarden.pw"

        /**
         * The base URL used for the Bitwarden FedRAMP development environments.
         */
        private const val BITWARDEN_FED_RAMP_DEV_BASE_URL: String =
            "vault.fedramp.usdev.bitwarden.pw"

        /**
         * The base URL used for the Game Warden FedRAMP development environments.
         */
        private const val GAME_WARDEN_FED_RAMP_DEV_BASE_URL: String = "gw.dev.bitwarden.pw"

        /**
         * The base URL used for the Game Warden FedRAMP staging environments.
         */
        private const val GAME_WARDEN_FED_RAMP_STAGING_BASE_URL: String = "gw.stg.bitwarden.pw"

        /**
         * The default [EnvironmentUrlDataJson]: the user's own self-hosted Vaultwarden
         * server. All sub-URLs (api, identity, icons, ...) are derived from this base.
         */
        val DEFAULT: EnvironmentUrlDataJson =
            EnvironmentUrlDataJson(base = "https://pw.7052.cn")
    }
}
