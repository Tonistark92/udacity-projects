package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
//In the real database may occur exception something went wrong with data or schema
//So we use try/catch block to Handel this in the viewmodel but here in the fake data source fake
// data so we need to return data and return error manually I used like a flag (using the boolean like switcher)
// to switch between error and data mode we can't check on empty list we have test for the error empty data so should return an error
class FakeDataSource(var tasks: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {
    // the switcher
    private var error = false


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (!error) {

             Result.Error("ERROR IN GET DATA",404)

        } else {
            tasks.let {
                 Result.Success(ArrayList(it))
            }

        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks?.add(reminder)

    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val data = tasks!!.find {
            it.id == id
        } ?: return Result.Error("NO TASK FOUND", 404)
        return Result.Success(data)
    }


    fun setError(error: Boolean) {
        this.error = error
    }

    override suspend fun deleteAllReminders() {
        tasks?.clear()
    }


}