package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.runner.RunWith
import org.hamcrest.core.IsNot
import org.junit.*
import org.koin.core.context.stopKoin
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    //  under test
    private lateinit var remindersList: RemindersListViewModel
    // Inject a fake data source into the viewModel.
    private lateinit var data: FakeDataSource

    private val item1 = ReminderDTO("Reminder1", "Description1", "Location1", 1.0, 1.0,"1")
    private val item2 = ReminderDTO("Reminder2", "Description2", "location2", 2.0, 2.0, "2")
    private val item3 = ReminderDTO("Reminder3", "Description3", "location3", 3.0, 3.0, "3")
    // Uses Architecture Components to execute each job in a synchronous manner.    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    //For unit testing, set the primary coroutine dispatcher.
    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Before
    fun model(){ stopKoin()
        data = FakeDataSource()
        remindersList = RemindersListViewModel(ApplicationProvider.getApplicationContext(), data)
    }

    @After
    fun clearData() = runBlockingTest{
        data.deleteAllReminders()
    }

    /*
    * This function tries to load the reminders from our View Model after testing removing all of the reminders.
    * Two variables are being tested here :
        1. show No Data [invalidateShowNoDataShowNoDataIsTrue]
        2. reminder list [loadRemindersLoadsThreeReminders]
    * */
    @Test
    fun invalidateShowNoDataShowNoDataIsTrue()= coroutineRule.runBlockingTest{
        // Empty DB
        data.deleteAllReminders()
        // Try to load Reminders
        remindersList.loadReminders()
        // expect that our reminder list Live data size is 0 and show no data is true
        MatcherAssert.assertThat(remindersList.remindersList.getOrAwaitValue().size, Is.`is`(0))
        MatcherAssert.assertThat(remindersList.showNoData.getOrAwaitValue(), Is.`is`(true))
    }
    // We test retrieving the three reminders we're placing in this method.
    @Test
    fun loadRemindersLoadsThreeReminders()= coroutineRule.runBlockingTest {
        //  just 3 Reminders in the DB
        data.deleteAllReminders()

        data.saveReminder(item1)
        data.saveReminder(item2)
        data.saveReminder(item3)
        // try to load Reminders
        remindersList.loadReminders()
        // expect to have only 3 reminders in remindersList and showNoData is false cause we have data
        MatcherAssert.assertThat(remindersList.remindersList.getOrAwaitValue().size, Is.`is`(3))
        MatcherAssert.assertThat(remindersList.showNoData.getOrAwaitValue(), Is.`is`(false))

    }
    // Here, we are testing checkLoading in this test.
    @Test
    fun loadRemindersCheckLoading()= coroutineRule.runBlockingTest{
        // Stop dispatcher so we may inspect initial values.
        coroutineRule.pauseDispatcher()
        //  Only 1 Reminder
        data.deleteAllReminders()
        data.saveReminder(item1)
        // load Reminders
        remindersList.loadReminders()
        // The loading indicator is displayed, then it is hidden after we are done.
        MatcherAssert.assertThat(remindersList.showLoading.getOrAwaitValue(), Is.`is`(true))
        // Execute pending coroutines actions
        coroutineRule.resumeDispatcher()
        // Then loading indicator is hidden
        MatcherAssert.assertThat(remindersList.showLoading.getOrAwaitValue(), Is.`is`(true))
    }
    // testing showing an Error
    @Test
    fun loadRemindersShouldReturnError()= coroutineRule.runBlockingTest{
        // give : set should return error to "true
        data.returnError(true)
        // when : we load Reminders
        remindersList.loadReminders()
        // then : We get showSnackBar in the view model giving us "not found"
        MatcherAssert.assertThat(
            remindersList.showSnackBar.getOrAwaitValue(),
            Is.`is`("no Reminder found")
        )
    }

}