package com.lind.vladimir.gallery

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.*


class Utils {

    companion object {

        fun getImagesList(page: Int, counter: Int): List<Image> {
            val url = "https://api.unsplash.com/photos/curated"

            val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
            urlBuilder.addQueryParameter("page", page.toString())
            urlBuilder.addQueryParameter("client_id", API_KEY)
            urlBuilder.addQueryParameter("per_page", IMAGE_PER_PAGE.toString())

            try {
                val response = getJsonFromServer(urlBuilder.build())
                val json = response?.body()?.string()

                if (json == null || response.code() != 200) {
                    throw IllegalStateException("Strange API behaviour")
                }

                val mapper = jacksonObjectMapper()

                val rez =  mapper.readValue<List<Image>>(json)
                for(image in rez)
                    image.page = page

                return rez

            } catch (e: Exception) {
                if (isOnline() && counter < 5) {
                    getImagesList(page, counter + 1)
                }
                else {
                    throw NetworkErrorException("Need internet")
                }

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

        fun saveToInternalStorage(context: Context, dirname: String, imageName: String,
                                  bitmapImage: Bitmap): String {
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

        fun isOnline(): Boolean {
            val runtime = Runtime.getRuntime()
            try {
                val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
                val exitValue = ipProcess.waitFor()
                return exitValue == 0
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return false
        }

        fun uploadNewImages(context: Context, page: Int) {

            GlobalScope.launch(Dispatchers.IO) {
                var list: List<Image> = mutableListOf()
                try {
                    list  = Utils.getImagesList(page, 0)
                } catch (e: NetworkErrorException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(context,
                            context.getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                } catch (e: IllegalStateException) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(context,
                            context.getString(R.string.api_error),
                            Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                GlobalScope.launch(Dispatchers.Main)
                {
                    ImageLoaderService.startLoading(context, list)
                }
            }
        }
    }

}