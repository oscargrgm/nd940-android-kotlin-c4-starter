package com.udacity.project4

import android.app.Application
import com.udacity.project4.ui.authentication.AuthenticationViewModel
import com.udacity.project4.data.local.LocalDB
import com.udacity.project4.data.local.RemindersLocalRepository
import com.udacity.project4.ui.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.ui.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            // This view model is declared singleton to be used across multiple fragments
            single { LocalDB.createRemindersDao(this@MyApp) }
            single { RemindersLocalRepository(get()) }

            viewModel { AuthenticationViewModel() }
            viewModel { SaveReminderViewModel(get(), get() as RemindersLocalRepository) }
            viewModel { RemindersListViewModel(get(), get() as RemindersLocalRepository) }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}