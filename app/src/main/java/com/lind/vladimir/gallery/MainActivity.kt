package com.lind.vladimir.gallery

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.preview_item.view.*

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity);
    }


    override fun onResume() {
        super.onResume()

        initPreviewGrid()
    }


    private fun initPreviewGrid()
    {
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.isNestedScrollingEnabled = true
        val adapter = PreviewAdapter(mutableListOf("A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC","A","b", "CCCCCCCCCC"));
        recyclerView.adapter = adapter
    }


}

class PreviewAdapter(val list:List<String>) : RecyclerView.Adapter<PreviewAdapter.previewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): previewHolder {

        val view = LayoutInflater.from(p0.context).inflate(R.layout.preview_item,null)

        return previewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: previewHolder, position: Int) {
        holder.textView.text = list[position]
    }

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.textView
    }
}