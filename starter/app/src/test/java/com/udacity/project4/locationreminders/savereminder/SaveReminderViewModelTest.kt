package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import androidx.test.core.app.ApplicationProvider.getApplicationContext


import androidx.test.ext.junit.runners.AndroidJUnit4


import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource

import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers

import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat

import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(maxSdk = Build.VERSION_CODES.P)
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeRepo: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun createRepository() {
        stopKoin()

        fakeRepo = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
                getApplicationContext(),
                fakeRepo
        )
        runBlocking{ fakeRepo.deleteAllReminders()}

    }

    private fun getReminder(): ReminderDataItem {
        return ReminderDataItem(
                title = "title",
                description = "desc",
                location = "loc",
                latitude = 47.5456551,
                longitude = 122.0101731)
    }
    // we her test the normal saving in teh database as we expect after saving it to return Reminder Saved
    @Test
    fun saveReminder() {
        // we get one reminder from the getReminder
        val reminder = getReminder()
        // the view model saves the reminder
        saveReminderViewModel.saveReminder(reminder)
        // we assert for the message Reminder Saved after saving (for success insertion)
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }
    // we test if the saving the remainder that has no title which is not gonna happen because the
    // fragment dont navigate if there is no title but if there is no title we check in the view model
    // in fun validateEnteredData() so it will return false to validate_SaveReminder and it will return false
    // so it is at least notnullvalue and the snakebar will massage for enter the title
    @Test
    fun saveReminder_withoutTitle() {
        // we creat fake reminder
        val reminder = ReminderDataItem(
                title = "tit",
                description = "desc",
                location = "loco",
                latitude = 47.5456551,
                longitude = 122.0101731)
        // the view model check and save the reminder and shouldn't save couse in
        // validate_SaveReminder we check before pass the reminder for the save fun in the validateEnteredData
        // and in this fun we check for the title and location in existis
        saveReminderViewModel.validate_SaveReminder(reminder)
        // we wish to return non null cause in validateEnteredData return boolean so it will return false in this case
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())

    }
    // we here just test while pausing the Dispatcher if it is loading or not if so it will return true
    // which what we expect if not -and that after we resume Dispatcher- will return false
    @Test
    fun showLoading() = runBlocking {

        val reminder = getReminder()
        // we pause the Dispatcher
        mainCoroutineRule.pauseDispatcher()
        // we save the reminder
        saveReminderViewModel.validate_SaveReminder(reminder)
        // we wish the fun getOrAwaitValue return true as the dipature is paused so it is loading now
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))
        //we resume the Dispatcher
        mainCoroutineRule.resumeDispatcher()
        // so the loading would be done so the showLoading.getOrAwaitValue() should return false now
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(false))


    }

    // testing saving the remainder without location in the validate_savereminder we sen the reminder
    // to validateEnteredData as we did to the location so it will return false if there is no location
    // the sme rest for the location test
    @Test
    fun saveReminder_withoutlocation() {
        // we create fake reminder
        val reminder = ReminderDataItem(
                title = "look",
                description = "lala",
                location = "",
                latitude = 47.5456551,
                longitude = 122.0101731)
        // we check in the viewmodel and in this fun validate_SaveReminder
        // we pass the reminder to be checked with validateEnteredData and it will return false and wont save
        //cause the reminder here with no location
        saveReminderViewModel.validate_SaveReminder(reminder)
        // we wish the return of the fun validateEnteredData that in the validate_SaveReminder notNullValue like true or false
        //but here will return false cause there is no location in the reminder
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())

    }
}