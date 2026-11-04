package eus.hitz.core.domain.sync

import javax.inject.Inject

/** Triggers a sync only when a backend is configured; otherwise it is a no-op. */
class SyncNowUseCase @Inject constructor(
    private val repository: SyncRepository,
) {
    suspend operator fun invoke(): SyncResult =
        if (repository.isEnabled) repository.sync() else SyncResult.Disabled
}
