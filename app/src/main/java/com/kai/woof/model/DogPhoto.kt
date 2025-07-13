package com.kai.woof.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class DogPhoto(
    val imageFile: File,
    val breedVariant: BreedVariant,
) : Parcelable