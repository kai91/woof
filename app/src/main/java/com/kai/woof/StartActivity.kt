package com.kai.woof

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.kai.woof.api.DogApiService
import com.kai.woof.image.ImageDownloader
import com.kai.woof.quiz.QuizGenerator
import com.kai.woof.repository.DogRepository
import com.kai.woof.repository.DogRepositoryImpl
import com.kai.woof.ui.theme.WoofTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StartActivity : ComponentActivity() {

    private lateinit var dogApiService: DogApiService
    private lateinit var imageDownloader: ImageDownloader
    private lateinit var dogRepository: DogRepository
    private lateinit var quizGenerator: QuizGenerator

    private fun init() {
        val gson = Gson()
        val okHttpClient = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        dogApiService = retrofit.create(DogApiService::class.java)
        imageDownloader = ImageDownloader(applicationContext, dogApiService)
        dogRepository = DogRepositoryImpl(dogApiService, imageDownloader)
        quizGenerator = QuizGenerator(dogRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WoofTheme {
                var isLoading by remember { mutableStateOf(false) }
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            isLoading = true
                            lifecycleScope.launch(Dispatchers.IO) {
                                val quiz = quizGenerator.generateQuiz()
                                isLoading = false
                                withContext(Dispatchers.Main) {
                                    startActivity(QuizActivity.newIntent(this@StartActivity, quiz))
                                }
                            }
                        }) {
                            Text("Start")
                        }
                    }
                    LoadingQuiz(isLoading)
                }
            }
        }

        val gson = Gson()
        val okHttpClient = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val dogApiService = retrofit.create(DogApiService::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            val response = dogApiService.getAllBreeds()
            print(response)
        }
    }

    private fun onStartClick() {

    }
}

@Composable
fun LoadingQuiz(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
//    WoofTheme {
//        Greeting("Android")
//    }
    WoofTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Greeting(
                name = "Android",
                modifier = Modifier.padding(innerPadding)
            )
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}