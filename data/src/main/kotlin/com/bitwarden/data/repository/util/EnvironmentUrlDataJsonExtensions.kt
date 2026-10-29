package com.bitwarden.data.repository.util

import com.bitwarden.data.datasource.disk.model.EnvironmentUrlDataJson
import com.bitwarden.data.repository.model.Environment

/**
 * Converts a raw [EnvironmentUrlDataJson] to an externally-consumable [Environment].
 *
 * RJPW: Every environment is a self-hosted environment; there are no pre-configured
 * official cloud regions to match against.
 */
fun EnvironmentUrlDataJson.toEnvironmentUrls(): Environment =
    Environment.SelfHosted(environmentUrlData = this)

/**
 * Converts a nullable [EnvironmentUrlDataJson] to an [Environment], where `null` values
 * default to the user's own Vaultwarden server.
 */
fun EnvironmentUrlDataJson?.toEnvironmentUrlsOrDefault(): Environment =
    this?.toEnvironmentUrls() ?: Environment.SelfHosted.DEFAULT
