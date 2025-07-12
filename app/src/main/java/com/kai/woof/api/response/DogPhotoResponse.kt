package com.kai.woof.api.response

import com.google.gson.annotations.SerializedName

data class DogPhotoResponse(
    @SerializedName("message") val photoUrl: String,
    @SerializedName("status") val status: String
) {
    fun isSuccessful(): Boolean = status == "success"
}