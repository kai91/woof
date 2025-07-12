package com.kai.woof.model

/**
 * Represent a breed and all possible sub-breeds
 */
data class Breed(
    val name: String,
    val subBreed: List<String>,
)