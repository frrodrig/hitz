package eus.hitz.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eus.hitz.core.data.sync.OfflineSyncRepository
import eus.hitz.core.domain.sync.SyncRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    @Singleton
    fun bindSyncRepository(impl: OfflineSyncRepository): SyncRepository
}
