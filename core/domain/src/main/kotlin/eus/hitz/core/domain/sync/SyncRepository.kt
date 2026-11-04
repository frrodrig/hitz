package eus.hitz.core.domain.sync

/**
 * Extension point for a future backend. The app is offline-first: until a backend exists,
 * the implementation does nothing and returns [SyncResult.Disabled].
 */
interface SyncRepository {
    /** Uploads local progress and downloads remote changes. */
    suspend fun sync(): SyncResult

    /** Whether a backend is configured. */
    val isEnabled: Boolean
}

sealed interface SyncResult {
    data object Disabled : SyncResult

    data class Success(val syncedAtEpochMillis: Long) : SyncResult

    data class Failure(val cause: Throwable) : SyncResult
}
