package org.wit.myapplication.main

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.myapplication.models.firebase.GamesFireStore
import org.wit.myapplication.models.stopwatch.Stopwatch
import org.wit.myapplication.models.stopwatch.StopwatchStore


class MainApp : Application(), AnkoLogger {


    // [START declare_auth]
    lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var firebasestore:  GamesFireStore
    lateinit var stopwatchstore: StopwatchStore
    // [END declare_auth]

    override fun onCreate() {
        super.onCreate()
        firebasestore = GamesFireStore(applicationContext)
        stopwatchstore = Stopwatch(applicationContext)
        info("Referee App started")
    }
}