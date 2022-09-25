package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
//fix robolectricVersion with newer version,
// note we can ignore it but viewmodel use context to use in fragment
@Config(maxSdk =28)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersRepository: FakeDataSource
    // Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel



    @Before//fix global intalize becaise each test need has new instance
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        remindersListViewModel = RemindersListViewModel( getApplicationContext(),remindersRepository)
    }
    @After
    fun delRep(){
        runBlocking { remindersRepository.deleteAllReminders()}
    }
    // we use it to return one ReminderDTO with fake data for testing issues
    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
            title = "title",
            description = "desc",
            location = "loc",
            latitude = 47.5456551,
            longitude = 122.0101731)
    }

    @Test
    fun returnError() = mainCoroutineRule.runTest {
        //we seet the value for the repo false so it will get no data and casue erro
        remindersRepository.setError(false)
        // we load reminders and expecting to get error
        remindersListViewModel.loadReminders()
        advanceUntilIdle()
        // then we assert to get the error
        var value=remindersListViewModel.showSnackBar.getOrAwaitValue()
        assertThat(value, `is`("ERROR IN GET DATA"))

    }

    // we use the fake one ReminderDTO that the getReminder returns and save it after ddeleting the the list ok?
    // and we pause the Dispatcher so the the reuslt that we expect that return true for loading the data cause we paused the
    // Dispatcher and it is waiting :) and when resuming the Dispatcher so it really commit and we wait for the
    // booleans for the nodata and loading false since the loading is done and the data is exisits now
    @Test
    fun showLoading_withdata() =  mainCoroutineRule.runBlockingTest {
        // first delet all reminders
        remindersRepository.deleteAllReminders()
        // get one reminder from getReminder
        val reminder = getReminder()
        // save it in the repo
        remindersRepository.saveReminder(reminder)
        // we pause the corotine's dispature
        mainCoroutineRule.pauseDispatcher()
        // we ask for reminders whitch will load for awhile until we let teh dipature resume
        remindersListViewModel.loadReminders()
        // then we assert that the loading is showing or not
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        // we resume the dipature
        mainCoroutineRule.resumeDispatcher()
        // as we resumed the dipature so the loading and the waiting will end so both
        //showLoading.getOrAwaitValue() and showNoData.getOrAwaitValue() will return false
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))


    }


}