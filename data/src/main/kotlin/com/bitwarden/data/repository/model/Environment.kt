package com.bitwarden.data.repository.model

import com.bitwarden.data.datasource.disk.model.EnvironmentUrlDataJson
import com.bitwarden.data.repository.util.labelOrBaseUrlHost

/**
 * A higher-level wrapper around [EnvironmentUrlDataJson] that provides type-safety, enumerability,
 * and human-readable labels.
 *
 * RJPW: The only supported environment is a self-hosted server. The default is the user's own
 * Vaultwarden server (see [EnvironmentUrlDataJson.DEFAULT]); official Bitwarden cloud regions
 * are not offered.
 */
sealed class Environment {
    /**
     * A type that can be enumerated over indicating the high-level type of environment.
     */
    abstract val type: Type

    /**
     * The raw [EnvironmentUrlDataJson] that contains specific base URLs for each relevant domain.
     */
    abstract val environmentUrlData: EnvironmentUrlDataJson

    /**
     * Indicates if the environment is used for FedRamp.
     */
    abstract val isFedRamp: Boolean

    /**
     * A human-readable label for the environment based in some way on its base URL.
     */
    abstract val label: String

    /**
     * A custom self-hosted environment with a fully configurable [environmentUrlData].
     */
    data class SelfHosted(
        override val environmentUrlData: EnvironmentUrlDataJson,
    ) : Environment() {
        override val type: Type get() = Type.SELF_HOSTED
        override val label: String get() = this.labelOrBaseUrlHost
        override val isFedRamp: Boolean get() = environmentUrlData.isFedRamp
        val isInternal: Boolean get() = environmentUrlData.isInternal

        @Suppress("UndocumentedPublicClass")
        companion object {
            /**
             * The default self-hosted environment, pointing at the user's own Vaultwarden
             * server.
             */
            val DEFAULT: SelfHosted = SelfHosted(
                environmentUrlData = EnvironmentUrlDataJson.DEFAULT,
            )
        }
    }

    /**
     * A summary of the various types that can be enumerated over.
     */
    enum class Type {
        SELF_HOSTED,
    }

    @Suppress("UndocumentedPublicClass")
    companion object {
        /**
         * List of all primary development environment base URLs.
         */
        val DEFAULT_INTERNAL_ENVIRONMENTS: List<String> = listOf(
            "https://vault.qa.bitwarden.pw",
            "https://qa-team.sh.bitwarden.pw",
            "https://vault.usdev.bitwarden.pw",
            "https://vault.fedramp.usdev.bitwarden.pw",
            "https://gw.dev.bitwarden.pw",
            "https://gw.stg.bitwarden.pw",
        )
    }
}
