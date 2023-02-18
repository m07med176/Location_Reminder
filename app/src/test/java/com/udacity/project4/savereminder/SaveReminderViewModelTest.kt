package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var saveReminder: SaveReminderViewModel
    private lateinit var data: FakeDataSource

    private val item1 = ReminderDataItem("Reminder1", "Description1", "Location1", 1.0, 1.0,"1")
    private val item2 = ReminderDataItem("", "Description2", "location2", 2.0, 2.0, "2")
    private val item3 = ReminderDataItem("Reminder3", "Description3", "", 3.0, 3.0, "3")
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Before
    fun setUpViewModel(){ stopKoin()
        data = FakeDataSource()
        saveReminder = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), data)
    }
    /**In this function we clear all reminders Live Data and test if they all null*/
    @Test
    fun onClearsReminderLiveData(){
        // Data to the variables
        saveReminder.reminderTitle.value = item1.title
        saveReminder.reminderDescription.value = item1.description
        saveReminder.reminderSelectedLocationStr.value = item1.location
        saveReminder.latitude.value = item1.latitude
        saveReminder.longitude.value = item1.longitude
        saveReminder.reminderId.value = item1.id
        // call on clear
        saveReminder.onClear()
        // expect all null
        MatcherAssert.assertThat(
            saveReminder.reminderTitle.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminder.reminderDescription.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminder.reminderSelectedLocationStr.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminder.latitude.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminder.longitude.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminder.reminderId.getOrAwaitValue(),
            Is.`is`(CoreMatchers.nullValue())
        )
    }
    // In this function we testing set the Live Data of reminder to be edited
    @Test
    fun editReminderSetsLiveDataOfReminderToBeEdited(){
        // call Edit reminder and passing item1
        saveReminder.editReminder(item1)
        // We expect that our saveReminderViewModel is holding the data of reminder1.
        MatcherAssert.assertThat(saveReminder.reminderTitle.getOrAwaitValue(), Is.`is`(item1.title))
        MatcherAssert.assertThat(
            saveReminder.reminderDescription.getOrAwaitValue(),
            Is.`is`(item1.description)
        )
        MatcherAssert.assertThat(
            saveReminder.reminderSelectedLocationStr.getOrAwaitValue(),
            Is.`is`(item1.location)
        )
        MatcherAssert.assertThat(saveReminder.latitude.getOrAwaitValue(), Is.`is`(item1.latitude))
        MatcherAssert.assertThat(saveReminder.longitude.getOrAwaitValue(), Is.`is`(item1.longitude))
        MatcherAssert.assertThat(saveReminder.reminderId.getOrAwaitValue(), Is.`is`(item1.id))
    }
    // add Reminder to Data via our ViewModel.saveReminder function
    @Test
    fun saveReminderAndAddsReminderToDataSource() = coroutineRule.runBlockingTest{
        // call save reminder passing item1
        saveReminder.saveReminder(item1)
        // Call get reminder that has id 1
        val checkReminder = data.getReminder("1") as Result.Success<ReminderDTO>
        // expect to get item1
        MatcherAssert.assertThat(checkReminder.data.title, Is.`is`(item1.title))
        MatcherAssert.assertThat(checkReminder.data.description, Is.`is`(item1.description))
        MatcherAssert.assertThat(checkReminder.data.location, Is.`is`(item1.location))
        MatcherAssert.assertThat(checkReminder.data.latitude, Is.`is`(item1.latitude))
        MatcherAssert.assertThat(checkReminder.data.longitude, Is.`is`(item1.longitude))
        MatcherAssert.assertThat(checkReminder.data.id, Is.`is`(item1.id))
    }
    // test check Loading
    @Test
    fun saveReminderAndCheckLoading()= coroutineRule.runBlockingTest{
        // Pause dispatcher so we can verify initial values
        coroutineRule.pauseDispatcher()
        // item1 to be saved
        saveReminder.saveReminder(item1)
        // loading indicator is shown
        MatcherAssert.assertThat(saveReminder.showLoading.getOrAwaitValue(), Is.`is`(true))
        // Execute pending coroutines actions
        coroutineRule.resumeDispatcher()
        // loading indicator is hidden
        MatcherAssert.assertThat(saveReminder.showLoading.getOrAwaitValue(), Is.`is`(false))
    }
    // test validateData by passing null title and we expect
    // showSnackBarInt to indicate to err_enter_title and validate return false
    @Test
    fun validateData_missingTitle_showSnackAndReturnFalse(){
        // Calling validateEnteredData and passing no title
        val valid = saveReminder.validateEnteredData(item2)
        // expect a SnackBar to be shown displaying err_enter_title string and validate return false
        MatcherAssert.assertThat(
            saveReminder.showSnackBarInt.getOrAwaitValue(),
            Is.`is`(R.string.err_enter_title)
        )
        MatcherAssert.assertThat(valid, Is.`is`(false))
    }
    //  test validateData by passing null location and we expect
    // showSnackBarInt to indicate to err_select_location and validate return false
    @Test
    fun validateData_missingLocation_showSnackAndReturnFalse(){
        // Calling validateEnteredData and passing no location
        val valid = saveReminder.validateEnteredData(item3)
        // expect a SnackBar to be shown displaying err_select_location string and validate return false
        MatcherAssert.assertThat(
            saveReminder.showSnackBarInt.getOrAwaitValue(),
            Is.`is`(R.string.err_select_location)
        )
        MatcherAssert.assertThat(valid, Is.`is`(false))
    }
}