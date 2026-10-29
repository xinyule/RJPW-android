package com.bitwarden.data.repository.util

import com.bitwarden.data.datasource.disk.model.EnvironmentUrlDataJson
import com.bitwarden.data.repository.model.Environment
import java.net.URI

/**
 * The fallback base URL used when a self-hosted environment is missing values: the user's
 * own Vaultwarden server.
 */
private val DEFAULT_BASE_URL: String
    get() = EnvironmentUrlDataJson.DEFAULT.base

/**
 * Returns the base api URL or the default value if one is not present.
 */
val Environment.baseApiUrl: String
    get() = this.environmentUrlData.api.sanitizeUrl
        ?: this.environmentUrlData.base.sanitizeUrl?.let { "$it/api" }
        ?: "$DEFAULT_BASE_URL/api"

/**
 * Returns the base events URL or the default value if one is not present.
 */
val Environment.baseEventsUrl: String
    get() = this.environmentUrlData.events.sanitizeUrl
        ?: this.environmentUrlData.base.sanitizeUrl?.let { "$it/events" }
        ?: "$DEFAULT_BASE_URL/events"

/**
 * Returns the base identity URL or the default value if one is not present.
 */
val Environment.baseIdentityUrl: String
    get() = this.environmentUrlData.identity.sanitizeUrl
        ?: this.environmentUrlData.base.sanitizeUrl?.let { "$it/identity" }
        ?: "$DEFAULT_BASE_URL/identity"

/**
 * Returns the base web vault URL. This will check for a custom [EnvironmentUrlDataJson.webVault]
 * before falling back to the [EnvironmentUrlDataJson.base]. This can still return null if both
 * are null or blank.
 */
val Environment.baseWebVaultUrlOrNull: String?
    get() = this.environmentUrlData.webVault.sanitizeUrl
        ?: this.environmentUrlData.base.sanitizeUrl

/**
 * Returns the base web vault URL or the default value if one is not present.
 *
 * See [baseWebVaultUrlOrNull] for more details.
 */
val Environment.baseWebVaultUrlOrDefault: String
    get() = this.baseWebVaultUrlOrNull ?: DEFAULT_BASE_URL

/**
 * Returns the base web send URL or the default value if one is not present.
 */
val Environment.baseWebSendUrl: String
    get() = this.baseWebVaultUrlOrNull?.let { "$it/#/send/" }
        ?: "$DEFAULT_BASE_URL/#/send/"

/**
 * Returns the base web vault import URL or the default value if one is not present.
 */
val Environment.toBaseWebVaultImportUrl: String
    get() = this
        .baseWebVaultUrlOrDefault
        .let { "$it/#/tools/import" }

/**
 * Returns a base icon url based on the environment or the default value if values are missing.
 */
val Environment.baseIconUrl: String
    get() = this.environmentUrlData.icon.sanitizeUrl
        ?: this.environmentUrlData.base.sanitizeUrl?.let { "$it/icons" }
        ?: "$DEFAULT_BASE_URL/icons"

/**
 * Returns the host of the environment's base URL.
 *
 * @see selfHostedUrlOrNull
 */
val Environment.labelOrBaseUrlHost: String
    get() = when (this) {
        is Environment.SelfHosted -> {
            // Grab the domain
            // Ex: "https://www.abc.com/path-1/path-1" -> "www.abc.com"
            URI
                .create(this.environmentUrlData.selfHostedUrlOrNull.orEmpty())
                .host
                .orEmpty()
        }
    }

/**
 * Returns the first self-hosted environment URL from
 * [EnvironmentUrlDataJson.webVault], [EnvironmentUrlDataJson.base],
 * [EnvironmentUrlDataJson.api], and finally [EnvironmentUrlDataJson.identity]. Returns `null` if
 * all self-host environment URLs are null.
 */
private val EnvironmentUrlDataJson.selfHostedUrlOrNull: String?
    get() = this.webVault.sanitizeUrl
        ?: this.base.sanitizeUrl
        ?: this.api.sanitizeUrl
        ?: this.identity.sanitizeUrl

/**
 * A helper method to filter out blank urls and remove any trailing forward slashes.
 */
private val String?.sanitizeUrl: String?
    get() = this?.trimEnd('/').takeIf { !it.isNullOrBlank() }
