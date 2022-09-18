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
        // if we have user so he is authanticated
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            // if we haven't user so he isn't authanticated
            AuthenticationState.UNAUTHENTICATED
        }
    }

}