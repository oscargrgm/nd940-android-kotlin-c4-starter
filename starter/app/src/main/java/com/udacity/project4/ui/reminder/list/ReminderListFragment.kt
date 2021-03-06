package com.udacity.project4.ui.reminder.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.utils.extension.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.extension.setTitle
import com.udacity.project4.utils.extension.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {

    // Use Koin to retrieve the ViewModel instance
    override val viewModel: RemindersListViewModel by viewModel()

    private lateinit var binding: FragmentRemindersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reminders,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))
        setupRecyclerView()

        binding.refreshLayout.setOnRefreshListener { viewModel.loadReminders() }

        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // load the reminders list on the ui
        viewModel.loadReminders()
    }

    private fun navigateToAddReminder() {
        // use the navigationCommand live data to navigate between the fragments
        viewModel.navigationCommand.postValue(
            NavigationCommand.To(ReminderListFragmentDirections.toSaveReminderFragment())
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {}

        // setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                // TODO: add the logout implementation
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }

}
