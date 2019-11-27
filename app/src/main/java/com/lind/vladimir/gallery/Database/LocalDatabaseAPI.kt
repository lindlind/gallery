package com.lind.vladimir.gallery.Database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lind.vladimir.gallery.Database.Tables.Favourites
import com.lind.vladimir.gallery.IMAGE_PER_PAGE
import com.lind.vladimir.gallery.Entities.Image
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insertOrThrow


/**
 * Local database API
 *
 * @param context parent context
 */
public class LocalDatabaseAPI(private val context: Context) {

    /**
     * @param image image to remove in database
     */
    fun removeImageFromDatabase(image: Image) {
        GalleryDatabase.getInstance(context).use {
            delete(Favourites.NAME, "${Favourites.FIELDS.ID} = '${image.id}'")
        }
    }

    /**
     * @param image to check the existence to the database
     * @return true if exist, or false
     */
    fun isImageExistInDatabase(image: Image): Boolean {
        return GalleryDatabase.getInstance(context).use {
            val cursor: Cursor
            try {
                cursor = query(Favourites.NAME,
                    arrayOf(Favourites.FIELDS.ID),
                    "${Favourites.FIELDS.ID} = \"${image.id}\"",
                    null,
                    null,
                    null,
                    null)
            } catch (e: SQLiteException) {
                throw Exception("Table ${Favourites.NAME} doesn't exist")
            }
            return@use cursor.count > 0
        }
    }

    /**
     * Get all image essence from database by page
     * @param page current page to load. Used for chunk loading of [IMAGE_PER_PAGE] items
     * @return [Image] list essence
     */
    fun getFavouritesFromDatabase(page: Int): List<Image> {
        return GalleryDatabase.getInstance(context).use {
            val cursor: Cursor
            try {
                cursor = query(Favourites.NAME, null, null,
                    null, null, null, null, "${IMAGE_PER_PAGE * (page - 1)}, $IMAGE_PER_PAGE")
            } catch (e: SQLiteException) {
                throw Exception("Table ${Favourites.NAME} doesn't exist")
            }

            cursor.moveToFirst()

            val images = mutableListOf<Image>()

            if (cursor.count == 0)
                return@use images
            do {
                var image = Image()
                var pos = 0
                for (columnName in cursor.columnNames) {
                    when (columnName) {
                        Favourites.FIELDS.SERIALIZED_IMAGE -> image =
                                jacksonObjectMapper().readerFor(Image::class.java)
                                    .readValue<Image>(cursor.getString(pos))
                    }
                    pos++;
                }
                image.page = page

                images.add(image)
            } while (cursor.moveToNext())

            return@use images
        }
    }

    /**
     * Get all images count from database
     *
     * @return images count from database in Table [Images.NAME]
     */
    fun getImagesCount(): Int {
        return GalleryDatabase.getInstance(context).use {
            val cursor = query(Favourites.NAME,
                null,
                null,
                null,
                null,
                null,
                null)

            cursor.count
        }
    }


    /**
     * Insert images in database
     *
     * @param images images list to add to the database
     */
    fun insertImagesInDatabase(images: List<Image>) {
        val mapper = ObjectMapper()

        GalleryDatabase.getInstance(context).use {
            for (image in images) {
                insertOrThrow(Favourites.NAME,
                    Favourites.FIELDS.ID to image.id,
                    Favourites.FIELDS.SERIALIZED_IMAGE to mapper.writeValueAsString(image))
            }
        }
    }

    /**
     * Insert imageData in database
     *
     * @param image imageData to add to the database
     */
    fun insertImageInDatabase(image: Image): Long {
        val mapper = ObjectMapper()

        return GalleryDatabase.getInstance(context).use {
            return@use insertOrThrow(Favourites.NAME,
                Favourites.FIELDS.ID to image.id,
                Favourites.FIELDS.SERIALIZED_IMAGE to mapper.writeValueAsString(image))
        }
    }

    fun deleteDatabase() {
        context.deleteDatabase(DATABASE_NAME);
    }
}