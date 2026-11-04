package eus.hitz.core.data.sync

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.sync.SyncResult
import kotlinx.coroutines.test.runTest
import org.junit.Test

class OfflineSyncRepositoryTest {
    @Test
    fun `la implementación offline está desactivada`() = runTest {
        val repo = OfflineSyncRepository()
        assertThat(repo.isEnabled).isFalse()
        assertThat(repo.sync()).isEqualTo(SyncResult.Disabled)
    }
}
