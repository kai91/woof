package com.kai.woof.screen.start

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kai.woof.R
import com.kai.woof.screen.quiz.QuizActivity
import com.kai.woof.ui.theme.WoofTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartActivity : ComponentActivity() {

    private val vm: StartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WoofTheme {
                val isLoading = vm.isLoading().collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                }, modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ErrorSnackBar(snackbarHostState)

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()

                            ) {
                                LottieHome()
                            }

                            StartButton()
                        }

                    }
                    LoadingQuiz(isLoading)
                }
            }
        }

        observeStartQuiz()
    }

    private fun observeStartQuiz() {
        lifecycleScope.launch {
            vm.quiz().collect { quiz ->
                quiz?.let { startActivity(QuizActivity.newIntent(this@StartActivity, it)) }
            }
        }

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

    @Composable
    fun StartButton() {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(onClick = {
                vm.generateQuiz()
            }) {
                Text(
                    "Start",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingQuiz(isLoading: State<Boolean>) {
    if (isLoading.value) {
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
fun LottieHome() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.dog_purple))
    LottieAnimation(
        composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxWidth(.75f)
            .aspectRatio(1f)
    )
}