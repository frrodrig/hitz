package eus.hitz.core.data.update

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eus.hitz.core.domain.update.AppInfo
import eus.hitz.core.domain.update.ReleaseRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UpdateModule {
    @Binds
    fun bindReleaseRepository(impl: GitHubReleaseRepository): ReleaseRepository

    @Binds
    @Singleton
    fun bindAppInfo(impl: AndroidAppInfo): AppInfo
}
