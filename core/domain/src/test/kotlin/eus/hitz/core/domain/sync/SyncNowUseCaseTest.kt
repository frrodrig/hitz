package eus.hitz.core.domain.sync

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SyncNowUseCaseTest {
    private class FakeSyncRepository(override val isEnabled: Boolean) : SyncRepository {
        var calls = 0

        override suspend fun sync(): SyncResult {
            calls++
            return SyncResult.Success(syncedAtEpochMillis = 42L)
        }
    }

    @Test
    fun `sin backend no sincroniza`() = runTest {
        val repo = FakeSyncRepository(isEnabled = false)
        assertThat(SyncNowUseCase(repo)()).isEqualTo(SyncResult.Disabled)
        assertThat(repo.calls).isEqualTo(0)
    }

    @Test
    fun `con backend delega en el repositorio`() = runTest {
        val repo = FakeSyncRepository(isEnabled = true)
        assertThat(SyncNowUseCase(repo)()).isEqualTo(SyncResult.Success(42L))
        assertThat(repo.calls).isEqualTo(1)
    }
}
