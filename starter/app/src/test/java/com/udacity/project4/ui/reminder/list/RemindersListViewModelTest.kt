package com.udacity.project4.ui.reminder.list

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.data.dto.ReminderDTO
import com.udacity.project4.ui.reminder.getOrAwaitValue
import com.udacity.project4.ui.reminder.rule.KoinRule
import com.udacity.project4.ui.reminder.rule.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    // Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var fakeDataSource: FakeDataSource

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

        val reminder1 = ReminderDTO("Title1", "Description1", "location1", 32.1, 32.1)
        val reminder2 = ReminderDTO("Title2", "Description2", "location2", 33.1, 33.1)
        val reminder3 = ReminderDTO("Title3", "Description3", "location3", 34.1, 34.1)

        fakeDataSource.saveReminder(reminder1)
        fakeDataSource.saveReminder(reminder2)
        fakeDataSource.saveReminder(reminder3)

        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun `Loading view is visible while saving a reminder`() {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue()).isTrue()
    }

    @Test
    fun `Load reminders after delete them returns show snackbar`() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)

        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showNoData.getOrAwaitValue()).isTrue()
    }

}