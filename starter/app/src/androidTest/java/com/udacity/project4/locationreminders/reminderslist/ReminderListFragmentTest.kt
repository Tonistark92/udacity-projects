package com.udacity.project4.locationreminders.reminderslist


import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click

import androidx.test.espresso.assertion.ViewAssertions.matches

import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.core.Is
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var repository: ReminderDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun initRepository() {

     stopKoin()

    val myModule = module {
        //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
        viewModel {
            RemindersListViewModel(
                    getApplicationContext(),
                    get() as ReminderDataSource
            )
        }

        single { RemindersLocalRepository(get()) as ReminderDataSource }
        single { LocalDB.createRemindersDao(getApplicationContext()) }
    }

    startKoin {
        androidContext(getApplicationContext())
        modules(listOf(myModule))
    }

     repository = GlobalContext.get().koin.get()


     runBlocking {
         repository.deleteAllReminders()
     }
    }

    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
                title = "title",
                description = "desc",
                location = "loc",
                latitude = 47.5456551,
                longitude = 122.0101731)
    }
    // we save the reminder and check if it is displayed on the fragment or not so we expect
    // that the saved and the showed in the same
    @Test
    fun reminders_DisplayedInUi() = runBlockingTest{
        // we get the reminder
        val reminder = getReminder()
        runBlocking{
//            we save it
            repository.saveReminder(reminder)
        }

        //we launch for testing if the reminders is diplayed or not in the ReminderListFragment
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        //check the views for the data is exists in each field
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))
        onView(withText(reminder.title)).check(matches(isDisplayed()))
        onView(withText(reminder.description)).check(matches(isDisplayed()))
        onView(withText(reminder.location)).check(matches(isDisplayed()))

    }
    // we check if there is no lists if the icon for impty bahavior is displayed or not
    @Test
    fun noReminders_shows_noData() = runBlockingTest{
        // we launch the fragment (ReminderListFragment) to check if the image for impty behavior is showed or not
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        //checking if the image for impty behavior is showed or not
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }
    // we check if we click on the floating action button will navigate
    //to the save reminder or not
    @Test
    fun clickOnFabIcon_navigatesTo_saveReminderFragment() {
        //init the fragment
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
       //init the navController class
        val navController = mock(NavController::class.java)
        // attach the navController to fragment
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        //checking for the button will navigate or nor to the SaveReminder
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }


}