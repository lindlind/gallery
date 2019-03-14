package com.lind.vladimir.gallery.Fragments

import android.accounts.NetworkErrorException
import android.content.Context
import com.lind.vladimir.gallery.Database.LocalDatabaseAPI
import com.lind.vladimir.gallery.Entities.Image
import com.lind.vladimir.gallery.R
import com.lind.vladimir.gallery.Services.ImageLoaderService
import com.lind.vladimir.gallery.Services.ImageLoaderServiceReceiver
import com.lind.vladimir.gallery.Services.LoadQueueStatus
import com.lind.vladimir.gallery.Services.LoadStatus
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FavouritesFragment : ImagesPreviewFragment() {
    override var limit: Int = -1

    override fun onAttach(context: Context?) {
        db = LocalDatabaseAPI(context!!)
        limit = db.getImagesCount()
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        initUI()
    }

    override fun onDestroyView() {
        onDestroyUI()
        super.onDestroyView()
    }

    private fun onDestroyUI() {
        ImageLoaderService.imageLoadQueue.clear()
        activity!!.toolbar.title = getString(R.string.app_name)
        activity!!.favs.setImageResource(R.drawable.like_outline)

    }

    private fun initUI() {
        ImageLoaderService.imageLoadQueue.clear()
        activity!!.toolbar.title = "Favourites"
        activity!!.favs.setImageResource(R.drawable.like_fill)
    }

    override fun uploadNewImages(context: Context, page: Int,
                                 receiver: ImageLoaderServiceReceiver?) {
        ImageLoaderService.imageLoadQueue[page] =
                LoadStatus(0,
                    LoadQueueStatus.START)

        GlobalScope.launch(Dispatchers.IO) {
            var list: List<Image> = mutableListOf()
            try {
                list = db.getFavouritesFromDatabase(page)
            } catch (e: NetworkErrorException) {
                return@launch
            }
            ImageLoaderService.startLoading(context,
                list,
                page,
                receiver)
        }
    }

}
