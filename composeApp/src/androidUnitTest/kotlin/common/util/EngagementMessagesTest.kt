package common.util

import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Test

class EngagementMessagesTest {

    @Test
    fun `getFridayMessages returns exactly 3 messages`() {
        assertEquals(3, EngagementMessages.getFridayMessages().size)
    }

    @Test
    fun `getSundayMessages returns exactly 3 messages`() {
        assertEquals(3, EngagementMessages.getSundayMessages().size)
    }

    @Test
    fun `getMessagesForDayOfWeek returns friday messages for day 6`() {
        val messages = EngagementMessages.getMessagesForDayOfWeek(EngagementMessages.FRIDAY_DAY_OF_WEEK)
        assertEquals(EngagementMessages.getFridayMessages(), messages)
    }

    @Test
    fun `getMessagesForDayOfWeek returns sunday messages for day 1`() {
        val messages = EngagementMessages.getMessagesForDayOfWeek(EngagementMessages.SUNDAY_DAY_OF_WEEK)
        assertEquals(EngagementMessages.getSundayMessages(), messages)
    }

    @Test
    fun `getMessagesForDayOfWeek returns empty for other days`() {
        for (day in listOf(2, 3, 4, 5, 7)) {
            assertTrue(EngagementMessages.getMessagesForDayOfWeek(day).isEmpty())
        }
    }

    @Test
    fun `getRandomMessageForDayOfWeek returns non-null for friday`() {
        val message = EngagementMessages.getRandomMessageForDayOfWeek(EngagementMessages.FRIDAY_DAY_OF_WEEK)
        assertTrue(EngagementMessages.getFridayMessages().contains(message))
    }

    @Test
    fun `getRandomMessageForDayOfWeek returns non-null for sunday`() {
        val message = EngagementMessages.getRandomMessageForDayOfWeek(EngagementMessages.SUNDAY_DAY_OF_WEEK)
        assertTrue(EngagementMessages.getSundayMessages().contains(message))
    }

    @Test
    fun `getRandomMessageForDayOfWeek returns null for invalid day`() {
        assertNull(EngagementMessages.getRandomMessageForDayOfWeek(3))
    }
}
