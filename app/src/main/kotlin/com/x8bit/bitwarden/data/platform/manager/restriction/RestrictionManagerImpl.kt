package com.x8bit.bitwarden.data.platform.manager.restriction

import android.content.RestrictionsManager
import com.bitwarden.data.datasource.disk.model.EnvironmentUrlDataJson
import com.bitwarden.data.repository.model.Environment
import com.x8bit.bitwarden.data.platform.repository.EnvironmentRepository

/**
 * The default implementation of the [RestrictionManager].
 */
class RestrictionManagerImpl(
    private val environmentRepository: EnvironmentRepository,
    private val restrictionsManager: RestrictionsManager,
) : RestrictionManager {

    override fun initialize() {
        updatePreconfiguredRestrictionSettings()
    }

    private fun updatePreconfiguredRestrictionSettings() {
        restrictionsManager
            .applicationRestrictions
            ?.takeUnless { it.isEmpty }
            ?.getString(BASE_ENVIRONMENT_URL_RESTRICTION_KEY)
            ?.let { url -> setPreconfiguredUrl(baseEnvironmentUrl = url) }
    }

    private fun setPreconfiguredUrl(baseEnvironmentUrl: String) {
        // Every environment is a custom self-hosted environment.
        environmentRepository.environment =
            Environment.SelfHosted(EnvironmentUrlDataJson(baseEnvironmentUrl))
    }
}

private const val BASE_ENVIRONMENT_URL_RESTRICTION_KEY: String = "baseEnvironmentUrl"
