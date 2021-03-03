package com.udacity.project4.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseUser
import com.udacity.project4.ui.authentication.data.FirebaseUserLiveData

enum class AuthenticationState {
    AUTHENTICATED, NOT_AUTHENTICATED;
}

class AuthenticationViewModel : ViewModel() {

    private val _authenticationState = FirebaseUserLiveData().map { firebaseUser: FirebaseUser? ->
        if (firebaseUser != null) AuthenticationState.AUTHENTICATED
        else AuthenticationState.NOT_AUTHENTICATED
    }
    val authenticationState: LiveData<AuthenticationState>
        get() = _authenticationState

}