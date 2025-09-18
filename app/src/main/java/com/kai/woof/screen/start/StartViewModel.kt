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

//    private val quiz = MutableStateFlow<Quiz?>(null)
//    private val isLoading = MutableStateFlow(false)
//    private val error = MutableSharedFlow<String>()
//    private val lastQuizResult = MutableStateFlow<QuizResult?>(null)
    private val _uiState = MutableStateFlow(StartScreenState())

    /**
     * Expose states to ui to subscribe to
     */
    val uiState: StateFlow<StartScreenState> = _uiState
//    fun quiz(): StateFlow<Quiz?> = quiz
//    fun isLoading(): StateFlow<Boolean> = isLoading
//    fun error(): SharedFlow<String> = error
//    fun lastQuizResult(): StateFlow<QuizResult?> = lastQuizResult

    fun generateQuiz() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch(dispatchers.io) {
            val newQuiz = kotlin.runCatching {
                quizGenerator.generateQuiz()
            }

            newQuiz.fold({ successResult ->
                _uiState.emit(_uiState.value.copy(
                    quiz = successResult,
                    isLoading = false
                ))
            }, { exception ->
                _uiState.emit(_uiState.value.copy(
                    error = exception.message ?: "Puppies not found",
                    isLoading = false
                ))
            })
        }
    }

    fun setQuizResult(result: QuizResult) {
        _uiState.value = _uiState.value.copy(lastQuizResult = result)
    }
}

data class StartScreenState(
    val quiz: Quiz? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastQuizResult: QuizResult? = null
)