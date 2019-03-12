package com.lind.vladimir.gallery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.util.LruCache
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.preview_fragment.*
import kotlinx.android.synthetic.main.preview_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.transition.TransitionInflater
import android.os.Build
import android.R.attr.transitionName
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewCompat.setTransitionName
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.detail_fragment.*
import kotlinx.android.synthetic.main.detail_fragment.view.*
import java.lang.Exception


class DetailFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.detail_fragment, null)

        val bundle = this.arguments
        val image = bundle!!.getSerializable("image") as Image

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.imageView.transitionName = image.id
        }

        val btm = BitmapFactory.decodeFile(image.urls?.localCoverPath)
        view.imageView.setImageBitmap(btm)

        view.description.text = image.description
        view.username.text = image.user?.name

        Glide.with(context!!)
            .load(image.urls?.full)
            .into(view!!.fullImageView)

        return view
    }

    override fun onPause() {
        view!!.imageView.setImageBitmap(null)
        super.onPause()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition =
                    TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }


}
