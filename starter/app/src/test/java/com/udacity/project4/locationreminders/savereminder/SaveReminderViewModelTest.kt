package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
//fix robolectricVersion with newer version,note we can ignore it but viewmodel use context to use in fragment
@Config(maxSdk =28)
@ExperimentalCoroutinesApi
class SaveReminderViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var saveRepository: FakeDataSource
    // Subject under test
    private lateinit var saveListViewModel: SaveReminderViewModel

    //TODO: provide testing to the SaveReminderView and its live data objects

    @Before//fix global intalize becaise each test need has new instance
    fun setupViewModel() {
        stopKoin()
        saveRepository = FakeDataSource()
        saveListViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),saveRepository)
    }
    @After
    fun delRep(){
        runBlocking { saveRepository.deleteAllReminders()}
    }
    // here trying the saving if successful and if it will be retrieved
    @Test
    fun addReminder_ReturnSaved() = runTest{

        val reminder =   ReminderDataItem(
            title = "tit",
            description = "desc",
            location = "loco",
            latitude = 47.5456551,
            longitude = 122.0101731)
        saveListViewModel.saveReminder(reminder)
        assertThat(saveListViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }
    // testing saving the remainder without location in the validate_savereminder we sen the reminder
    // to validateEnteredData as we did to the location so it will return false if there is no location
    // the sme rest for the location test
    @Test
    fun addNoLocation_retrunError() = runTest{

        val reminder = ReminderDataItem(
            title = "look",
            description = "lala",
            location = "",
            latitude = 47.5456551,
            longitude = 122.0101731)
        // we check in the viewmodel and in this fun validate_SaveReminder
        // we pass the reminder to be checked with validateEnteredData and it will return false and wont save
        //cause the reminder here with no location
        saveListViewModel.validateAndSaveReminder(reminder)
        // we wish the return of the fun validateEnteredData that in the validate_SaveReminder notNullValue like true or false
        //but here will return false cause there is no location in the reminder
        assertThat(saveListViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))
    }
    // we test if the saving the remainder that has no title which is not gonna happen because the
    // fragment dont navigate if there is no title but if there is no title we check in the view model
    // in fun validateEnteredData() so it will return false to validate_SaveReminder and it will return false
    // so it is at least notnullvalue and the snakebar will massage for enter the title
    @Test
    fun addNoTitle_retrunError() = runTest {
        val reminder =  ReminderDataItem(
            title = "tit",
            description = "desc",
            location = "loco",
            latitude = 47.5456551,
            longitude = 122.0101731)
        // the view model check and save the reminder and shouldn't save couse in
        // validate_SaveReminder we check before pass the reminder for the save fun in the validateEnteredData
        // and in this fun we check for the title and location in existis
        saveListViewModel.validateAndSaveReminder(reminder)
        // we wish to return non null cause in validateEnteredData return boolean so it will return false in this case
        assertThat(saveListViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))

    }
    // we here check if the loading will appear while we saving
    @Test
    fun addReminder_showLoadingBar()= runBlocking{
        val reminder =  ReminderDataItem(
            title = "tit",
            description = "desc",
            location = "loco",
            latitude = 47.5456551,
            longitude = 122.0101731)
        mainCoroutineRule.pauseDispatcher()
        saveListViewModel.validateAndSaveReminder(reminder)
        assertThat(saveListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveListViewModel.showLoading.getOrAwaitValue(), `is`(false))

    }

}