package com.x8bit.bitwarden.data.billing.di

import com.bitwarden.core.data.manager.dispatcher.DispatcherManager
import com.x8bit.bitwarden.data.auth.datasource.disk.AuthDiskSource
import com.x8bit.bitwarden.data.billing.manager.PremiumStateManager
import com.x8bit.bitwarden.data.billing.manager.PremiumStateManagerImpl
import com.x8bit.bitwarden.data.platform.datasource.disk.SettingsDiskSource
import com.x8bit.bitwarden.data.platform.repository.EnvironmentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides premium-state dependencies.
 *
 * RJPW: This module intentionally provides only [PremiumStateManager], which derives Premium
 * status from the server-synced profile. There is no billing repository or Play Billing
 * provider because in-app purchases do not exist in this build.
 */
@Module
@InstallIn(SingletonComponent::class)
object PremiumModule {

    @Provides
    @Singleton
    fun providePremiumStateManager(
        authDiskSource: AuthDiskSource,
        settingsDiskSource: SettingsDiskSource,
        environmentRepository: EnvironmentRepository,
        dispatcherManager: DispatcherManager,
    ): PremiumStateManager = PremiumStateManagerImpl(
        authDiskSource = authDiskSource,
        settingsDiskSource = settingsDiskSource,
        environmentRepository = environmentRepository,
        dispatcherManager = dispatcherManager,
    )
}
