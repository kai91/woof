package com.kai.woof.screen.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kai.woof.model.BreedVariant
import com.kai.woof.model.Question
import com.kai.woof.model.Quiz
import com.kai.woof.model.QuizResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Handle logic for scoring the quiz result
 */
class QuizViewModel : ViewModel() {

    private lateinit var quiz: Quiz
    private lateinit var scoreList: List<Result>
    private val currentQuestion = MutableStateFlow<Question?>(null)
    private val pageIndicator = MutableStateFlow<List<Result>>(emptyList())
    private val correctChoice = MutableStateFlow<BreedVariant?>(null)
    private val incorrectChoice = MutableStateFlow<BreedVariant?>(null)
    private val quizResult = MutableStateFlow<QuizResult?>(null)
    private var answered = false
    private var index = 0
    private var startTime: Long = 0

    fun pageIndicator(): StateFlow<List<Result>> = pageIndicator
    fun currentQuestion(): StateFlow<Question?> = currentQuestion

    // Show ui to the user if the selected the correct answer
    fun correctChoice(): StateFlow<BreedVariant?> = correctChoice
    fun incorrectChoice(): StateFlow<BreedVariant?> = incorrectChoice
    fun quizResult(): StateFlow<QuizResult?> = quizResult

    fun setQuiz(quiz: Quiz) {
        this.quiz = quiz
        this.startTime = System.currentTimeMillis()
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
            if (isCorrect) {
                correctChoice.emit(breedVariant)
            } else {
                correctChoice.emit(question.dogPhoto.breedVariant)
                incorrectChoice.emit(breedVariant)
            }

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
            val score = scoreList.count { it == Result.Correct }
            val result = QuizResult(timeTaken, score)

            viewModelScope.launch {
                quizResult.emit(result)
            }
        } else {
            // clear previous selection
            answered = false

            index++
            val nextQuestion = quiz.questionList[index]
            scoreList = scoreList.toMutableList().apply {
                this[index] = Result.Current
            }
            viewModelScope.launch {
                pageIndicator.emit(scoreList)
                incorrectChoice.emit(null)
                correctChoice.emit(null)
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