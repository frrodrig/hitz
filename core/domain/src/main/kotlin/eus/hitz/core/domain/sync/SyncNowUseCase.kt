package eus.hitz.core.domain.sync

import javax.inject.Inject

/** Lanza una sincronización solo si hay backend; si no, no hace nada. */
class SyncNowUseCase @Inject constructor(
    private val repository: SyncRepository,
) {
    suspend operator fun invoke(): SyncResult =
        if (repository.isEnabled) repository.sync() else SyncResult.Disabled
}
