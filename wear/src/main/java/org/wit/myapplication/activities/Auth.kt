package org.wit.myapplication.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.startActivity
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.firebase.GamesFireStore


/**
 * Demonstrates using Google Sign-In on Android Wear
 */
class Auth : ComponentActivity() {

    private var mSignInButton: SignInButton? = null
    private var mSignOutButton: Button? = null
    lateinit var app: MainApp
    lateinit var loader: AlertDialog
    lateinit var db : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        app = application as MainApp




        // Set up the sign in button.
        mSignInButton = findViewById(R.id.sign_in_button)
        mSignInButton?.setOnClickListener(View.OnClickListener { signIn() })

        // Set up the sign out button.
//        mSignOutButton = findViewById(R.id.sign_out_button)
//        mSignOutButton?.setOnClickListener(View.OnClickListener { signOut() })
   //     checkAlreadySignedIn()

        // Initialise Firebase Auth
        app.auth = FirebaseAuth.getInstance()
        // Initialise Firestore
        db = FirebaseFirestore.getInstance()
        setupGoogleSignInClient()
    }


    /**
     * Configures the GoogleApiClient used for sign in. Requests scopes profile and email.
     */
    protected fun setupGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        app.mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


    }
//    private fun checkAlreadySignedIn() {
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        updateUi(account)
//    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
                TAG,
                "Activity request code: $requestCode"
        )
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                   // handleSignInResult(task)
                }catch (e: ApiException){
                    Log.w(TAG, "Google Sign In Failed: ", e)
                    Toast.makeText(this, "SignIn Failed", Toast.LENGTH_LONG)
                    updateUi(null)
                }
        }
    }



//    protected fun handleSignInResult(completedTask: Task<GoogleSignInAccount?>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//
//            // Signed in successfully, show authenticated UI.
//            updateUi(account)
//        } catch (e: ApiException) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(
//                TAG,
//                "signInResult:failed code=" + e.statusCode + ". Msg=" + GoogleSignInStatusCodes.getStatusCodeString(
//                    e.statusCode
//                )
//            )
//            updateUi(null)
//        }
//    }

    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, R.string.google_signin_successful, Toast.LENGTH_SHORT).show()
            mSignInButton!!.visibility = View.GONE
            //mSignOutButton!!.visibility = View.VISIBLE

            startActivity<GamesList>()
        } else {
            mSignInButton!!.visibility = View.VISIBLE
          //  mSignOutButton!!.visibility = View.GONE
        }
    }

    /**
     * Starts Google sign in activity, response handled in onActivityResult.
     */
    private fun signIn() {
        if (app.mGoogleSignInClient == null) {
            Log.e(TAG, "Google Sign In API client not initialized.")
            return
        }
        val signInIntent = app.mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    /**
     * Signs the user out and resets the sign-in button to visible.
     */
    private fun signOut() {
        if (app.mGoogleSignInClient == null) {
            Log.e(TAG, "Google Sign In API client not initialized.")
            return
        }
        app.mGoogleSignInClient!!.signOut().addOnCompleteListener(this) {
            updateUi(null)
            Toast.makeText(
                    this,
                    R.string.signout_successful,
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    // [START auth_with_google]
    @SuppressLint("NewApi")
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        app.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = app.auth.currentUser



                    db.collection("Member").document(user.uid)
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result!!

//                                    Check if the authenticated user is a member & if they are a referee
                                    if (document != null)
                                    {
                                        val isClubRef = document.data?.get("refereeOfClub")
                                        val isCountyRef = document.data?.get("refereeOfCounty")

                                        if(isClubRef == true  || isCountyRef == true ) {
                                            Log.d(TAG, document.id + " => " + isClubRef)
                                            app.firebasestore.fetchGames()
                                            val games =  app.firebasestore.findAllGames()
                                            // get the teams involved in each game
                                            for(game in games)
                                            {
                                                game.teamA?.let { app.firebasestore.fetchTeam(it) }
                                                game.teamB?.let { app.firebasestore.fetchTeam(it) }
                                            }

                                            Log.i(TAG, "fetchGames: $games")
                                            updateUi(user)
                                        }
                                        else{
                                            Log.w(TAG, "Error getting documents.", task.exception)
                                            Toast.makeText(
                                                    this,
                                                    "SignIn Failed",
                                                    Toast.LENGTH_LONG
                                            ).show()
                                            updateUi(null)
                                        }
                                    }
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.exception)
                                    Toast.makeText(
                                            this,
                                            "SignIn Failed",
                                            Toast.LENGTH_LONG
                                    ).show()
                                    updateUi(null)
                                }
                            }




                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                            this,
                            "SignIn Failed",
                            Toast.LENGTH_LONG
                    ).show()
                    updateUi(null)
                }
            }
    }

    companion object {
        private const val TAG = "GoogleSignInActivity"
        const val REQUEST_CODE_SIGN_IN = 8001
    }
}