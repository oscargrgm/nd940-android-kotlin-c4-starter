package com.udacity.project4.ui.reminder.data.local

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.data.dto.ReminderDTO
import com.udacity.project4.data.dto.Result
import com.udacity.project4.data.local.RemindersDatabase
import com.udacity.project4.data.local.RemindersLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase
    private lateinit var appContext: Application

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        appContext = ApplicationProvider.getApplicationContext()

        database = Room.inMemoryDatabaseBuilder(appContext, RemindersDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        localDataSource = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        val reminder1 = ReminderDTO("Title1", "Description1", "location1", 32.1, 32.1)
        localDataSource.saveReminder(reminder1)

        val result = localDataSource.getReminder(reminder1.id)
        result as Result.Success

        assertThat(result.data.id).isEqualTo(reminder1.id)
        assertThat(result.data.title).isEqualTo(reminder1.title)
        assertThat(result.data.description).isEqualTo(reminder1.description)
        assertThat(result.data.latitude).isEqualTo(reminder1.latitude)
        assertThat(result.data.longitude).isEqualTo(reminder1.longitude)
    }

    @Test
    fun shouldReturnError_saveReminder_retrievesReminder() = runBlocking {
        // GIVEN - a new reminder saved in the database
        val reminder1 = ReminderDTO("Title1", "Description1", "location1", 32.1, 32.1)
        localDataSource.saveReminder(reminder1)

        // WHEN  - reminder1 retrieved by ID
        val result = localDataSource.getReminder("1323")
        result as Result.Error

        // THEN - Same task is returned
        assertThat(result.message).isEqualTo("Reminder not found!")

    }

    @Test
    fun deleteAllReminders_dataReminderIsEmpty() = runBlocking {
        localDataSource.deleteAllReminders()
        val result = localDataSource.getReminders()

        result as Result.Success
        assertThat(result.data.isEmpty()).isTrue()
    }
}