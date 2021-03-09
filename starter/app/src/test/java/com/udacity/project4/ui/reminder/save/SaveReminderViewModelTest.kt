package com.udacity.project4.ui.reminder.save

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.util.getOrAwaitValue
import com.udacity.project4.ui.reminder.list.ReminderDataItem
import com.udacity.project4.rule.KoinRule
import com.udacity.project4.rule.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var fakeDataSource: FakeDataSource

    private lateinit var appContext: Application

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Stops Koin after each test in order to avoid starting it multiple times.
    @get:Rule
    val koinRule = KoinRule()

    @Before
    fun setup() = runBlockingTest {
        fakeDataSource = FakeDataSource()

        appContext = ApplicationProvider.getApplicationContext()
        saveReminderViewModel = SaveReminderViewModel(appContext, fakeDataSource)
    }

    @Test
    fun `Loading view is visible while saving a reminder`() {
        mainCoroutineRule.pauseDispatcher()

        val reminder = ReminderDataItem(
            "Title",
            "Description",
            "Location",
            32.1,
            32.1
        )
        saveReminderViewModel.saveReminder(reminder)

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue()).isTrue()

        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue()).isFalse()
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue())
            .isEqualTo(appContext.getString(R.string.reminder_saved))
    }

    @Test
    fun `Save item with empty title returns show snackbar`() {
        saveReminderViewModel = SaveReminderViewModel(appContext, fakeDataSource)
        val reminder1 = ReminderDataItem(
            null,
            null,
            null,
            32.1,
            32.1
        )

        saveReminderViewModel.validateEnteredData(reminder1)

        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue())
            .isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun `Save item with empty location returns show snackbar`() {
        saveReminderViewModel = SaveReminderViewModel(appContext, fakeDataSource)
        val reminder = ReminderDataItem(
            "Title",
            null,
            null,
            32.1,
            32.1
        )
        saveReminderViewModel.validateEnteredData(reminder)

        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue())
            .isEqualTo(R.string.err_select_location)
    }
}
