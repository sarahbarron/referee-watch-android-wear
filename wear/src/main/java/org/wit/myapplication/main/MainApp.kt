package org.wit.myapplication.main

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.myapplication.models.firebase.GamesFireStore


class MainApp : Application(), AnkoLogger {


    // [START declare_auth]
    lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var firebasestore:  GamesFireStore
    // [END declare_auth]

    override fun onCreate() {
        super.onCreate()
        firebasestore = GamesFireStore(applicationContext)
        info("Referee App started")
    }
}