package com.kai.woof.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizResult(
    val timeTakenMs: Long,
    val score: Int,
    val maxScore: Int,
) : Parcelable