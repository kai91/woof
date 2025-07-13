package com.kai.woof.screen.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kai.woof.di.DispatcherProvider
import com.kai.woof.model.Quiz
import com.kai.woof.model.QuizResult
import com.kai.woof.quiz.QuizGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val quizGenerator: QuizGenerator,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {

    private val quiz = MutableStateFlow<Quiz?>(null)
    private val isLoading = MutableStateFlow(false)
    private val error = MutableSharedFlow<String>()
    private val lastQuizResult = MutableStateFlow<QuizResult?>(null)

    /**
     * Expose states to ui to subscribe to
     */
    fun quiz(): StateFlow<Quiz?> = quiz
    fun isLoading(): StateFlow<Boolean> = isLoading
    fun error(): SharedFlow<String> = error
    fun lastQuizResult(): StateFlow<QuizResult?> = lastQuizResult

    fun generateQuiz() {
        isLoading.value = true
        viewModelScope.launch(dispatchers.io) {
            val newQuiz = kotlin.runCatching {
                quizGenerator.generateQuiz()
            }

            newQuiz.fold({ successResult ->
                quiz.value = successResult
                isLoading.value = false
            }, { exception ->
                isLoading.value = false
                error.emit(exception.message ?: "Puppies not found")
            })
        }
    }

    fun setQuizResult(result: QuizResult) {
        lastQuizResult.value = result
    }
}