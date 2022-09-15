package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity


class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding

    private val viewModel by viewModels<AuthViewModel>()

    companion object {
        const val TAG = "Auth_Activity"
        const val SIGN_IN_RESULT_CODE = 45
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityAuthenticationBinding>(
            this,
            R.layout.activity_authentication
        )
        viewModel.authState.observe(this, Observer { authenticationState ->

            when (authenticationState) {

                AuthViewModel.AuthenticationState.AUTHENTICATED -> {
                    startActivity(Intent(this, RemindersActivity::class.java))
                    finish()
                }
                else -> {

                    binding.signinButton.setOnClickListener {
                        startSignIn()
                    }
                }
            }
        })
        binding.signinButton.setOnClickListener {
            startSignIn()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )


            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }


    private fun startSignIn() {

        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), SIGN_IN_RESULT_CODE)
    }

}
