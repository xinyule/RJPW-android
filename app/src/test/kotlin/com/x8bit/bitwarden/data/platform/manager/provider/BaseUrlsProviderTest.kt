package com.x8bit.bitwarden.data.platform.manager.provider

import com.bitwarden.data.repository.model.Environment
import com.x8bit.bitwarden.data.platform.datasource.disk.FakeEnvironmentDiskSource
import com.x8bit.bitwarden.data.platform.provider.BaseUrlsProviderImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class BaseUrlsProviderTest {

    private val fakeEnvironmentDiskSource = FakeEnvironmentDiskSource()
    private val baseUrlsManager = BaseUrlsProviderImpl(
        environmentDiskSource = fakeEnvironmentDiskSource,
    )

    @Test
    fun `getBaseApiUrl should return correct api URL when preAuthEnvironmentUrlData is set`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = Environment.SelfHosted.DEFAULT.environmentUrlData
        assertEquals(
            "https://pw.7052.cn/api",
            baseUrlsManager.getBaseApiUrl(),
        )
    }

    @Test
    fun `getBaseApiUrl should return default value when preAuthEnvironmentUrlData is null`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = null
        assertEquals(
            "https://pw.7052.cn/api",
            baseUrlsManager.getBaseApiUrl(),
        )
    }

    @Test
    fun `getBaseIdentityUrl should return correct api URL when preAuthEnvironmentUrlData is set`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = Environment.SelfHosted.DEFAULT.environmentUrlData
        assertEquals(
            "https://pw.7052.cn/identity",
            baseUrlsManager.getBaseIdentityUrl(),
        )
    }

    @Test
    fun `getBaseIdentityUrl should return default value when preAuthEnvironmentUrlData is null`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = null
        assertEquals(
            "https://pw.7052.cn/identity",
            baseUrlsManager.getBaseIdentityUrl(),
        )
    }

    @Test
    fun `getBaseEventsUrl should return correct api URL when preAuthEnvironmentUrlData is set`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = Environment.SelfHosted.DEFAULT.environmentUrlData
        assertEquals(
            "https://pw.7052.cn/events",
            baseUrlsManager.getBaseEventsUrl(),
        )
    }

    @Test
    fun `getBaseEventsUrl should return default value when preAuthEnvironmentUrlData is null`() {
        fakeEnvironmentDiskSource.preAuthEnvironmentUrlData = null
        assertEquals(
            "https://pw.7052.cn/events",
            baseUrlsManager.getBaseEventsUrl(),
        )
    }

    @Test
    fun `getBaseFillAssistUrl should return url from disk source when present`() {
        fakeEnvironmentDiskSource.fillAssistRulesUrl = "https://example.com/"
        assertEquals("https://example.com/", baseUrlsManager.getBaseFillAssistUrl())
    }

    @Test
    fun `getBaseFillAssistUrl should return null when not set`() {
        fakeEnvironmentDiskSource.fillAssistRulesUrl = null
        assertNull(baseUrlsManager.getBaseFillAssistUrl())
    }
}
