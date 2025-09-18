package com.kai.woof.screen.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kai.woof.model.BreedVariant
import com.kai.woof.model.Question
import com.kai.woof.model.Quiz
import com.kai.woof.model.QuizResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Handle logic for scoring the quiz result
 */
@HiltViewModel
class QuizViewModel @Inject constructor() : ViewModel() {

    private lateinit var quiz: Quiz
    private val _uiState = MutableStateFlow(QuizScreenState())

    val uiState: StateFlow<QuizScreenState> = _uiState
    private var answered = false
    private var index = 0
    private var startTime: Long = 0

    fun setQuiz(quiz: Quiz) {
        this.quiz = quiz
        this.startTime = System.currentTimeMillis()
        if (quiz.questionList.isEmpty()) {
            return
        }

        val currentQuestion = quiz.questionList.first()
        // Init score list with default value of pending
        var scoreList = List(quiz.questionList.size) { Result.Pending }
        scoreList = scoreList.toMutableList().apply {
            this[0] = Result.Current
        }
        _uiState.update {
            it.copy(
                currentQuestion = currentQuestion,
                scoreList = scoreList,
            )
        }
    }

    fun answer(breedVariant: BreedVariant) {
        // Check if already answered to prevent user clicking again when animating or transitioning
        if (answered) return

        answered = true
        val question = _uiState.value.currentQuestion ?: return

        val isCorrect = question.dogPhoto.breedVariant == breedVariant
        val scoreList = _uiState.value.scoreList.toMutableList().apply {
            this[index] = if (isCorrect) Result.Correct else Result.Incorrect
        }

        val correctChoice = if (isCorrect) breedVariant else question.dogPhoto.breedVariant
        val incorrectChoice = if (isCorrect) null else breedVariant
        _uiState.update {
            it.copy(
                scoreList = scoreList,
                correctChoice = correctChoice,
                incorrectChoice = incorrectChoice
            )
        }

        viewModelScope.launch {
            // Delay for user to see the result
            delay(2000)
            moveToNextQuestion()
        }

    }

    /**
     * Move to next question if there's a next one, else end the quiz with a resulting score
     */
    private fun moveToNextQuestion() {
        if (index == quiz.questionList.size - 1) {
            // Quiz completed - emit result
            val timeTaken = System.currentTimeMillis() - startTime
            val score = _uiState.value.scoreList.count { it == Result.Correct }
            val result = QuizResult(timeTaken, score, quiz.questionList.size)

            _uiState.update {
                it.copy(
                    quizResult = result
                )
            }
        } else {
            // clear previous selection
            answered = false

            index++
            val nextQuestion = quiz.questionList[index]
            val scoreList = _uiState.value.scoreList.toMutableList().apply {
                this[index] = Result.Current
            }
            _uiState.update {
                it.copy(
                    scoreList = scoreList,
                    incorrectChoice = null,
                    correctChoice = null,
                    currentQuestion = nextQuestion
                )
            }
        }

    }

}

data class QuizScreenState(
    val currentQuestion: Question? = null,
    val scoreList: List<Result> = emptyList(),
    val correctChoice: BreedVariant? = null,
    val incorrectChoice: BreedVariant? = null,
    val quizResult: QuizResult? = null,
)

enum class Result {
    Incorrect,
    Correct,
    Current,
    Pending
}