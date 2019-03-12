package com.lind.vladimir.gallery

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import com.lind.vladimir.gallery.Utils.Companion.isOnline
import java.io.File
import java.io.Serializable
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
        for ((i, image) in list.withIndex()) {
            val file =
                File(INTERNAL_COVER_STORAGE + image.id + ".jpg")

            if (!file.exists()) {
                val url = URL(image.urls?.small)
                val btm = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                if(btm == null)
                {
                    if(!isOnline())
                    {
                        val intent = Intent()
                        intent.action = NO_INTERNET_BROADCAST_RECEIVER_TAG
                        sendBroadcast(intent)
                    }
                }
                else
                    Utils.saveToInternalStorage(this, "Covers", image.id + ".jpg", btm)
            }

            image.urls?.localCoverPath = INTERNAL_COVER_STORAGE +
                    image.id + ".jpg"

            val intent = Intent()
            intent.action = RECYCLER_BROADCAST_RECEIVER_TAG
            intent.putExtra("image", image)
            intent.putExtra("pos", i)

            sendBroadcast(intent)

            Log.i("loading", file.absolutePath)
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
