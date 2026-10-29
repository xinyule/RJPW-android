package com.bitwarden.data.repository.util

import com.bitwarden.data.datasource.disk.model.EnvironmentUrlDataJson
import com.bitwarden.data.repository.model.Environment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EnvironmentUrlsDataJsonExtensionsTest {
    @Test
    fun `toEnvironmentUrls should correctly convert the default urls to the expected type`() {
        assertEquals(
            Environment.SelfHosted.DEFAULT,
            EnvironmentUrlDataJson.DEFAULT.toEnvironmentUrls(),
        )
    }

    @Test
    fun `toEnvironmentUrls should correctly convert custom urls to the expected type`() {
        assertEquals(
            Environment.SelfHosted(
                environmentUrlData = DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA,
            ),
            DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA.toEnvironmentUrls(),
        )
    }

    @Test
    fun `toEnvironmentUrlsOrDefault should correctly convert the default urls to the expected type`() {
        assertEquals(
            Environment.SelfHosted.DEFAULT,
            EnvironmentUrlDataJson.DEFAULT.toEnvironmentUrlsOrDefault(),
        )
    }

    @Test
    fun `toEnvironmentUrlsOrDefault should correctly convert custom urls to the expected type`() {
        assertEquals(
            Environment.SelfHosted(
                environmentUrlData = DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA,
            ),
            DEFAULT_CUSTOM_ENVIRONMENT_URL_DATA.toEnvironmentUrlsOrDefault(),
        )
    }

    @Test
    fun `toEnvironmentUrlsOrDefault should convert null types to the default values`() {
        assertEquals(
            Environment.SelfHosted.DEFAULT,
            (null as EnvironmentUrlDataJson?).toEnvironmentUrlsOrDefault(),
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
