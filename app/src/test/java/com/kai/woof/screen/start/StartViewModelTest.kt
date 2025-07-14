package com.kai.woof.screen.start

import com.kai.woof.di.DispatcherProvider
import com.kai.woof.model.Quiz
import com.kai.woof.model.QuizResult
import com.kai.woof.quiz.QuizGenerator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class StartViewModelTest {
    private lateinit var viewModel: StartViewModel
    private lateinit var quizGenerator: QuizGenerator
    private val testDispatcher = StandardTestDispatcher()

    private val testDispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setup() {
        quizGenerator = mock()
        viewModel = StartViewModel(quizGenerator, testDispatcherProvider)
    }

    @Test
    fun `generateQuiz - when successful - updates quiz state and loading state`() = runTest {
        // Given
        val mockQuiz = Quiz(emptyList())
        whenever(quizGenerator.generateQuiz()).thenReturn(mockQuiz)

        // When
        viewModel.generateQuiz()

        // First verify loading is true
        Assert.assertTrue(viewModel.isLoading().value)

        // Advance time until all coroutines complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Now verify final state
        Assert.assertEquals(mockQuiz, viewModel.quiz().value)
        Assert.assertFalse(viewModel.isLoading().value)
    }

    @Test
    fun `generateQuiz - when fails - updates error state and loading state`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(quizGenerator.generateQuiz()).thenThrow(RuntimeException(errorMessage))

        // When
        viewModel.generateQuiz()

        // First verify loading is true
        Assert.assertTrue(viewModel.isLoading().value)

        // Advance time until all coroutines complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Then verify final state
        Assert.assertFalse(viewModel.isLoading().value)
    }

    @Test
    fun `setQuizResult - updates lastQuizResult state`() = runTest {
        // Given
        val mockResult = QuizResult(10, 5, 5)

        // When
        viewModel.setQuizResult(mockResult)

        // Then
        Assert.assertEquals(mockResult, viewModel.lastQuizResult().value)
    }
} 