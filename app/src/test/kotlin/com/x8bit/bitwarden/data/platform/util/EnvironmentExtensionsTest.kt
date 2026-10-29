package com.x8bit.bitwarden.data.platform.util

import com.bitwarden.data.datasource.disk.model.EnvironmentUrlDataJson
import com.bitwarden.data.repository.model.Environment
import com.bitwarden.ui.platform.manager.intent.model.AuthTabData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EnvironmentExtensionsTest {

    @Test
    fun `duoAuthTabData should return the correct AuthTabData for all environments`() {
        assertEquals(
            AuthTabData.CustomScheme(
                callbackUrl = "bitwarden://duo-callback",
                callbackScheme = "bitwarden",
            ),
            Environment.SelfHosted.DEFAULT.duoAuthTabData,
        )
        assertEquals(
            AuthTabData.HttpsScheme(
                host = "bitwarden.pw",
                path = "duo-callback",
            ),
            Environment.SelfHosted(DEFAULT_INTERNAL_ENVIRONMENT_URL_DATA).duoAuthTabData,
        )
        assertEquals(
            AuthTabData.CustomScheme(
                callbackUrl = "bitwarden://duo-callback",
                callbackScheme = "bitwarden",
            ),
            Environment.SelfHosted(DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA).duoAuthTabData,
        )
    }

    @Test
    fun `webAuthnAuthTabData should return the correct AuthTabData for all environments`() {
        assertEquals(
            AuthTabData.CustomScheme(
                callbackUrl = "bitwarden://webauthn-callback",
                callbackScheme = "bitwarden",
            ),
            Environment.SelfHosted.DEFAULT.webAuthnAuthTabData,
        )
        assertEquals(
            AuthTabData.HttpsScheme(
                host = "bitwarden.pw",
                path = "webauthn-callback",
            ),
            Environment.SelfHosted(DEFAULT_INTERNAL_ENVIRONMENT_URL_DATA).webAuthnAuthTabData,
        )
        assertEquals(
            AuthTabData.CustomScheme(
                callbackUrl = "bitwarden://webauthn-callback",
                callbackScheme = "bitwarden",
            ),
            Environment.SelfHosted(DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA).webAuthnAuthTabData,
        )
    }

    @Test
    fun `ssoAuthTabData should return the correct AuthTabData for all environments`() {
        assertEquals(
            AuthTabData.CustomScheme(
                callbackUrl = "bitwarden://sso-callback",
                callbackScheme = "bitwarden",
            ),
            Environment.SelfHosted.DEFAULT.ssoAuthTabData,
        )
        assertEquals(
            AuthTabData.HttpsScheme(
                host = "bitwarden.pw",
                path = "sso-callback",
            ),
            Environment.SelfHosted(DEFAULT_INTERNAL_ENVIRONMENT_URL_DATA).ssoAuthTabData,
        )
        assertEquals(
            AuthTabData.CustomScheme(
                callbackUrl = "bitwarden://sso-callback",
                callbackScheme = "bitwarden",
            ),
            Environment.SelfHosted(DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA).ssoAuthTabData,
        )
    }

    @Test
    fun `appLinksScheme should return the correct scheme for the default environment`() {
        val expectedScheme = "bitwarden"

        assertEquals(
            expectedScheme,
            Environment.SelfHosted.DEFAULT.appLinksScheme,
        )
    }

    @Test
    fun `appLinksScheme should return the correct scheme for internal environment`() {
        val expectedScheme = "https"

        assertEquals(
            expectedScheme,
            Environment.SelfHosted(DEFAULT_INTERNAL_ENVIRONMENT_URL_DATA).appLinksScheme,
        )
    }

    @Test
    fun `appLinksScheme should return the correct scheme for custom environment`() {
        val expectedScheme = "bitwarden"

        assertEquals(
            expectedScheme,
            Environment.SelfHosted(DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA).appLinksScheme,
        )
    }
}

private val DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA = EnvironmentUrlDataJson(
    base = "base",
    api = "api",
    identity = "identity",
    icon = "icon",
    notifications = "notifications",
    webVault = "webVault",
    events = "events",
)

private val DEFAULT_INTERNAL_ENVIRONMENT_URL_DATA = DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA.copy(
    base = "qa.vault.bitwarden.pw",
)
