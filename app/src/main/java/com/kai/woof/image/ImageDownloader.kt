package com.kai.woof.image

import android.content.Context
import com.kai.woof.api.DogApiService
import com.kai.woof.api.saveToFile
import com.kai.woof.model.BreedVariant
import com.kai.woof.model.DogPhoto
import java.io.File
import java.io.IOException


private const val TMP_IMAGE_FOLDER_PATH = "tmp_photo"

class ImageDownloader(
    private val context: Context,
    private val dogApiService: DogApiService
) {

    private val tempPhotoDirectoryFile: File by lazy {
        val dir = File(context.codeCacheDir, TMP_IMAGE_FOLDER_PATH)
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                // Handle directory creation failure (e.g., throw an exception)
                throw IOException("Failed to create directory: tmp_photo")
            }
        }
        return@lazy dir
    }

    suspend fun downloadPhoto(url: String): DogPhoto {
        val breedVariant = extractBreedFromUrl(url)
        val destinationFile = File(tempPhotoDirectoryFile, extractFileNameFromUrl(url))
        dogApiService.downloadFile(url).saveToFile(destinationFile)
        return DogPhoto(destinationFile, breedVariant)
    }

    /**
     * Clears all temporary photos from the cache directory
     * @return true if all files were successfully deleted, false otherwise
     */
    fun clearTempPhotos(): Boolean {
        return try {
            if (tempPhotoDirectoryFile.exists()) {
                val files = tempPhotoDirectoryFile.listFiles()
                files?.all { it.delete() } ?: true // Directory is empty or doesn't contain files
            } else {
                true // Directory doesn't exist, so nothing to clear
            }
        } catch (e: Exception) {
            false // Return false if any error occurs during deletion
        }
    }

    /**
     * Parse for breed info from the url.
     * The last portion of the path is the file name.
     * The second last portion of the path is the breed variant info:
     * - {breed}-{sub_breed}
     * - {breed}
     *
     * Example url:
     * - https://images.dog.ceo/breeds/rajapalayam-indian/Rajapalayam-dog.jpg
     * - https://images.dog.ceo/breeds/airedale/n02096051_4514.jpg
     */
    private fun extractBreedFromUrl(url: String): BreedVariant {
        val split = url.split("/")
        val secondLastIndex = split.size - 2
        val breedString = split[secondLastIndex]

        val breedSplit = breedString.split("-")
        val subBreed = if (breedSplit.size < 2) null else breedSplit[1]
        return BreedVariant(breedSplit[0], subBreed)
    }

    private fun extractFileNameFromUrl(url: String): String {
        val split = url.split("/")
        val breed = split[split.size - 2]
        return "${breed}_${split[split.size - 1]}"
    }
}