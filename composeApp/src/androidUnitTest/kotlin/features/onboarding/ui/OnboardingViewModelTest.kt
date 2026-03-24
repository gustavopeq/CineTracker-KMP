package features.onboarding.ui

import database.repository.SettingsRepository
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val settingsRepository: SettingsRepository = mockk(relaxUnitFun = true)

    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = OnboardingViewModel(settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `completeOnboarding calls setOnboardingCompleted on repository`() = runTest {
        viewModel.completeOnboarding {}

        advanceUntilIdle()

        coVerify { settingsRepository.setOnboardingCompleted() }
    }

    @Test
    fun `completeOnboarding invokes callback after writing flag`() = runTest {
        var callbackInvoked = false

        viewModel.completeOnboarding { callbackInvoked = true }

        advanceUntilIdle()

        assertTrue(callbackInvoked)
    }
}
