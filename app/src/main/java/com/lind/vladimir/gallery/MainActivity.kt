package com.lind.vladimir.gallery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast


val RECYCLER_BROADCAST_RECEIVER_TAG = "RECYCLER_BROADCAST_RECEIVER_TAG"
val NO_INTERNET_BROADCAST_RECEIVER_TAG = "NO_INTERNET_BROADCAST_RECEIVER_TAG"
var list: MutableList<Image?> = mutableListOf()

class MainActivity : AppCompatActivity() {

    var recyclerReceiver: MessageReceiver? = MessageReceiver()
    val fragment = PreviewFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container,
                fragment,"previewFragment").commit()
        }
    }

    override fun onResume() {
        super.onResume()

        recyclerReceiver = MessageReceiver()
        registerReceiver(recyclerReceiver, IntentFilter(RECYCLER_BROADCAST_RECEIVER_TAG))
    }


    override fun onStop() {
        super.onStop()
        if (recyclerReceiver != null) {
            unregisterReceiver(recyclerReceiver)
            recyclerReceiver = null
        }
    }


    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            when (intent.action) {
                RECYCLER_BROADCAST_RECEIVER_TAG -> {
                    synchronized(list) {
                        val image = intent.getSerializableExtra("image") as Image
                        val pos = intent.getIntExtra("pos", -1)
                        val notifyPos = IMAGE_PER_PAGE * (image.page!! - 1) + pos
                        Log.i("adapterBR", notifyPos.toString() + ":" + image.page)
                        Log.i("adapterBRImage", image.toString())
                        list[notifyPos] = image

                        fragment.adapter.notifyItemChanged(notifyPos)

                    }
                }
                NO_INTERNET_BROADCAST_RECEIVER_TAG -> {
                    Toast.makeText(context, "Need internet connection", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
}
