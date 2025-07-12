package com.kai.woof.api.response

import com.google.gson.annotations.SerializedName

data class DogBreedResponse(
    @SerializedName("message") val message: Map<String, List<String>>,
    @SerializedName("status") val status: String
) {
    fun isSuccessful(): Boolean = status == "success"
}