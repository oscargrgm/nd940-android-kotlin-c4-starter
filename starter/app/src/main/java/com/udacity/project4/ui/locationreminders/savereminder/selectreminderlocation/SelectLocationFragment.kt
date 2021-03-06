package com.udacity.project4.ui.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.ui.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.extension.addLatLngMarker
import com.udacity.project4.utils.extension.enableLocation
import com.udacity.project4.utils.extension.onLongClick
import com.udacity.project4.utils.extension.setCurrentLocation
import com.udacity.project4.utils.extension.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.extension.setStyle
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectLocationFragment : BaseFragment() {

    // Use Koin to get the view model of the SaveReminder
    override val viewModel: SaveReminderViewModel by viewModel()

    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap

    @SuppressLint("MissingPermission")
    private val locationContract = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            map.enableLocation()
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap

            map.run {
                setStyle(requireContext(), R.raw.map_style)
                onLongClick { currentMap, latLng ->
                    onLocationSelected(currentMap, latLng)
                }
            }
            locationContract.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        viewModel.currentLocation.observe(viewLifecycleOwner, ::onCurrentLocation)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun onLocationSelected(currentMap: GoogleMap, latLng: LatLng) {
        // TODO: When the user confirms on the selected location,
        //  send back the selected location details to the view model
        //  and navigate back to the previous fragment to save the reminder and add the geofence
        currentMap.addLatLngMarker("", latLng)
        viewModel.saveLatLng(latLng)
    }

    private fun onCurrentLocation(location: Location?) {
        location?.let {
            map.setCurrentLocation(it)
        }
    }
}
