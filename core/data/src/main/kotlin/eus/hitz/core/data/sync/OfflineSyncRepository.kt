package eus.hitz.core.data.sync

import eus.hitz.core.domain.sync.SyncRepository
import eus.hitz.core.domain.sync.SyncResult
import javax.inject.Inject

/** Offline implementation: there is no backend, so nothing is synced. */
internal class OfflineSyncRepository @Inject constructor() : SyncRepository {
    override val isEnabled: Boolean = false

    override suspend fun sync(): SyncResult = SyncResult.Disabled
}
