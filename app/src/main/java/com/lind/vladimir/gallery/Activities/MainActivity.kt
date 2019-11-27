package com.lind.vladimir.gallery.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lind.vladimir.gallery.Fragments.EndlessListFragment
import com.lind.vladimir.gallery.Fragments.FavouritesFragment
import com.lind.vladimir.gallery.INTERNAL_COVER_STORAGE
import com.lind.vladimir.gallery.R
import kotlinx.android.synthetic.main.main_activity.*


val RECYCLER_BROADCAST_RECEIVER_TAG = 0
val NO_INTERNET_BROADCAST_RECEIVER_TAG = 1

class MainActivity : AppCompatActivity() {

    var isFavourite = false

    val fragment = EndlessListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        INTERNAL_COVER_STORAGE = filesDir.parent + "/app_Covers/"

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container,
                fragment, "EndlessFragment").commitNow()
        }

        favs.setOnClickListener {
            if (!isFavourite) {
                supportFragmentManager.beginTransaction().replace(R.id.container,
                    FavouritesFragment(), "FavouritesFragment").commitNow()
            }
            else {
                supportFragmentManager.beginTransaction().replace(R.id.container,
                    fragment, "EndlessFragment").commitNow()
            }
            isFavourite = !isFavourite
        }

    }
}
