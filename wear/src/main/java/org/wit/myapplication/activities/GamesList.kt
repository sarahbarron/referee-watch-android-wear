package org.wit.myapplication.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.google.firebase.firestore.FirebaseFirestore
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp


class GamesList : AppCompatActivity(),
        AmbientModeSupport.AmbientCallbackProvider, MenuItem.OnMenuItemClickListener,
        WearableNavigationDrawerView.OnItemSelectedListener {

        lateinit var app: MainApp
        lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_list)
        app = application as MainApp
        db = FirebaseFirestore.getInstance()

        val user = app.auth.currentUser
        Log.d(TAG,"UID : "+user.uid)
        val referee = db.collection("Member").document(user.uid)
        Log.d(TAG,"Referee: " + referee)


        db!!.collection("Game").whereEqualTo("referee", referee)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        for (document in task.result!!) {
                            Log.d(TAG, "Game : " + document.id + " => " + document.data)
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                 }
          }
    }


    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(pos: Int) {
        TODO("Not yet implemented")
    }
    companion object {
        private const val TAG = "Game List Activity"
    }
}