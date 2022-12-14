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
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private val viewModel by viewModels<AuthViewModel>()
    companion object {
        // the tag for log
        const val TAG = "AuthenticationActivity"
        // the code for sign in
        const val SIGN_IN_RESULT_CODE = 3001
    }

    private lateinit var homeBinding: ActivityAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                //if he authanticated in the past so we navigate to the reminderactivity
                AuthViewModel.AuthenticationState.AUTHENTICATED -> {
                    val i = Intent(this@AuthenticationActivity, RemindersActivity::class.java)
                    startActivity(i)
                }
            }
        })
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)
        homeBinding.loginButton.setOnClickListener { signInFlow() }
    }



    private fun signInFlow() {
        // Give users the option to sign in email or Google account
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers).build(), SIGN_IN_RESULT_CODE
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // when success sign in
                val i = Intent(this@AuthenticationActivity, RemindersActivity::class.java)
                startActivity(i)
//                Log.i(TAG, "Successfully signed in user " + "${FirebaseAuth.getInstance().currentUser?.displayName}!")
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }


}
