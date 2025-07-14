package com.kai.woof.fake

import com.kai.woof.model.Breed
import com.kai.woof.model.DogPhoto
import com.kai.woof.repository.DogRepository

class FakeDogRepository: DogRepository {
    var breedList: List<Breed>? = null
    var dogPhoto: DogPhoto? = null
    var getCompleteBreedCalled = 0

    override suspend fun getCompleteDogBreeds(): List<Breed>? {
        getCompleteBreedCalled++
        return breedList
    }

    override suspend fun getRandomDogPhoto(): DogPhoto? {
        return dogPhoto
    }
}