package com.kai.woof.repository

import com.kai.woof.api.DogApiService
import com.kai.woof.image.ImageDownloader
import com.kai.woof.model.Breed
import com.kai.woof.model.DogPhoto
import javax.inject.Inject

class DogRepositoryImpl @Inject constructor(
    private val dogApiService: DogApiService,
    private val imageDownloader: ImageDownloader,
): DogRepository {
    override suspend fun getCompleteDogBreeds(): List<Breed>? {
        val response = dogApiService.getAllBreeds()
        val body = response.body()
        if (body?.isSuccessful() != true) {
            // api request failed
            return null
        }

        return extractBreedList(body.message)
    }

    override suspend fun getRandomDogPhoto(): DogPhoto? {
        val responseBody = dogApiService.randomPhoto()?.body()
        if (responseBody?.isSuccessful() != true) {
            // api request failed
            return null
        }

        // Proceed to download the photo url into local disk
        return imageDownloader.downloadPhoto(responseBody.photoUrl)

    }

    private fun extractBreedList(breedMap: Map<String, List<String>>): List<Breed> {
        val newList = mutableListOf<Breed>()

        breedMap.forEach { entry ->
            val breed = entry.key
            val subBreed = entry.value
            newList.add(Breed(breed, subBreed))
        }

        return newList
    }
}

interface DogRepository {

    suspend fun getCompleteDogBreeds(): List<Breed>?

    suspend fun getRandomDogPhoto(): DogPhoto?
}