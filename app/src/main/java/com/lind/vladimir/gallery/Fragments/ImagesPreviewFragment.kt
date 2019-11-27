package com.lind.vladimir.gallery.Fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lind.vladimir.gallery.Activities.NO_INTERNET_BROADCAST_RECEIVER_TAG
import com.lind.vladimir.gallery.Activities.RECYCLER_BROADCAST_RECEIVER_TAG
import com.lind.vladimir.gallery.Adapters.PreviewAdapter
import com.lind.vladimir.gallery.Database.LocalDatabaseAPI
import com.lind.vladimir.gallery.Entities.Image
import com.lind.vladimir.gallery.IMAGE_PER_PAGE
import com.lind.vladimir.gallery.R
import com.lind.vladimir.gallery.Services.ImageLoaderServiceReceiver
import com.lind.vladimir.gallery.Utils.WrapContentGridLayoutManager
import kotlinx.android.synthetic.main.preview_fragment.*


interface DataProviderI {
    fun uploadNewImages(context: Context, page: Int, receiver: ImageLoaderServiceReceiver?)
}

abstract class ImagesPreviewFragment : Fragment(),
    ImageLoaderServiceReceiver.Receiver, DataProviderI {

    var adapter: PreviewAdapter? = null
    private var receiver: ImageLoaderServiceReceiver? = ImageLoaderServiceReceiver(Handler())


    abstract var limit: Int
    lateinit var db: LocalDatabaseAPI

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        db = LocalDatabaseAPI(context!!)
        adapter = PreviewAdapter(this, this, receiver, db, limit)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.preview_fragment, null)
    }

    override fun onResume() {
        receiver!!.setReceiver(this)
        initPreviewGrid()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        receiver!!.setReceiver(null)
    }

    private fun initPreviewGrid() {
        view.apply {
            val layoutManager =
                WrapContentGridLayoutManager(context!!, 2)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = layoutManager
            recyclerView.isNestedScrollingEnabled = true
            recyclerView.adapter = adapter
        }
    }

    override fun onReceiveResult(resultCode: Int, data: Bundle) {
        when (resultCode) {
            RECYCLER_BROADCAST_RECEIVER_TAG -> {
                adapter!!.apply {
                    synchronized(list) {

                        val image = data.getSerializable("image") as Image
                        val pos = data.getInt("pos", -1)
                        val notifyPos = IMAGE_PER_PAGE * (image.page!! - 1) + pos
                        Log.i("adapterBR", notifyPos.toString() + ":" + image.page)
                        Log.i("adapterBRImage", image.toString())
                        if (list[notifyPos] != image) {
                            list[notifyPos] = image
                            notifyItemChanged(notifyPos)
                        }

                    }
                }

            }
            NO_INTERNET_BROADCAST_RECEIVER_TAG -> {
                Log.i("PreviewReceiver", "Need internet connection")
                Toast.makeText(context, "Need internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }


    abstract override fun uploadNewImages(context: Context, page: Int,
                                          receiver: ImageLoaderServiceReceiver?)

}