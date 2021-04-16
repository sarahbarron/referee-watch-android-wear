package org.wit.myapplication.activities
import android.os.Bundle
import android.app.Activity
import android.view.MenuItem
import androidx.wear.widget.WearableRecyclerView
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.drawer.WearableNavigationDrawerView

import org.wit.myapplication.R

class GamesList : AppCompatActivity(),
        AmbientModeSupport.AmbientCallbackProvider, MenuItem.OnMenuItemClickListener,
        WearableNavigationDrawerView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_list)
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
}