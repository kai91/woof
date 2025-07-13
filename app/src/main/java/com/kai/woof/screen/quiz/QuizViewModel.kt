package com.kai.woof.screen.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kai.woof.model.BreedVariant
import com.kai.woof.model.Question
import com.kai.woof.model.Quiz
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Handle logic for scoring the quiz result
 */
class QuizViewModel: ViewModel() {

    private lateinit var quiz: Quiz
    private lateinit var scoreList: List<Result>
    private val currentQuestion = MutableStateFlow<Question?>(null)
    private val pageIndicator = MutableStateFlow<List<Result>>(emptyList())
    private val correctChoice = MutableStateFlow<BreedVariant?>(null)
    private var answered = false
    private var index = 0

    fun pageIndicator(): StateFlow<List<Result>> = pageIndicator
    fun currentQuestion(): StateFlow<Question?> = currentQuestion
    // Show ui to the user if the selected the correct answer
    fun correctChoice(): StateFlow<BreedVariant?> = correctChoice

    fun setQuiz(quiz: Quiz) {
        this.quiz = quiz
        if (quiz.questionList.isEmpty()) {
            return
        }
        viewModelScope.launch {
            currentQuestion.emit(quiz.questionList.first())
            // Init score list with default value of pending
            scoreList = List(quiz.questionList.size) { Result.Pending }
            scoreList = scoreList.toMutableList().apply {
                this[0] = Result.Current
            }
            pageIndicator.emit(scoreList)
        }
    }

    fun answer(breedVariant: BreedVariant) {
        // Check if already answered to prevent user clicking again when animating or transitioning
        if (answered) return

        answered = true
        val question = currentQuestion.value ?: return

        val isCorrect = question.dogPhoto.breedVariant == breedVariant
        scoreList = scoreList.toMutableList().apply {
            this[index] = if (isCorrect) Result.Correct else Result.Incorrect
        }
        viewModelScope.launch {
            pageIndicator.emit(scoreList)
        }

        moveToNextQuestion()
    }

    /**
     * Move to next question if there's a next one, else end the quiz with a resulting score
     */
    private fun moveToNextQuestion() {
        if (index == quiz.questionList.size - 1) {
            // todo show quiz result
        } else {
            answered = false
            index++
            val nextQuestion = quiz.questionList[index]
            scoreList = scoreList.toMutableList().apply {
                this[index] = Result.Current
            }
            viewModelScope.launch {
                pageIndicator.emit(scoreList)
                currentQuestion.emit(nextQuestion)
            }
        }

    }


}

enum class Result {
    Incorrect,
    Correct,
    Current,
    Pending
}