package com.kai.woof.screen.start

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kai.woof.ui.theme.WoofTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@AndroidEntryPoint
class StartActivity : ComponentActivity() {

    val vm: StartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WoofTheme {
                var isLoading by remember { mutableStateOf(false) }
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ErrorSnackBar(snackbarHostState)

                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            onStartClick()
//                            isLoading = true
//                            lifecycleScope.launch(Dispatchers.IO) {
//                                val quiz = quizGenerator.generateQuiz()
//                                isLoading = false
//                                withContext(Dispatchers.Main) {
//                                    startActivity(QuizActivity.newIntent(this@StartActivity, quiz))
//                                }
//                            }
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
        vm.generateQuiz()
    }

    @Composable
    fun ErrorSnackBar(snackbarHostState: SnackbarHostState) {
        // Collect error events as a one-time effect
        LaunchedEffect(Unit) {
            vm.error().collect { errorMessage ->
                snackbarHostState.showSnackbar(errorMessage)
            }
        }
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