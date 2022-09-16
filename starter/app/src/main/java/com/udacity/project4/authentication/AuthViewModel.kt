package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AuthViewModel : ViewModel()  {
    // for types of auth
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    //mapping for each state to corresponding value
    val authState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

}