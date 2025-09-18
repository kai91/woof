package com.kai.woof.screen.quiz

import com.kai.woof.di.DispatcherProvider
import com.kai.woof.model.BreedVariant
import com.kai.woof.model.DogPhoto
import com.kai.woof.model.Question
import com.kai.woof.model.Quiz
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class QuizViewModelTest {
    private lateinit var viewModel: QuizViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testDispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setup() {
        viewModel = QuizViewModel(testDispatcherProvider)
    }

    @Test
    fun `setQuiz - with empty question list - does not update state`() = runTest {
        // Given
        val emptyQuiz = Quiz(emptyList())

        // When
        viewModel.setQuiz(emptyQuiz)

        // Then
        Assert.assertNull(viewModel.uiState.value.currentQuestion)
        Assert.assertTrue(viewModel.uiState.value.scoreList.isEmpty())
    }

    @Test
    fun `setQuiz - with valid quiz - initializes first question and score list`() = runTest {
        // Given
        val breedVariant1 = BreedVariant("Golden Retriever", null)
        val breedVariant2 = BreedVariant("Labrador", null)
        val dogPhoto = DogPhoto("1", File("test.jpg"), breedVariant1)
        val question = Question(dogPhoto, listOf(breedVariant1, breedVariant2))
        val quiz = Quiz(listOf(question))

        // When
        viewModel.setQuiz(quiz)

        // Then
        Assert.assertEquals(question, viewModel.uiState.value.currentQuestion)
        Assert.assertEquals(1, viewModel.uiState.value.scoreList.size)
        Assert.assertEquals(Result.Current, viewModel.uiState.value.scoreList[0])
    }

    @Test
    fun `answer - with correct answer - updates score list and shows correct choice`() = runTest {
        // Given
        val breedVariant1 = BreedVariant("Golden Retriever", null)
        val breedVariant2 = BreedVariant("Labrador", null)
        val dogPhoto = DogPhoto("1", File("test.jpg"), breedVariant1)
        val question = Question(dogPhoto, listOf(breedVariant1, breedVariant2))
        val quiz = Quiz(listOf(question))
        viewModel.setQuiz(quiz)

        // When
        viewModel.answer(breedVariant1)

        // Then
        Assert.assertEquals(Result.Correct, viewModel.uiState.value.scoreList[0])
        Assert.assertEquals(breedVariant1, viewModel.uiState.value.correctChoice)
        Assert.assertNull(viewModel.uiState.value.incorrectChoice)
    }

    @Test
    fun `answer - with incorrect answer - updates score list and shows both choices`() = runTest {
        // Given
        val breedVariant1 = BreedVariant("Golden Retriever", null)
        val breedVariant2 = BreedVariant("Labrador", null)
        val dogPhoto = DogPhoto("1", File("test.jpg"), breedVariant1)
        val question = Question(dogPhoto, listOf(breedVariant1, breedVariant2))
        val quiz = Quiz(listOf(question))
        viewModel.setQuiz(quiz)

        // When
        viewModel.answer(breedVariant2)

        // Then
        Assert.assertEquals(Result.Incorrect, viewModel.uiState.value.scoreList[0])
        Assert.assertEquals(breedVariant1, viewModel.uiState.value.correctChoice)
        Assert.assertEquals(breedVariant2, viewModel.uiState.value.incorrectChoice)
    }

    @Test
    fun `answer - when already answered - ignores subsequent answers`() = runTest {
        // Given
        val breedVariant1 = BreedVariant("Golden Retriever", null)
        val breedVariant2 = BreedVariant("Labrador", null)
        val dogPhoto = DogPhoto("1", File("test.jpg"), breedVariant1)
        val question = Question(dogPhoto, listOf(breedVariant1, breedVariant2))
        val quiz = Quiz(listOf(question))
        viewModel.setQuiz(quiz)

        // When
        viewModel.answer(breedVariant1)
        val firstAnswerState = viewModel.uiState.value
        viewModel.answer(breedVariant2) // This should be ignored

        // Then
        Assert.assertEquals(firstAnswerState, viewModel.uiState.value)
    }

    @Test
    fun `answer - with single question quiz - completes quiz and generates result`() = runTest {
        // Given
        val breedVariant1 = BreedVariant("Golden Retriever", null)
        val breedVariant2 = BreedVariant("Labrador", null)
        val dogPhoto = DogPhoto("1", File("test.jpg"), breedVariant1)
        val question = Question(dogPhoto, listOf(breedVariant1, breedVariant2))
        val quiz = Quiz(listOf(question))
        viewModel.setQuiz(quiz)

        // When
        viewModel.answer(breedVariant1)

        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = viewModel.uiState.value.quizResult
        Assert.assertNotNull(result)
        Assert.assertEquals(1, result?.score)
        Assert.assertEquals(1, result?.maxScore)
    }

    @Test
    fun `answer - with multiple questions - moves to next question after delay`() = runTest {
        // Given
        val breedVariant1 = BreedVariant("Golden Retriever", null)
        val breedVariant2 = BreedVariant("Labrador", null)
        val breedVariant3 = BreedVariant("Poodle", null)
        val dogPhoto1 = DogPhoto("1", File("test1.jpg"), breedVariant1)
        val dogPhoto2 = DogPhoto("2", File("test2.jpg"), breedVariant2)
        val question1 = Question(dogPhoto1, listOf(breedVariant1, breedVariant2))
        val question2 = Question(dogPhoto2, listOf(breedVariant2, breedVariant3))
        val quiz = Quiz(listOf(question1, question2))
        viewModel.setQuiz(quiz)

        // When
        viewModel.answer(breedVariant1) // Correct answer

        // Advance time to complete the delay
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(question2, viewModel.uiState.value.currentQuestion)
        Assert.assertEquals(Result.Correct, viewModel.uiState.value.scoreList[0])
        Assert.assertEquals(Result.Current, viewModel.uiState.value.scoreList[1])
        Assert.assertNull(viewModel.uiState.value.correctChoice)
        Assert.assertNull(viewModel.uiState.value.incorrectChoice)
    }

    @Test
    fun `answer - completes entire quiz - generates final result`() = runTest {
        // Given
        val breedVariant1 = BreedVariant("Golden Retriever", null)
        val breedVariant2 = BreedVariant("Labrador", null)
        val dogPhoto1 = DogPhoto("1", File("test1.jpg"), breedVariant1)
        val dogPhoto2 = DogPhoto("2", File("test2.jpg"), breedVariant2)
        val question1 = Question(dogPhoto1, listOf(breedVariant1, breedVariant2))
        val question2 = Question(dogPhoto2, listOf(breedVariant1, breedVariant2))
        val quiz = Quiz(listOf(question1, question2))
        viewModel.setQuiz(quiz)

        // When - Answer first question correctly
        viewModel.answer(breedVariant1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Answer second question incorrectly
        viewModel.answer(breedVariant1) // Wrong answer for question2
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = viewModel.uiState.value.quizResult
        Assert.assertNotNull(result)
        Assert.assertEquals(1, result?.score) // Only first question was correct
        Assert.assertEquals(2, result?.maxScore)
    }
}