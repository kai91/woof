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

    @Before
    fun setUp() {
        dogRepository = FakeDogRepository()
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
        // Given
        dogRepository.breedList = mutableListOf<Breed>().apply {
            add(Breed("a", emptyList()))
            add(Breed("b", emptyList()))
            add(Breed("c", emptyList()))
        }
        dogRepository.dogPhoto = DogPhoto(File(""), BreedVariant("a", null))

        // When
        val quiz = quizGenerator.generateQuiz()

        // Then
        Assert.assertEquals(5, quiz.questionList.size)
    }

    @Test
    fun `when a quiz is generated, the correct answer should always be one of the options`() = runTest {
        // Given
        dogRepository.breedList = mutableListOf<Breed>().apply {
            add(Breed("a", emptyList()))
            add(Breed("b", emptyList()))
            add(Breed("c", emptyList()))
            add(Breed("d", emptyList()))
            add(Breed("e", emptyList()))
            add(Breed("f", emptyList()))
            add(Breed("g", emptyList()))
        }
        val returnedBreed = BreedVariant("a", null)
        dogRepository.dogPhoto = DogPhoto(File(""), returnedBreed)

        // Repeat for 1000 times to ensure it is not random
        for (i in 1..1000) {
            // When
            val quiz = quizGenerator.generateQuiz()

            // Then
            quiz.questionList.map {
                Assert.assertTrue(it.choiceList.contains(returnedBreed))
            }
        }

    }

    @Test
    fun `when a quiz is generated, all the options are unique`() = runTest {
        // Given
        dogRepository.breedList = mutableListOf<Breed>().apply {
            add(Breed("a", emptyList()))
            add(Breed("b", emptyList()))
            add(Breed("c", emptyList()))
            add(Breed("d", emptyList()))
            add(Breed("e", emptyList()))
            add(Breed("f", emptyList()))
            add(Breed("g", emptyList()))
        }
        val returnedBreed = BreedVariant("a", null)
        dogRepository.dogPhoto = DogPhoto(File(""), returnedBreed)

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