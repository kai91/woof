package com.kai.woof.fake

import com.kai.woof.model.Breed
import com.kai.woof.model.DogPhoto
import com.kai.woof.repository.DogRepository

class FakeDogRepository: DogRepository {
    var breedList: List<Breed>? = null
    var dogPhoto: List<DogPhoto>? = null
    var getCompleteBreedCalled = 0
    var currentIndex = 0

    override suspend fun getCompleteDogBreeds(): List<Breed>? {
        getCompleteBreedCalled++
        return breedList
    }

    override suspend fun getRandomDogPhoto(): DogPhoto? {
        val list = dogPhoto
        if (list.isNullOrEmpty()) return null

        if (currentIndex < list.size) {
            val result = list[currentIndex]
            currentIndex++
            if (currentIndex == list.size) currentIndex = 0
            return result
        }
        return null
    }
}