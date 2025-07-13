package com.kai.woof

import android.app.Application
import com.kai.woof.quiz.QuizGenerator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WoofApplication: Application() {

    @Inject
    lateinit var quizGenerator: QuizGenerator

    override fun onCreate() {
        super.onCreate()
        quizGenerator.initAsync()
    }
}

