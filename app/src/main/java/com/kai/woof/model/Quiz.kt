package com.kai.woof.model

/**
 * A quiz session, with a list of all questions
 */
data class Quiz(
    val questionList: List<Question>
)

/**
 * A Question has a photo, a list of choices to be displayed to the user, and a correct answer
 */
data class Question(
    val dogPhoto: DogPhoto,
    val choiceList: List<BreedVariant>
)