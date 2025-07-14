package com.kai.woof.screen.start

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kai.woof.R
import com.kai.woof.model.QuizResult
import com.kai.woof.screen.quiz.QuizActivity
import com.kai.woof.ui.theme.WoofTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartActivity : ComponentActivity() {

    private val vm: StartViewModel by viewModels()

    private val quizResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == QuizActivity.QUIZ_RESULT_CODE) {
            result.data?.let { data ->
                val quizResult =
                    IntentCompat.getParcelableExtra(data, "result", QuizResult::class.java)
                quizResult?.let { handleQuizResult(it) }
            }
        }
    }

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
                        if (isLoading.value) {
                            LoadingQuiz(isLoading)
                        } else {
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
                    }
                }
            }
        }

        observeStartQuiz()
    }

    private fun observeStartQuiz() {
        lifecycleScope.launch {
            vm.quiz().collect { quiz ->
                quiz?.let {
                    quizResultLauncher.launch(QuizActivity.newIntent(this@StartActivity, it))
                }
            }
        }
    }

    private fun handleQuizResult(quizResult: QuizResult) {
        vm.setQuizResult(quizResult)
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
        val lastResult = vm.lastQuizResult().collectAsState()

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Show last quiz result if available
                lastResult.value?.let { result ->
                    QuizResultDisplay(result)
                    Spacer(modifier = Modifier.padding(16.dp))
                }

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
    fun QuizResultDisplay(result: QuizResult) {
        val timeInSeconds = result.timeTakenMs / 1000
        val percentage = (result.score.toFloat() / result.maxScore.toFloat()) * 100

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Last Quiz Result",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Score: ${result.score}/${result.maxScore}",
                fontSize = 18.sp,
                color = if (percentage >= 50) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )

            Text(
                text = "Time: ${timeInSeconds}s",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            // Semi-transparent background overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Loading animation
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.loading_corgi)
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                    )

                    Spacer(modifier = Modifier.padding(24.dp))

                    // Loading text
                    Text(
                        text = "Gathering the puppies...",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.padding(16.dp))

                    // Subtitle
                    Text(
                        text = "Fetching dog breeds and photos",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.padding(32.dp))

                    // Progress indicator
                    CircularProgressIndicator(
                        modifier = Modifier.width(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 4.dp
                    )
                }
            }
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