package com.lind.vladimir.gallery.Fragments

import android.content.Context
import android.widget.Toast
import com.lind.vladimir.gallery.Entities.Image
import com.lind.vladimir.gallery.R
import com.lind.vladimir.gallery.Services.ImageLoaderService
import com.lind.vladimir.gallery.Services.ImageLoaderServiceReceiver
import com.lind.vladimir.gallery.Services.LoadQueueStatus
import com.lind.vladimir.gallery.Services.LoadStatus
import com.lind.vladimir.gallery.Utils.Utils
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class EndlessListFragment : ImagesPreviewFragment() {
    override var limit: Int = -1

    override fun onResume() {
        super.onResume()
        initUI()
    }

    private fun initUI() {
        activity!!.toolbar.title = getString(R.string.app_name)
    }

    override fun uploadNewImages(context: Context, page: Int,
                                 receiver: ImageLoaderServiceReceiver?) {
        ImageLoaderService.imageLoadQueue[page] = LoadStatus(0, LoadQueueStatus.START)

        GlobalScope.launch(Dispatchers.IO) {
            var list: List<Image> = mutableListOf()
            try {
                list = Utils.getImagesList(context, page)
            } catch (e: Throwable) {
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(context,
                        context.getString(R.string.no_internet),
                        Toast.LENGTH_SHORT).show()
                    ImageLoaderService.imageLoadQueue[page]?.status = LoadQueueStatus.FAILED
                }
                return@launch
            } catch (e: IllegalStateException) {
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(context,
                        e.message,
                        Toast.LENGTH_SHORT).show()
                    ImageLoaderService.imageLoadQueue[page]?.status = LoadQueueStatus.FAILED
                }
                return@launch
            }
            ImageLoaderService.startLoading(context, list, page, receiver)
        }
    }

}
