package com.kai.woof.api

import com.kai.woof.api.response.DogBreedResponse
import com.kai.woof.api.response.DogPhotoResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File

interface DogApiService {

    @GET("api/breeds/list/all")
    suspend fun getAllBreeds(): Response<DogBreedResponse>

    @GET("api/breeds/image/random")
    suspend fun randomPhoto(): Response<DogPhotoResponse>

    @Streaming
    @GET
    suspend fun downloadFile(@Url photoUrl:String): ResponseBody
}

fun ResponseBody.saveToFile(destinationFile: File) {
    byteStream().use { inputStream ->
        destinationFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}