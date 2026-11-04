package eus.hitz.core.data.di

import android.content.Context
import android.content.res.AssetManager
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eus.hitz.core.data.content.AssetContentSource
import eus.hitz.core.data.content.AssetCourseRepository
import eus.hitz.core.data.content.ContentSource
import eus.hitz.core.data.database.HitzDatabase
import eus.hitz.core.data.database.ProgressDao
import eus.hitz.core.data.progress.RoomProgressRepository
import eus.hitz.core.data.sync.OfflineSyncRepository
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.domain.sync.SyncRepository
import eus.hitz.core.domain.time.EpochClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    @Singleton
    fun bindSyncRepository(impl: OfflineSyncRepository): SyncRepository

    @Binds
    @Singleton
    fun bindCourseRepository(impl: AssetCourseRepository): CourseRepository

    @Binds
    fun bindContentSource(impl: AssetContentSource): ContentSource

    @Binds
    @Singleton
    fun bindProgressRepository(impl: RoomProgressRepository): ProgressRepository

    companion object {
        @Provides
        fun provideAssetManager(@ApplicationContext context: Context): AssetManager = context.assets

        @Provides
        @Named(AssetCourseRepository.IO_DISPATCHER)
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): HitzDatabase =
            Room.databaseBuilder(context, HitzDatabase::class.java, HitzDatabase.NAME).build()

        @Provides
        fun provideProgressDao(database: HitzDatabase): ProgressDao = database.progressDao()

        @Provides
        fun provideClock(): EpochClock = EpochClock { System.currentTimeMillis() }
    }
}
