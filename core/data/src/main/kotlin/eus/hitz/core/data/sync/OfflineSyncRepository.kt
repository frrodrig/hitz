package eus.hitz.core.data.sync

import eus.hitz.core.domain.sync.SyncRepository
import eus.hitz.core.domain.sync.SyncResult
import javax.inject.Inject

/** Implementación offline: no hay backend, así que no se sincroniza nada. */
internal class OfflineSyncRepository @Inject constructor() : SyncRepository {
    override val isEnabled: Boolean = false

    override suspend fun sync(): SyncResult = SyncResult.Disabled
}
