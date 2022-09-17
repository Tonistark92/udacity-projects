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

        val reminder = getReminder()
        saveReminderViewModel.saveReminder(reminder)
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }
    // we test if the saving the remainder that has no title which is not gonna happen because the
    // fragment dont navigate if there is no title but if there is no title we check in the view model
    // in fun validateEnteredData() so it will return false to validate_SaveReminder and it will return false
    // so it is at least notnullvalue and the snakebar will massage for enter the title
    @Test
    fun saveReminder_withoutTitle() {

        val reminder = ReminderDataItem(
                title = "tit",
                description = "desc",
                location = "loco",
                latitude = 47.5456551,
                longitude = 122.0101731)

        saveReminderViewModel.validate_SaveReminder(reminder)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())

    }
    // we here just test while pausing the Dispatcher if it is loading or not if so it will return true
    // which what we expect if not -and that after we resume Dispatcher- will return false
    @Test
    fun showLoading() = runBlocking {

        val reminder = getReminder()

        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validate_SaveReminder(reminder)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(false))


    }

    // testing saving the remainder without location in the validate_savereminder we sen the reminder
    // to validateEnteredData as we did to the location so it will return false if there is no location
    // the sme rest for the location test
    @Test
    fun saveReminder_withoutlocation() {

        val reminder = ReminderDataItem(
                title = "look",
                description = "lala",
                location = "",
                latitude = 47.5456551,
                longitude = 122.0101731)

        saveReminderViewModel.validate_SaveReminder(reminder)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())

    }
}