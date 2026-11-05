package eus.hitz.core.data.di

import android.content.Context
import android.content.res.AssetManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eus.hitz.core.data.audio.AndroidAudioProvider
import eus.hitz.core.data.audio.AndroidSoundEffects
import eus.hitz.core.data.content.AssetContentSource
import eus.hitz.core.data.content.AssetCourseRepository
import eus.hitz.core.data.content.ContentSource
import eus.hitz.core.data.database.HitzDatabase
import eus.hitz.core.data.database.ProgressDao
import eus.hitz.core.data.gamification.RoomGamificationRepository
import eus.hitz.core.data.progress.RoomProgressRepository
import eus.hitz.core.data.reminder.WorkManagerReminderScheduler
import eus.hitz.core.data.settings.DataStoreSettingsRepository
import eus.hitz.core.data.sync.OfflineSyncRepository
import eus.hitz.core.domain.audio.AudioProvider
import eus.hitz.core.domain.audio.SoundEffects
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.gamification.GamificationRepository
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.domain.settings.ReminderScheduler
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.sync.SyncRepository
import eus.hitz.core.domain.time.EpochClock
import eus.hitz.core.domain.time.ZoneProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.ZoneId
import javax.inject.Named
import javax.inject.Singleton

/** One instance per process, as DataStore requires (also across Hilt test components). */
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DataStoreSettingsRepository.FILE_NAME,
)

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

    @Binds
    @Singleton
    fun bindSettingsRepository(impl: DataStoreSettingsRepository): SettingsRepository

    @Binds
    @Singleton
    fun bindGamificationRepository(impl: RoomGamificationRepository): GamificationRepository

    @Binds
    fun bindReminderScheduler(impl: WorkManagerReminderScheduler): ReminderScheduler

    @Binds
    fun bindAudioProvider(impl: AndroidAudioProvider): AudioProvider

    @Binds
    fun bindSoundEffects(impl: AndroidSoundEffects): SoundEffects

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
        fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.settingsDataStore

        @Provides
        fun provideClock(): EpochClock = EpochClock { System.currentTimeMillis() }

        @Provides
        fun provideZone(): ZoneProvider = ZoneProvider { ZoneId.systemDefault() }
    }
}
