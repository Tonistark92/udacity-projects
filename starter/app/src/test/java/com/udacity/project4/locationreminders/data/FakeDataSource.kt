package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()): ReminderDataSource {

    private var wantReturnError = false

    fun setShouldReturnError(wantReturnError: Boolean) {
        this.wantReturnError = wantReturnError
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
    // the reasons -> that we need to check if the last stored data can be retrieved or
    // to check if we can connect using teh dao-> getreminders or not may be there is problem with query
    // or i used the wrong method and we need instromented test and unit test for reasons like
    // for unit the device may use some installed sql locally and for each device may differe in sql versions
    // this from the 8th video in the course
    // so i test it  if it work so use the Result.Success(ArrayList(reminders)) and get me the list
    // and if not return error
    // i used the wantReturnError for both check cases the error and the one case that really return
    // the list and both using the util class Result and it's inner classes
    // again is used wantReturnError for each check you can one at the time make it true and see the error
    // and let is false and it will return the list
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (wantReturnError){
            // if we want to see the error case so let the wantReturnError true so will get the error
            return Result.Error("Reminders not found", 404)
        }else{
            // this case for the Success return for the reminder
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
        if(wantReturnError){
            // if the boolean is true gets error
            return Result.Error("Error")
        }else{
            //if not try to get the reminder by id
            val reminder = reminders.find { it.id == id }

            if (reminder != null) {
                // if what we got from reminders.find is not null so return it by the util Result.Success
                 return Result.Success(reminder)
            } else {
                //if it is null return error
                return Result.Error("Reminder not found", 404)
            }
        }
    }
}