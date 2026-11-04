package eus.hitz.core.designsystem.component

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GroupPositionTest {
    @Test
    fun `single item is Single`() {
        assertThat(GroupPosition.of(0, 1)).isEqualTo(GroupPosition.Single)
    }

    @Test
    fun `positions in a group of three`() {
        assertThat((0..2).map { GroupPosition.of(it, 3) })
            .containsExactly(GroupPosition.First, GroupPosition.Middle, GroupPosition.Last)
            .inOrder()
    }
}
