package com.lind.vladimir.gallery.Services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import com.lind.vladimir.gallery.Activities.NO_INTERNET_BROADCAST_RECEIVER_TAG
import com.lind.vladimir.gallery.Activities.RECYCLER_BROADCAST_RECEIVER_TAG
import com.lind.vladimir.gallery.Entities.Image
import com.lind.vladimir.gallery.INTERNAL_COVER_STORAGE
import com.lind.vladimir.gallery.Services.ImageLoaderServiceReceiver.Companion.IMAGE_LOADER_RECEIVER_TAG
import com.lind.vladimir.gallery.Utils.Utils
import com.lind.vladimir.gallery.Utils.Utils.Companion.isOnline
import java.io.File
import java.io.Serializable
import java.net.URL
import java.util.concurrent.Executors


object LoadQueueStatus {
    const val START = 0
    const val DONE = 1
    const val RUNNING = 2
    const val FAILED = 3
}

data class LoadStatus(var loadedCount: Int, var status: Int) : Serializable


class ImageLoaderService : IntentService("ImageLoaderService") {

    var receiver: ResultReceiver? = null

    override fun onHandleIntent(intent: Intent?) {
        receiver = intent?.getParcelableExtra(IMAGE_LOADER_RECEIVER_TAG)

        when (intent?.action) {
            "LoadImages" -> {
                val images = intent.getSerializableExtra("imagesList") as List<Image>
                val page = intent.getIntExtra("page", -1)

                handler(images, page)
            }
        }
    }

    private fun handler(list: List<Image>, page: Int) {

        val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)

        for ((i, image) in list.withIndex()) {
            executor.submit {

                imageLoadQueue[page]?.status =
                        LoadQueueStatus.RUNNING
                // val absPos = (image.page!! - 1) * IMAGE_PER_PAGE + i
                val file =
                    File(INTERNAL_COVER_STORAGE + image.id + ".jpg")
                if (!file.exists()) {
                    val url = URL(image.urls?.small)
                    val stream = url.openConnection().getInputStream()
                    val btm = BitmapFactory.decodeStream(stream)
                    stream.close()

                    if (btm == null) {
                        if (!isOnline(this)) {
                            val data = Bundle()
                            receiver?.send(NO_INTERNET_BROADCAST_RECEIVER_TAG, data)
                        }
                        Log.i("loadingBTM", "BTM NULL!")
                    }
                    else
                        Utils.saveToInternalStorage(this, "Covers", image.id + ".jpg", btm)
                }

                image.urls?.localCoverPath = INTERNAL_COVER_STORAGE +
                        image.id + ".jpg"

                val data = Bundle()
                data.putSerializable("image", image as Serializable)
                data.putInt("pos", i)
                receiver?.send(RECYCLER_BROADCAST_RECEIVER_TAG, data)

                imageLoadQueue[page]?.loadedCount?.inc()

                if (imageLoadQueue[page]?.loadedCount == list.size)
                    imageLoadQueue[page]?.status =
                            LoadQueueStatus.DONE
                Log.i("loading", file.absolutePath)
            }
        }

    }

    companion object {
        val imageLoadQueue: MutableMap<Int, LoadStatus> = mutableMapOf()

        @JvmStatic
        fun startLoading(context: Context, list: List<Image>, page: Int,
                         receiver: ImageLoaderServiceReceiver?) {
            val intent = Intent(context, ImageLoaderService::class.java).apply {
                action = "LoadImages"
                putExtra("imagesList", list as Serializable)
                putExtra("page", page)
                putExtra(IMAGE_LOADER_RECEIVER_TAG, receiver)
            }
            context.startService(intent)
        }
    }
}

class ImageLoaderServiceReceiver(handler: Handler) : ResultReceiver(handler) {

    companion object {
        const val IMAGE_LOADER_RECEIVER_TAG = "IMAGE_LOADER_RECEIVER_TAG"
    }

    private var mReceiver: Receiver? = null

    interface Receiver {
        fun onReceiveResult(resultCode: Int, data: Bundle)
    }

    fun setReceiver(receiver: Receiver?) {
        mReceiver = receiver
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        if (mReceiver != null) {
            mReceiver!!.onReceiveResult(resultCode, resultData)
        }
    }
}