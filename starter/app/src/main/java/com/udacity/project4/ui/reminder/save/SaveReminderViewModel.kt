package com.udacity.project4.ui.reminder.save

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.location.Location
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.data.ReminderDataSource
import com.udacity.project4.data.dto.ReminderDTO
import com.udacity.project4.ui.reminder.list.ReminderDataItem
import com.udacity.project4.utils.extension.CurrentLocationResult
import com.udacity.project4.utils.extension.async
import com.udacity.project4.utils.extension.requireCurrentLocation
import kotlinx.coroutines.launch

class SaveReminderViewModel(
    val app: Application,
    private val dataSource: ReminderDataSource
) : BaseViewModel(app) {

    private val _reminderTitle = MutableLiveData<String?>()
    val reminderTitle: LiveData<String?>
        get() = _reminderTitle

    private val _reminderDescription = MutableLiveData<String?>()
    val reminderDescription: LiveData<String?>
        get() = _reminderDescription

    private val _reminderSelectedLocationStr = MutableLiveData<String?>()
    val reminderSelectedLocationStr: LiveData<String?>
        get() = _reminderSelectedLocationStr

    private val _selectedPOI = MutableLiveData<PointOfInterest?>()
    val selectedPOI: LiveData<PointOfInterest?>
        get() = _selectedPOI

    private val _latitude = MutableLiveData<Double?>()
    val latitude: LiveData<Double?>
        get() = _latitude

    private val _longitude = MutableLiveData<Double?>()
    val longitude: LiveData<Double?>
        get() = _longitude

    private val _currentLocation = MutableLiveData<Location?>()
    val currentLocation: LiveData<Location?>
        get() = _currentLocation

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        _reminderTitle.value = null
        _reminderDescription.value = null
        _reminderSelectedLocationStr.value = null
        _selectedPOI.value = null
        _latitude.value = null
        _longitude.value = null
    }

    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    fun validateAndSaveReminder(reminderData: ReminderDataItem) {
        if (validateEnteredData(reminderData)) {
            saveReminder(reminderData)
        }
    }

    /**
     * Save the reminder to the data source
     */
    private fun saveReminder(reminderData: ReminderDataItem) {
        showLoading.value = true

        viewModelScope.launch {
            dataSource.saveReminder(
                ReminderDTO(
                    reminderData.title,
                    reminderData.description,
                    reminderData.location,
                    reminderData.latitude,
                    reminderData.longitude,
                    reminderData.id
                )
            )
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(reminderData: ReminderDataItem): Boolean = when {
        reminderData.title.isNullOrEmpty() -> {
            showSnackBarInt.value = R.string.err_enter_title
            false
        }
        reminderData.location.isNullOrEmpty() -> {
            showSnackBarInt.value = R.string.err_select_location
            false
        }
        else -> true
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun requireCurrentLocation(activity: Activity) = viewModelScope.async {
        when (val result = activity.requireCurrentLocation()) {
            is CurrentLocationResult.Success -> _currentLocation.value = result.value
            is CurrentLocationResult.Error -> result.exception.printStackTrace()
        }
    }

    fun setSelectedPoi(context: Context, poi: PointOfInterest) {
        _selectedPOI.value = poi
        _reminderSelectedLocationStr.value = String.format(
            context.getString(R.string.lat_long_snippet),
            poi.latLng.latitude,
            poi.latLng.longitude
        )
        _latitude.value = _selectedPOI.value?.latLng?.latitude
        _latitude.value = _selectedPOI.value?.latLng?.longitude
    }

    fun onSaveLocationClicked(view: View) {
        if (_selectedPOI.value != null) {
            navigationCommand.value = NavigationCommand.Back
        } else {
            showErrorMessage.value = view.context.getString(R.string.select_poi)
        }
    }
}