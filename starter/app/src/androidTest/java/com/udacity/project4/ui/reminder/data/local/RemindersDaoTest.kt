package com.udacity.project4.ui.reminder.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.data.dto.ReminderDTO
import com.udacity.project4.data.local.RemindersDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_test() = runBlockingTest {
        // GIVEN - insert a reminder
        val reminder1 = ReminderDTO("Title1", "Description1", "location1", 32.1, 32.1)
        database.reminderDao().saveReminder(reminder1)

        // WHEN - Get the reminder by id from the database
        val loaded = database.reminderDao().getReminderById(reminder1.id)

        // THEN - The loaded data contains the expected values
        assertThat(loaded as ReminderDTO).isNotNull()
        assertThat(loaded.id).isEqualTo(reminder1.id)
        assertThat(loaded.title).isEqualTo(reminder1.title)
        assertThat(loaded.description).isEqualTo(reminder1.description)
        assertThat(loaded.location).isEqualTo(reminder1.location)
        assertThat(loaded.latitude).isEqualTo(reminder1.latitude)
        assertThat(loaded.longitude).isEqualTo(reminder1.longitude)
    }
}