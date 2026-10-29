package com.x8bit.bitwarden.data.billing.manager

import app.cash.turbine.test
import com.bitwarden.core.data.manager.dispatcher.FakeDispatcherManager
import com.bitwarden.data.datasource.disk.model.EnvironmentUrlDataJson
import com.x8bit.bitwarden.data.auth.datasource.disk.model.AccountJson
import com.x8bit.bitwarden.data.auth.datasource.disk.model.UserStateJson
import com.x8bit.bitwarden.data.auth.datasource.disk.util.FakeAuthDiskSource
import com.x8bit.bitwarden.data.platform.datasource.disk.util.FakeSettingsDiskSource
import com.x8bit.bitwarden.data.platform.repository.util.FakeEnvironmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * Tests for the RJPW [PremiumStateManagerImpl] stub: Premium status is read only from the
 * server-synced profile, and there is no billing, subscription polling, or in-app upgrade.
 */
class PremiumStateManagerTest {

    private val fakeAuthDiskSource = FakeAuthDiskSource().apply {
        userState = DEFAULT_USER_STATE_JSON
    }

    private val fakeSettingsDiskSource = FakeSettingsDiskSource()

    private val fakeEnvironmentRepository = FakeEnvironmentRepository()

    private val dispatcherManager = FakeDispatcherManager()

    private fun createManager(): PremiumStateManagerImpl = PremiumStateManagerImpl(
        authDiskSource = fakeAuthDiskSource,
        settingsDiskSource = fakeSettingsDiskSource,
        environmentRepository = fakeEnvironmentRepository,
        dispatcherManager = dispatcherManager,
    )

    @Test
    fun `isPremiumFlow should be false when the synced profile has no premium`() = runTest {
        val manager = createManager()
        manager.isPremiumFlow.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `isPremiumFlow should be true when the synced profile has personal premium`() = runTest {
        fakeAuthDiskSource.userState = userStateJsonWith(
            account = createAccountJson(hasPremiumPersonally = true),
        )
        val manager = createManager()
        manager.isPremiumFlow.test {
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `isPremiumFlow should be true when the synced profile has organization premium`() =
        runTest {
            fakeAuthDiskSource.userState = userStateJsonWith(
                account = createAccountJson(hasPremiumFromOrganization = true),
            )
            val manager = createManager()
            manager.isPremiumFlow.test {
                assertTrue(awaitItem())
            }
        }

    @Test
    fun `isPremiumFlow should update when the synced profile changes`() = runTest {
        val manager = createManager()
        manager.isPremiumFlow.test {
            assertFalse(awaitItem())
            fakeAuthDiskSource.userState = userStateJsonWith(
                account = createAccountJson(hasPremiumPersonally = true),
            )
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `isPremiumFlow should be false when there is no user state`() = runTest {
        fakeAuthDiskSource.userState = null
        val manager = createManager()
        manager.isPremiumFlow.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `isInAppUpgradeAvailable should always be false`() {
        val manager = createManager()
        assertFalse(manager.isInAppUpgradeAvailable())
    }

    @Test
    fun `isSelfHosted should be true for the default self-hosted environment`() = runTest {
        val manager = createManager()
        assertTrue(manager.isSelfHosted)
        manager.isSelfHostedFlow.test {
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `isUpgradedToPremiumCardEligibleFlow should be false before any premium transition`() =
        runTest {
            val manager = createManager()
            manager.isUpgradedToPremiumCardEligibleFlow.test {
                assertFalse(awaitItem())
            }
        }

    @Suppress("MaxLineLength")
    @Test
    fun `isUpgradedToPremiumCardEligibleFlow should become true when sync grants personal premium`() =
        runTest {
            val manager = createManager()
            manager.isUpgradedToPremiumCardEligibleFlow.test {
                assertFalse(awaitItem())
                fakeAuthDiskSource.userState = userStateJsonWith(
                    account = createAccountJson(hasPremiumPersonally = true),
                )
                assertTrue(awaitItem())
            }
            assertEquals(
                true,
                fakeSettingsDiskSource.getUpgradedToPremiumCardPending(userId = ACTIVE_USER_ID),
            )
        }

    @Suppress("MaxLineLength")
    @Test
    fun `organization premium alone should not make the Upgraded to Premium card eligible`() =
        runTest {
            val manager = createManager()
            manager.isUpgradedToPremiumCardEligibleFlow.test {
                assertFalse(awaitItem())
                fakeAuthDiskSource.userState = userStateJsonWith(
                    account = createAccountJson(hasPremiumFromOrganization = true),
                )
                expectNoEvents()
            }
        }

    @Test
    fun `dismissUpgradedToPremiumCard should consume the card and store the flags`() = runTest {
        val manager = createManager()
        fakeAuthDiskSource.userState = userStateJsonWith(
            account = createAccountJson(hasPremiumPersonally = true),
        )
        manager.dismissUpgradedToPremiumCard()
        assertEquals(
            true,
            fakeSettingsDiskSource.getUpgradedToPremiumCardConsumed(userId = ACTIVE_USER_ID),
        )
        assertEquals(
            false,
            fakeSettingsDiskSource.getUpgradedToPremiumCardPending(userId = ACTIVE_USER_ID),
        )
        manager.isUpgradedToPremiumCardEligibleFlow.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `dismissPremiumUpgradeBanner should store the dismissed flag for the active user`() {
        val manager = createManager()
        manager.dismissPremiumUpgradeBanner()
        assertEquals(
            true,
            fakeSettingsDiskSource.getPremiumUpgradeBannerDismissed(userId = ACTIVE_USER_ID),
        )
    }
}

private const val ACTIVE_USER_ID = "activeUserId"

/**
 * Builds a [UserStateJson] whose active account is [account], keyed by `userId`.
 */
private fun userStateJsonWith(account: AccountJson): UserStateJson = UserStateJson(
    activeUserId = account.profile.userId,
    accounts = mapOf(account.profile.userId to account),
)

private fun createAccountJson(
    userId: String = ACTIVE_USER_ID,
    hasPremiumPersonally: Boolean = false,
    hasPremiumFromOrganization: Boolean = false,
): AccountJson = AccountJson(
    profile = AccountJson.Profile(
        userId = userId,
        email = "active@bitwarden.com",
        isEmailVerified = true,
        isTwoFactorEnabled = false,
        name = "Active User",
        stamp = null,
        organizationId = null,
        avatarColorHex = "#aa00aa",
        hasPremiumPersonally = hasPremiumPersonally,
        hasPremiumFromOrganization = hasPremiumFromOrganization,
        forcePasswordResetReason = null,
        kdfType = null,
        kdfIterations = null,
        kdfMemory = null,
        kdfParallelism = null,
        userDecryptionOptions = null,
        creationDate = Instant.parse("2023-10-01T12:00:00Z"),
    ),
    settings = AccountJson.Settings(environmentUrlData = EnvironmentUrlDataJson.DEFAULT),
)

private val DEFAULT_USER_STATE_JSON: UserStateJson =
    userStateJsonWith(account = createAccountJson())
