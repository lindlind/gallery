package com.lind.vladimir.gallery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.util.LruCache
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionListenerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.detail_fragment.*
import kotlinx.android.synthetic.main.detail_fragment.view.*
import kotlinx.android.synthetic.main.preview_fragment.*
import kotlinx.android.synthetic.main.preview_fragment.view.*
import kotlinx.android.synthetic.main.preview_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable

class PreviewFragment : Fragment() {
    var adapter: PreviewFragment.PreviewAdapter = PreviewAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.preview_fragment, null)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPreviewGrid()
    }

    override fun onResume() {
        super.onResume()


    }


    override fun onStop() {
        super.onStop()

    }

    private fun initPreviewGrid() {

        view.apply {
            val layoutManager = GridLayoutManager(context, 2)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = layoutManager
            recyclerView.isNestedScrollingEnabled = true
            recyclerView.adapter = adapter
        }
    }


    inner class PreviewAdapter() :
        RecyclerView.Adapter<PreviewAdapter.previewHolder>() {

        private val previews: LruCache<String, Bitmap?>

        init {
            val maxMemory = Runtime.getRuntime().maxMemory().toInt()
            val cacheSize = maxMemory / 4

            previews = object : LruCache<String, Bitmap?>(cacheSize) {
                override fun sizeOf(key: String, value: Bitmap): Int {
                    return value.byteCount
                }
            }
        }

        var page = 1

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)

            if(page == 1)
                uploadMore()

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager!!
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisiblesItems =
                        (layoutManager as GridLayoutManager).findFirstVisibleItemPosition()

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        page++
                        uploadMore()
                    }
                }
            })
        }

        fun uploadMore() {
            synchronized(list) {
                for (i in 0 until IMAGE_PER_PAGE) {
                    list.add(null)
                    notifyItemInserted(list.size - 1)
                }
            }

            Utils.uploadNewImages(recyclerView.context, page)
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): previewHolder {

            val view = LayoutInflater.from(p0.context).inflate(R.layout.preview_item, null)
            return previewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: previewHolder, position: Int) {
            val image = list[position]

            Log.i("recycler$position",image.toString()?:"")

            if (image == null) {
                holder.imageView.setBackgroundColor(Color.GRAY)
                holder.imageView.setImageBitmap(null)
                holder.itemView.setOnClickListener {}
                return
            }

            ViewCompat.setTransitionName(holder.imageView, image.id)

            synchronized(previews) {
                if (previews.get(image.id!!) != null) {
                    holder.imageView.setImageBitmap(previews.get(image.id!!))
                }
                else {
                    GlobalScope.launch(Dispatchers.IO) {
                        val btm = BitmapFactory.decodeFile(image.urls?.localCoverPath)
                        if (btm != null) {
                            previews.put(image.id!!, btm)
                            GlobalScope.launch(Dispatchers.Main) {
                                holder.imageView.setImageBitmap(btm)
                            }
                        }
                    }
                }

                holder.itemView.setOnClickListener {
                    onAnimalItemClick(this@PreviewFragment.context!!, image, holder.imageView)
                }
            }
        }

        fun onAnimalItemClick(context: Context, image: Image, sharedImageView: ImageView) {
            val bundle = Bundle()
            bundle.putSerializable("image", image as Serializable)

            val changeTransform =
                TransitionInflater.from(context)
                    .inflateTransition(R.transition.change_image_transform)
            val explodeTransform =
                TransitionInflater.from(context).inflateTransition(android.R.transition.fade)

            // Setup exit transition on first fragment
            this@PreviewFragment.sharedElementReturnTransition = changeTransform
            this@PreviewFragment.exitTransition = explodeTransform

            val fragment = DetailFragment()
            fragment.arguments = bundle

            // Setup enter transition on second fragment
            val explodeTransformNewFragment =
                TransitionInflater.from(context).inflateTransition(android.R.transition.fade)

            val changeTransformNewFragment =
                TransitionInflater.from(context)
                    .inflateTransition(R.transition.change_image_transform)
            fragment.enterTransition = explodeTransformNewFragment

            fragment.sharedElementEnterTransition = changeTransformNewFragment

            (context as MainActivity).supportFragmentManager
                .beginTransaction()
                .addSharedElement(sharedImageView, ViewCompat.getTransitionName(sharedImageView)!!)
                .addToBackStack("")
                .replace(R.id.container, fragment)
                .commit()
        }

        inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView = itemView.image
        }
    }
}

