package com.udacity.project4.ui.reminder.save.location

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.ui.reminder.save.SaveReminderViewModel
import com.udacity.project4.utils.extension.addPoiMarker
import com.udacity.project4.utils.extension.enableLocation
import com.udacity.project4.utils.extension.onPoiClick
import com.udacity.project4.utils.extension.setCurrentLocation
import com.udacity.project4.utils.extension.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.extension.setStyle
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment() {

    // Use Koin to get the view model of the SaveReminder
    override val viewModel by inject<SaveReminderViewModel>()

    private lateinit var binding: FragmentSelectLocationBinding

    private var map: GoogleMap? = null

    @SuppressLint("MissingPermission")
    private val locationContract = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            map?.enableLocation()
            viewModel.requireCurrentLocation(requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_location,
            container,
            false
        )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap

            map?.setStyle(requireContext(), R.raw.map_style)
            map?.onPoiClick { _, poi -> viewModel.setSelectedPoi(requireContext(), poi) }

            locationContract.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        viewModel.currentLocation.observe(viewLifecycleOwner, ::onCurrentLocation)
        viewModel.selectedPOI.observe(viewLifecycleOwner, ::onSelectedPoi)
        viewModel.showErrorMessage.observe(viewLifecycleOwner, ::onErrorMessage)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun onCurrentLocation(location: Location?) {
        location?.let {
            map?.setCurrentLocation(it)
        }
    }

    private fun onSelectedPoi(selectedPoi: PointOfInterest?) {
        selectedPoi?.let { map?.addPoiMarker(it) }
    }

    private fun onErrorMessage(message: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.no_data))
            .setMessage(message ?: requireContext().getString(R.string.select_poi))
            .setPositiveButton(requireContext().getString(android.R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
