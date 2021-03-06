package com.udacity.project4.ui.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.ui.reminder.RemindersActivity
import com.udacity.project4.utils.extension.launchActivityAndFinish
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This class should be the starting point of the app, It asks the users to sign in / register,
 * and redirects the signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val viewModel: AuthenticationViewModel by viewModel()

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        binding.lifecycleOwner = this

        binding.bnSignIn.setOnClickListener { launchSignIn() }

        viewModel.authenticationState.observe(this, ::onAuthenticationState)

        // TODO: a bonus is to customize the sign in flow to look nice using :
        // https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
    }

    private fun launchSignIn() {
        val signInProviders = listOf(SIGN_IN_PROVIDER_EMAIL, SIGN_IN_PROVIDER_GOOGLE)
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(signInProviders)
            .setIsSmartLockEnabled(false, false)
            .build()

        startActivity(signInIntent)
    }

    private fun onAuthenticationState(authenticationState: AuthenticationState) {
        when (authenticationState) {
            AuthenticationState.AUTHENTICATED -> launchActivityAndFinish<RemindersActivity>()
            AuthenticationState.NOT_AUTHENTICATED -> {
                // TODO Navigate to register
            }
        }
    }

    companion object {
        private val SIGN_IN_PROVIDER_EMAIL = AuthUI.IdpConfig.EmailBuilder().build()
        private val SIGN_IN_PROVIDER_GOOGLE = AuthUI.IdpConfig.GoogleBuilder().build()
    }
}
