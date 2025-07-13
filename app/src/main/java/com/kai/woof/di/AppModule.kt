package com.kai.woof.di

import android.content.Context
import com.google.gson.Gson
import com.kai.woof.api.DogApiService
import com.kai.woof.image.ImageDownloader
import com.kai.woof.quiz.QuizGenerator
import com.kai.woof.repository.DogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideQuizGenerator(dogRepository: DogRepository): QuizGenerator {
        return QuizGenerator(dogRepository)
    }

    @Provides
    @Singleton
    fun provideImageDownloader(@ApplicationContext context: Context, dogApiService: DogApiService): ImageDownloader {
        return ImageDownloader(context, dogApiService)
    }

    @Provides
    @Singleton
    fun provideDogApiService(retrofit: Retrofit): DogApiService {
        return retrofit.create(DogApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val gson = Gson()
        val okHttpClient = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit
    }
}