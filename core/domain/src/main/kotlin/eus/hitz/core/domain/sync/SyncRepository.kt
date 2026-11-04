package eus.hitz.core.domain.sync

/**
 * Punto de extensión para un backend futuro. La app es offline-first: mientras no haya
 * backend, la implementación no hace nada y devuelve [SyncResult.Disabled].
 */
interface SyncRepository {
    /** Sube el progreso local y descarga cambios remotos. */
    suspend fun sync(): SyncResult

    /** Indica si hay un backend configurado. */
    val isEnabled: Boolean
}

sealed interface SyncResult {
    data object Disabled : SyncResult

    data class Success(val syncedAtEpochMillis: Long) : SyncResult

    data class Failure(val cause: Throwable) : SyncResult
}
