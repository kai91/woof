package com.kai.woof.quiz

import com.kai.woof.fake.FakeDogRepository
import com.kai.woof.model.Breed
import com.kai.woof.model.BreedVariant
import com.kai.woof.model.DogPhoto
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class QuizGeneratorTest {

    private lateinit var quizGenerator: QuizGenerator
    private lateinit var dogRepository: FakeDogRepository

    val breeds: List<Breed> = ('a'..'o').map { letter ->
        Breed(letter.toString(), emptyList())
    }

    val dogPhotos: List<DogPhoto> = ('a'..'o').map { letter ->
        DogPhoto(letter.toString(), File(letter.toString()),
            BreedVariant(letter.toString(), null))
    }

    @Before
    fun setUp() {
        dogRepository = FakeDogRepository()
        dogRepository.breedList = breeds
        dogRepository.dogPhoto = dogPhotos
        quizGenerator = QuizGenerator(dogRepository)
    }

    @Test
    fun `even when init is called twice, only 1 api request is made`() {
        // Given
        dogRepository.breedList = emptyList()

        // When
        quizGenerator.initAsync()
        quizGenerator.initAsync()
        Thread.sleep(50)

        // Then
        Assert.assertEquals(1, dogRepository.getCompleteBreedCalled)
    }

    @Test
    fun `when a quiz is generated, it should have 5 questions`() = runTest {
        // When
        val quiz = quizGenerator.generateQuiz()

        // Then
        Assert.assertEquals(5, quiz.questionList.size)
    }

    @Test
    fun `when a quiz is generated, the correct answer should always be one of the options`() =
        runTest {

            // Repeat for 1000 times to ensure it is not random
            for (i in 1..1000) {
                // When
                val quiz = quizGenerator.generateQuiz()

                // Then
                quiz.questionList.map {
                    Assert.assertTrue(it.choiceList.contains(it.dogPhoto.breedVariant))
                }
            }

        }

    @Test
    fun `when a quiz is generated, all the options are unique`() = runTest {
        // Repeat for 1000 times to ensure it is not random
        for (i in 1..1000) {
            // When
            val quiz = quizGenerator.generateQuiz()

            // Then
            quiz.questionList.map {
                val choices = it.choiceList
                val choicesWithoutDuplicate = choices.distinct()
                Assert.assertEquals(choices.size, choicesWithoutDuplicate.size)
            }
        }

    }
}