package com.kai.woof.quiz

import com.kai.woof.model.Breed
import com.kai.woof.model.BreedVariant
import com.kai.woof.model.DogPhoto
import com.kai.woof.model.Question
import com.kai.woof.model.Quiz
import com.kai.woof.repository.DogRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

// Number of questions in a single Quiz
private const val QUIZ_SIZE = 5

/**
 *  Can be used to generate a Quiz for the user to play
 */
@OptIn(DelicateCoroutinesApi::class)
class QuizGenerator(
    private val dogRepository: DogRepository
) {
    private var completeBreedList: List<Breed>? = null
    private var completeVariantList: List<BreedVariant>? = null
    private val initMutex = Mutex()

    /**
     *  Fetch full breed and sub-breed info for reference
     */
    @Synchronized
    fun initAsync() {
        // It's ok to use GlobalScope since this is designed to be a Singleton
        GlobalScope.launch {
            runCatching { init() }
        }
    }


    private suspend fun init() {
        initMutex.withLock {
            if (completeBreedList != null) {
                // Already initialized
                return@withLock
            }
            val breeds = dogRepository.getCompleteDogBreeds() ?: return@withLock
            completeBreedList = breeds

            val breedVariantList = mutableListOf<BreedVariant>()
            breeds.map { breed ->
                if (breed.subBreed.isEmpty()) {
                    breedVariantList.add(BreedVariant(breed.name, null))
                    return@map
                }

                breed.subBreed.map { subBreed ->
                    breedVariantList.add(BreedVariant(breed.name, subBreed))
                }
            }

            completeVariantList = breedVariantList
        }
    }

    suspend fun generateQuiz(): Quiz {
        init()

        // Download in parallel
        val deferredPhotos = (1..QUIZ_SIZE).map {
            coroutineScope {
                async { dogRepository.getRandomDogPhoto() }
            }
        }

        val dogPhotos = deferredPhotos.awaitAll().filterNotNull()
        if (dogPhotos.size != QUIZ_SIZE) {
            throw IllegalStateException("Download photo failed")
        }

        val questions = dogPhotos.map { generateQuestion(it) }
        return Quiz(questions)
    }

    private fun generateQuestion(dogPhoto: DogPhoto, numberOfChoices: Int = 3): Question {
        val correctAnswer = dogPhoto.breedVariant
        // Exclude the correct answer from the list
        var variantList = (completeVariantList ?: emptyList()) - correctAnswer

        val options = mutableListOf<BreedVariant>()
        options.add(correctAnswer)

        for (i in 1..<numberOfChoices) {
            val option = variantList[Random.nextInt(variantList.size)]
            variantList = variantList - option
            options.add(option)
        }

        return Question(dogPhoto, options.shuffled())
    }
}