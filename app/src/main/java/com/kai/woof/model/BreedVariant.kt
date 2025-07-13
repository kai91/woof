package com.kai.woof.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represent a specific breed & sub-breed
 */
@Parcelize
data class BreedVariant(
    val breedName: String,
    val subBreedName: String?
) : Parcelable