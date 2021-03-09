package com.udacity.project4.ui.reminder.save

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.ui.reminder.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.ui.reminder.list.ReminderDataItem
import com.udacity.project4.utils.GEOFENCE_EXPIRATION_IN_MILLISECONDS
import com.udacity.project4.utils.GEOFENCE_RADIUS_IN_METERS
import com.udacity.project4.utils.extension.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {

    // Get the view model this time as a single to be shared with the another fragment
    override val viewModel by inject<SaveReminderViewModel>()

    private lateinit var binding: FragmentSaveReminderBinding

    private val geofencingClient: GeofencingClient by lazy {
        LocationServices.getGeofencingClient(requireActivity())
    }
    private val geofencingPendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java).apply {
            action = ACTION_GEOFENCE_EVENT
        }
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_save_reminder,
            container,
            false
        )

        setDisplayHomeAsUpEnabled(true)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectLocation.setOnClickListener {
            // Navigate to another fragment to get the user location
            viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.toSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title: String = binding.reminderTitle.text.toString()
            val description: String = binding.reminderDescription.text.toString()
            val location: String? = viewModel.reminderSelectedLocationStr.value
            val latitude: Double? = viewModel.latitude.value
            val longitude: Double? = viewModel.longitude.value

            val dataItem = ReminderDataItem(title, description, location, latitude, longitude)
            createGeofence(dataItem)
            viewModel.validateAndSaveReminder(dataItem)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // make sure to clear the view model after destroy, as it's a single view model.
        viewModel.onClear()
    }

    @SuppressLint("MissingPermission")
    private fun createGeofence(dataItem: ReminderDataItem) {
        if (dataItem.latitude == null || dataItem.longitude == null) return

        val geofence = Geofence.Builder()
            .setRequestId(dataItem.id)
            .setCircularRegion(
                dataItem.latitude!!,
                dataItem.longitude!!,
                GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        try {
            geofencingClient.addGeofences(geofencingRequest, geofencingPendingIntent).run {
                addOnSuccessListener {
                    Log.d(TAG, "Geofences added")
                    requireContext().toast("Geofences added")
                }
                addOnFailureListener {
                    Log.e(TAG, "Problem adding geofences")
                    requireContext().toast("Problem adding geofences")
                    it.printStackTrace()
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    private fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ACTION_GEOFENCE_EVENT =
            "com.udacity.project4.ui.reminder.save.SaveReminderFragment.action_geofence_event"

        private const val TAG: String = "SaveReminderFragment"
    }
}
