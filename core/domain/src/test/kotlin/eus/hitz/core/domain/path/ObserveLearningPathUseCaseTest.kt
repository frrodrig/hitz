package eus.hitz.core.domain.path

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.testing.FakeProgressRepository
import eus.hitz.core.domain.testing.TestContent
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ObserveLearningPathUseCaseTest {
    @Test
    fun `the path follows the progress`() = runTest {
        val progress = FakeProgressRepository()
        ObserveLearningPathUseCase(TestContent.courseRepository, progress)().test {
            assertThat(awaitItem().currentNode?.levelId).isEqualTo("u01-1.l1")
            progress.completed.value = setOf("u01-1.l1.s1")
            assertThat(awaitItem().currentNode?.levelId).isEqualTo("u01-1.l2")
        }
    }
}
