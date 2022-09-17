package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import java.lang.Exception


class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()): ReminderDataSource {

    private var shouldReturnError = false

    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }
    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
    // here we test the getReminders dao fun if the result is error so we use the result class util
    //if not we return Result.Success(ArrayList(reminders)) so it will return the list of  data
    // so test on the error case and the sucess
    // the error case that i pointed to that the DAO fun getReminders may not happen for some reasons
    // so i test it  if it work so use the Result.Success(ArrayList(reminders)) and get me the list
    // and if not return error
    // i used the shouldReturnError for both check cases the error and the one case that really return
    // the list and both using the util class Result and it's inner classes
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError){
            return Result.Error("Reminders not found", 404)
        }else{
            return return Result.Success(ArrayList(reminders))
        }
        // is that what you wanted ?
//        if(reminders.isEmpty()){
        //or reminders.size()==0

//            return  Result.Error("Reminders not found", 404)
//        }
//        else
//        {
//            return return Result.Success(ArrayList(reminders))
//        }
    }

    // and here based on the boolean if in the begining we dont want it to return any thing and get error
    // to test if it will preform well when the transaction is failed for some reason
    // else we return the normal remainder based on the id if exists and use the database util class
    // for Result.Success(reminder) to return the remainder
    //else we use the result's class inner class error  Result.Error("Reminder not found", 404)
    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(shouldReturnError){
            return Result.Error("Error")
        }else{
            val reminder = reminders.find { it.id == id }

            if (reminder != null) {
                 return Result.Success(reminder)
            } else {
                return Result.Error("Reminder not found", 404)
            }
        }
    }
}