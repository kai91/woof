package com.kai.woof.screen.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kai.woof.di.DispatcherProvider
import com.kai.woof.model.Quiz
import com.kai.woof.model.QuizResult
import com.kai.woof.quiz.QuizGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val quizGenerator: QuizGenerator,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StartScreenState())

    /**
     * Expose states to ui to subscribe to
     */
    val uiState: StateFlow<StartScreenState> = _uiState

    fun generateQuiz() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(dispatchers.io) {
            val newQuiz = kotlin.runCatching {
                quizGenerator.generateQuiz()
            }

            newQuiz.fold({ successResult ->
                _uiState.update {
                    it.copy(
                        quiz = successResult,
                        isLoading = false
                    )
                }
            }, { exception ->
                _uiState.update {
                    it.copy(
                        error = exception.message ?: "Puppies not found",
                        isLoading = false
                    )
                }
            })
        }
    }

    fun setQuizResult(result: QuizResult) {
        _uiState.update {
            it.copy(lastQuizResult = result)
        }
    }
}

data class StartScreenState(
    val quiz: Quiz? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastQuizResult: QuizResult? = null
)