package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
                title = "title",
                description = "desc",
                location = "loc",
                latitude = 47.5456551,
                longitude = 122.0101731)
    }

    @Before
    fun setup() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }
    // we save the reminder as normal and get it back so we check with result class then
    // check if the saved == the returned so we tested teh saving and getting by id
    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        // GIVEN - A new reminder saved in the database.
        val reminder = getReminder()
        repository.saveReminder(reminder)

        // WHEN  - reminder retrieved by ID.
        val result = repository.getReminder(reminder.id)

        // THEN - Same reminder is returned.
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success


        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
        assertThat(result.data.location, `is`(reminder.location))
    }
    // testing that if the deleting all is really happening or not so we save one reminder that we have it's
    // id then we delete all then check if the reminder with the id we know is still there or it
    // the expected value  Reminder not found
    @Test
    fun deleteAllReminders_getReminderById() = runBlocking {
        val reminder = getReminder()
        repository.saveReminder(reminder)
        repository.deleteAllReminders()

        val result = repository.getReminder(reminder.id)


        assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))

    }
}