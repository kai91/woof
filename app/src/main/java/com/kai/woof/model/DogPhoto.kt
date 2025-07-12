package com.kai.woof.model

import java.io.File

data class DogPhoto(
    val imageFile: File,
    val breedVariant: BreedVariant,
)