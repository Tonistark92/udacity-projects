package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest

import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource

import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository

import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi

import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
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
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class SaveReminderFragmentTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun initRepository() {

        stopKoin()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                SaveReminderViewModel(
                        ApplicationProvider.getApplicationContext(),
                        get() as ReminderDataSource
                )
            }

            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(ApplicationProvider.getApplicationContext()) }
        }

        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(myModule))
        }

        saveReminderViewModel = GlobalContext.get().koin.get()

    }

    // testing if we press the save button while the title is impty
    // will the snackbar will notify the user to fill it or not
    @Test
    fun noTitle_fails() {
        val navController = Mockito.mock(NavController::class.java)
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle.EMPTY, R.style.AppTheme)



        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())
        onView(withId(R.id.snackbar_text)).check(matches(withText(R.string.err_enter_title)))

    }

    private fun getReminder(): ReminderDataItem {
        return ReminderDataItem(
                title = "title",
                description = "desc",
                location = "loc",
                latitude = 47.5456551,
                longitude = 122.0101731)
    }
    //testing if the we press the save button when all done (he really filled the location and the title i mean it is succeses navigation should accur)
    //so the toast for the succeses saving should be showen
    @Test
    fun saveReminder_succeeds() {
       val reminder = getReminder()



        val navController = Mockito.mock(NavController::class.java)
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle.EMPTY, R.style.AppTheme)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }


        onView(withId(R.id.reminderTitle)).perform(ViewActions.typeText(reminder.title))
        onView(withId(R.id.reminderDescription)).perform(ViewActions.typeText(reminder.description))


       saveReminderViewModel.saveReminder(reminder)

       assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is` ("Reminder Saved !"))
    }
}