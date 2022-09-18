package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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
@Config(maxSdk = Build.VERSION_CODES.P)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeRepo: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel

    @Before
    fun createRepository() {
        stopKoin()
        // init  the viewmodel
        fakeRepo = FakeDataSource()
        // init the repo
        remindersListViewModel = RemindersListViewModel(getApplicationContext(), fakeRepo
        )
    }
    // we here test what if the viewmodel askes for reminders and they he cant reach them so let the repo
    // return error ok?
    //and ask the viewmodel for the remainder to see if the snakbar's message is Reminders not found
    // this is expexted as the repo return error
    @Test
    fun loadRemindersWhenRemindersAreUnavailable() = runBlockingTest {
        // let the repo return return error so the fitiching should fail
        fakeRepo.setShouldReturnError(true)
        // let the viewmodel ask for reminder and we should get error
        remindersListViewModel.loadReminders()
        //assert for the return of the loadReminders (we wish to return Reminders not found)
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), `is`("Reminders not found"))

    }
    //we test when there is no data at all like in the first time we open the app so
    //in the viewmodel there is fun called invalidateShowNoData() we test if it will inform the user properly
    // if it will return true for no data or not
    @Test
    fun noData() = runBlockingTest {
        // first we delet all the reminders to test what happen when ask when no data
        fakeRepo.deleteAllReminders()
        // ask for reminders
        remindersListViewModel.loadReminders()
        // we wish the getOrAwaitValue to return true as there is no data
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
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
    // we use the fake one ReminderDTO that the getReminder returns and save it after ddeleting the the list ok?
    // and we pause the Dispatcher so the the reuslt that we expect that return true for loading the data cause we paused the
    // Dispatcher and it is waiting :) and when resuming the Dispatcher so it really commit and we wait for the
    // booleans for the nodata and loading false since the loading is done and the data is exisits now
    @Test
    fun showLoading_withdata() = runBlocking {
        // first delet all reminders
        fakeRepo.deleteAllReminders()
        // get one reminder from getReminder
        val reminder = getReminder()
        // save it in the repo
        fakeRepo.saveReminder(reminder)
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
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(false))


    }
}