package com.kai.woof.screen.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kai.woof.model.Quiz
import com.kai.woof.quiz.QuizGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val quizGenerator: QuizGenerator
): ViewModel() {

    private val quiz = MutableStateFlow<Quiz?>(null)
    private val isLoading = MutableStateFlow(false)
    private val error = MutableSharedFlow<String>()

    /**
     * Expose states to ui to subscribe to
     */
    fun quiz(): StateFlow<Quiz?> = quiz
    fun isLoading(): StateFlow<Boolean> = isLoading
    fun error(): SharedFlow<String> = error

    fun generateQuiz() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            val newQuiz = kotlin.runCatching {
                quizGenerator.generateQuiz()

            }

            newQuiz.fold({ successResult ->
                quiz.value = successResult
                isLoading.value = false
            }, { exception ->
                error.emit(exception.message ?: "Puppies not found")
            })

        }
    }
}