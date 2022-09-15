package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AuthViewModel : ViewModel()  {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }


    val authState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

}