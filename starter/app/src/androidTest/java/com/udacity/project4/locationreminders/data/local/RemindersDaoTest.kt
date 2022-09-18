package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)

//Unit test the DAO
@SmallTest
class RemindersDaoTest {


    private lateinit var database: RemindersDatabase

    // Executes all tasks synchronously by Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // Using an in memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(getApplicationContext(), RemindersDatabase::class.java).build()
    }
    // close the db after finishing
    @After
    fun closeDb() = database.close()


    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
                title = "title",
                description = "desc",
                location = "loc",
                latitude = 47.5456551,
                longitude = 122.0101731)
    }
    // we first get reminder from getReminder and save it and then get it back by it's id so we test
    // the saving and the fitching with id and we check that the data we saved is that what we
    // inserted by retriving it by reminder's id then check what we expect that the
    // data of the saved one is the retriedd one
    @Test
    fun insertReminderAndFindById() = runBlockingTest {
        val reminder = getReminder()
        // we use the dataase's dao to save the reminder
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the reminder by (id) from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - check The loaded data contains the expected values.
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
        assertThat(loaded.location, `is`(reminder.location))
        }

    }
