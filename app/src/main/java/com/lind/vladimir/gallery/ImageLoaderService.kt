package com.lind.vladimir.gallery

import android.app.ActionBar
import android.app.Activity
import android.app.IntentService
import android.content.ComponentName
import android.content.Intent
import android.content.Context
import android.content.ServiceConnection
import java.io.Serializable
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL


class ImageLoaderService : IntentService("ImageLoaderService") {


    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            "LoadImages" -> {
                val images = intent.getSerializableExtra("imagesList") as List<Image>
                handler(images)
            }
        }
    }

    private fun handler(list: List<Image>) {
        for (image in list) {
            val file = File("/data/user/0/com.lind.vladimir.gallery/app_Covers/" + image.id + ".jpg")

            if (!file.exists()) {
                val url = URL(image.urls?.small)
                val btm = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                Utils.saveToInternalStorage(this, "Covers", image.id + ".jpg", btm)
            }

            image.urls?.localCoverPath = "/data/user/0/com.lind.vladimir.gallery/app_Covers/" + image.id + ".jpg"

            val intent = Intent()
            intent.action = RECYCLER_BROADCAST_RECEIVER_TAG
            intent.putExtra("image", image)
            sendBroadcast(intent)

            Log.i("loding",file.absolutePath)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {

        @JvmStatic
        fun startLoading(context: Context, list: List<Image>) {
                val intent = Intent(context, ImageLoaderService::class.java).apply {
                    action = "LoadImages"
                    putExtra("imagesList", list as Serializable)
                }
                context.startService(intent)
        }
    }
}
