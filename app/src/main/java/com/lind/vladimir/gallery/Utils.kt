package com.lind.vladimir.gallery

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception
import android.graphics.Bitmap
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.content.Context.MODE_PRIVATE
import android.R
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*


class Utils {

    companion object {

        fun getImagesList(page: Int): List<Image> {
            val url = "https://api.unsplash.com/photos"

            val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
            urlBuilder.addQueryParameter("page", page.toString())
            urlBuilder.addQueryParameter("client_id", API_KEY)
            urlBuilder.addQueryParameter("per_page", "10")

            try {
                val response = getJsonFromServer(urlBuilder.build())
                val json = response?.body()?.string()

                if (json == null || response!!.code() != 200) {
                    //TODO vse ploho
                    return mutableListOf()
                }

                val mapper = jacksonObjectMapper()

                return mapper.readValue(json)

            } catch (e: Exception) {
                Log.i("", "")
                //TODO  xnj-nj cltkfnm
            }

            return mutableListOf()
        }

        fun getJsonFromServer(url: HttpUrl): Response? {
            val builder = OkHttpClient.Builder()
            val client = builder.build()

            val request = Request.Builder()
                    .url(url)
                    .build()
            return client.newCall(request).execute()
        }

        fun saveToInternalStorage(context: Context, dirname:String, imageName:String, bitmapImage: Bitmap): String {
            val cw = ContextWrapper(context)
            val directory = cw.getDir(dirname, Context.MODE_PRIVATE)
            val mypath = File(directory, imageName)

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(mypath)
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return directory.absolutePath
        }

        fun loadImageFromStorage(path: String, imageName: String): Bitmap? {

            try {
                val f = File(path, imageName)
                val b = BitmapFactory.decodeStream(FileInputStream(f))
                return b
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

        fun uploadNewImages(context: Context, page: Int)
        {
            GlobalScope.launch(Dispatchers.IO) {
                val list = Utils.getImagesList(page).toMutableList()
                GlobalScope.launch(Dispatchers.Main)
                {
                    ImageLoaderService.startLoading(context, list)
                }
            }
        }
    }

}