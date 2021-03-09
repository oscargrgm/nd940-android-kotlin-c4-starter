package com.udacity.project4.data

import com.udacity.project4.data.dto.ReminderDTO
import com.udacity.project4.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(
    var reminders: MutableList<ReminderDTO>? = mutableListOf()
) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        reminders?.let {
            Result.Success(ArrayList(it))
        } ?: Result.Error("reminders not found")

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> =
        reminders?.firstOrNull { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error("reminder not found")

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}