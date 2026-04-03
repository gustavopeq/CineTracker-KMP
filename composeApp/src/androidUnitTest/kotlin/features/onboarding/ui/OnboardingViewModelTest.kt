package features.onboarding.ui

import common.util.platform.AppNotifications
import database.repository.SettingsRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

class OnboardingViewModelTest {

    private val settingsRepository: SettingsRepository = mockk(relaxUnitFun = true)

    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(AppNotifications)
        every { AppNotifications.scheduleEngagementReminders() } just runs
        every { AppNotifications.cancelEngagementReminders() } just runs
        viewModel = OnboardingViewModel(settingsRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `completeOnboarding calls setOnboardingCompleted on repository`() {
        viewModel.completeOnboarding {}

        verify { settingsRepository.setOnboardingCompleted() }
    }

    @Test
    fun `completeOnboarding invokes callback after writing flag`() {
        var callbackInvoked = false

        viewModel.completeOnboarding { callbackInvoked = true }

        assertTrue(callbackInvoked)
    }

    @Test
    fun `enableEngagementReminders saves preference as enabled`() {
        viewModel.enableEngagementReminders()

        verify { settingsRepository.setEngagementRemindersEnabled(true) }
    }

    @Test
    fun `enableEngagementReminders schedules notifications`() {
        viewModel.enableEngagementReminders()

        verify { AppNotifications.scheduleEngagementReminders() }
    }

    @Test
    fun `skipEngagementReminders saves preference as disabled`() {
        viewModel.skipEngagementReminders()

        verify { settingsRepository.setEngagementRemindersEnabled(false) }
    }
}
