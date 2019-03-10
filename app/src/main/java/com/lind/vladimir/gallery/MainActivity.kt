package com.lind.vladimir.gallery

import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.preview_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.support.v7.util.DiffUtil
import android.graphics.BitmapFactory
import android.util.Log
import okhttp3.internal.Util

val RECYCLER_BROADCAST_RECEIVER_TAG = "RECYCLER_BROADCAST_RECEIVER_TAG"

class MainActivity : AppCompatActivity() {
    val adapter: PreviewAdapter = PreviewAdapter(mutableListOf())
    var recyclerReceiver: MessageReceiver? = MessageReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        initPreviewGrid()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(recyclerReceiver, IntentFilter(RECYCLER_BROADCAST_RECEIVER_TAG))
    }

    override fun onStop() {
        if (recyclerReceiver!=null) {
            unregisterReceiver(recyclerReceiver);
            recyclerReceiver=null
        }
        super.onStop()
    }


    private fun initPreviewGrid() {
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.adapter = adapter
    }


    inner class MessageReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val image = intent.getSerializableExtra("image") as Image
            adapter.list.add(image)
            adapter.notifyItemInserted(adapter.itemCount - 1)
        }
    }
}


class ImageDiffUtilCallback(private val oldList: List<Image>, private val newList: List<Image>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProduct = oldList[oldItemPosition]
        val newProduct = newList[newItemPosition]
        return oldProduct.id.equals(newProduct.id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProduct = oldList[oldItemPosition]
        val newProduct = newList[newItemPosition]
        return oldProduct.urls?.small.equals(newProduct.urls?.small)
    }
}

class PreviewAdapter(var list: MutableList<Image> = mutableListOf()) : RecyclerView.Adapter<PreviewAdapter.previewHolder>() {

    var page = 1;
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        Utils.uploadNewImages(recyclerView.context,page)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager!!
                val visibleItemCount = layoutManager.getChildCount();
                val totalItemCount = layoutManager.getItemCount();
                val pastVisiblesItems = (layoutManager as GridLayoutManager).findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    page++;
                    Utils.uploadNewImages(recyclerView.context, page)
                }
            }
        })
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): previewHolder {

        val view = LayoutInflater.from(p0.context).inflate(R.layout.preview_item, null)

        return previewHolder(view)
    }

    fun update(newData: MutableList<Image>) {
        val productDiffUtilCallback = ImageDiffUtilCallback(list, newData)
        val diffUtils = DiffUtil.calculateDiff(productDiffUtilCallback)
        diffUtils.dispatchUpdatesTo(this)

        list = newData
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: previewHolder, position: Int) {
        holder.imageView.setBackgroundColor(R.color.colorAccent)
        GlobalScope.launch(Dispatchers.IO) {
            val btm = BitmapFactory.decodeFile(list[position].urls?.localCoverPath)
            Log.e("Image" + position, list[position].urls?.localCoverPath)
            GlobalScope.launch(Dispatchers.Main) {
                holder.imageView.setImageBitmap(btm)
            }
        }
    }

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.image
    }


}