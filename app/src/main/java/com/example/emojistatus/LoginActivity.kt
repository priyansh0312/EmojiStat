package com.example.emojistatus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class LoginActivity : AppCompatActivity(){

    private lateinit var btnSignIn : Button  // we can do this or we can bind the layout file to the activity

//    it is a way to hold all the static and constant variables in your class - same as CONST for class
    private companion object {
        private const val TAG  = "LoginActivity"  // since we are going to login something, we make another variable - convention - always use TAG name as the class name
        private const val RC_SIGN_IN = 31299
    }

    private lateinit var auth: FirebaseAuth  // 4. shared instance of the firebase auth - copied from google

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnSignIn = findViewById<View>(R.id.btnSignIn) as SignInButton  // we can do this or we can bind the layout file to the activity

        auth = Firebase.auth  // 5. firebase authentication object

        // 1. Configure Google Sign In - copy from firebase authentication site :  https://tinyurl.com/yzojmgpx
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 2. set on click listener for the button and using above created client - copy from firebase authentication page
        btnSignIn.setOnClickListener{
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)  // RC_SIGN_IN is used as a request code and should be defined inside of a companion object
        }
    }


    // 6. checking if the user is already logged in, then we directly go to the feed. OnStart is a lifecycle method on the acitivity that happens after the onCreate
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    
    // 8. logic code to start new activity when the sign in is successful
    private fun updateUI(currentUser: FirebaseUser?) {
        // navigate to the main Activity

        // if user is null :  redo the login ; fucking stupid user
        // else we move to the next activity
        if (currentUser == null){
            Log.w(TAG, "Login Failed. Please try again!")
            return
        }
        else{
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // this removes the loginActivity from the backstack and when we press back, we do not go to the LoginActivity again
        }
    }


    // 3. used to capture the response that startActivityForResult is gonna give us when we click the button
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)  // call firebaseAuthWithGoogle
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // 7. copied from firebase authentication page - this function was used in the above function when the user logs
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }


}
