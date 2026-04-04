package features.onboarding.ui

import database.repository.SettingsRepository
import io.mockk.MockKAnnotations
import io.mockk.mockk
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
}
