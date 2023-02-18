package com.udacity.project4.locationreminders

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class ReminderDescriptionActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )

        val reminderData = intent.extras?.get(EXTRA_ReminderDataItem) as ReminderDataItem
        binding.reminderDataItem = reminderData
        binding.lifecycleOwner = this

    }
}

class DescriptionViewModel(private val dataSource: ReminderDataSource, application: Application)
    : AndroidViewModel(application) {

    fun deleteReminder(reminderData: ReminderDataItem) {
        viewModelScope.launch {
            dataSource.delete(reminderData.id)
        }
    }

}
