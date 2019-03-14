package com.lind.vladimir.gallery.Utils

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.ConnectivityManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lind.vladimir.gallery.API_KEY
import com.lind.vladimir.gallery.Database.LocalDatabaseAPI
import com.lind.vladimir.gallery.Entities.Image
import com.lind.vladimir.gallery.Entities.SplashUser
import com.lind.vladimir.gallery.IMAGE_PER_PAGE
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class Utils {

    companion object {

        fun getImagesList(context: Context, page: Int, counter: Int = 0): List<Image> {
            val url = "https://api.unsplash.com/photos/curated"
            val db = LocalDatabaseAPI(context)

            val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
            urlBuilder.addQueryParameter("page", page.toString())
            urlBuilder.addQueryParameter("client_id", API_KEY)
            urlBuilder.addQueryParameter("per_page", IMAGE_PER_PAGE.toString())

            try {
                val response =
                    getJsonFromServer(urlBuilder.build())
                val json = response?.body()?.string()

                if (response?.code() != 200) {
                    throw IllegalStateException(json)
                }
                if (json == null)
                    throw IllegalStateException("Strange API behaviour")

                val mapper = jacksonObjectMapper()

                val rez = mapper.readValue<List<Image>>(json)
                for (image in rez) {
                    image.page = page
                }

                return rez

            } catch (e: Exception) {
                if (e is IllegalStateException) throw  e

                if (isOnline(context) && counter < 1) {
                    getImagesList(context,
                        page,
                        counter + 1)
                }
                else {
                    throw NetworkErrorException("Need internet")
                }

            }

            return mutableListOf()
        }

        fun getUserImagesList(context: Context, page: Int, user: SplashUser,
                              counter: Int = 0): List<Image> {
            val url = "https://api.unsplash.com/users/${user.username}/photos"

            val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
            urlBuilder.addQueryParameter("page", page.toString())
            urlBuilder.addQueryParameter("client_id", API_KEY)
            urlBuilder.addQueryParameter("per_page", IMAGE_PER_PAGE.toString())

            val db = LocalDatabaseAPI(context)

            try {
                val response =
                    getJsonFromServer(urlBuilder.build())
                val json = response?.body()?.string()

                if (response?.code() != 200) {
                    throw IllegalStateException(json)
                }
                if (json == null)
                    throw IllegalStateException("Strange API behaviour")

                val mapper = jacksonObjectMapper()

                val rez = mapper.readValue<List<Image>>(json)
                for (image in rez) {
                    image.page = page
                }


                return rez

            } catch (e: Exception) {
                if (e is IllegalStateException) throw  e
                if (isOnline(context) && counter < 1) {
                    getImagesList(context,
                        page,
                        counter + 1)
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
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, fos)
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


        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            var isConnected = false
            if (connectivityManager != null) {
                val activeNetwork = connectivityManager.activeNetworkInfo
                isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            }

            return isConnected
        }

    }

}