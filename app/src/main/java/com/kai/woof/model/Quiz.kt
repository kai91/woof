package com.kai.woof.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A quiz session, with a list of all questions
 */
@Parcelize
data class Quiz(
    val questionList: List<Question>
) : Parcelable

/**
 * A Question has a photo, a list of choices to be displayed to the user, and a correct answer
 */
@Parcelize
data class Question(
    val dogPhoto: DogPhoto,
    val choiceList: List<BreedVariant>
) : Parcelable