package com.kai.woof.screen.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kai.woof.quiz.QuizGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class StartViewModel @Inject constructor(
    private val quizGenerator: QuizGenerator
): ViewModel() {

    fun generateQuiz() {
        viewModelScope.launch(Dispatchers.IO) {
            quizGenerator.generateQuiz()
        }
    }
}