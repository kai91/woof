package com.kai.woof.screen.start

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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kai.woof.R
import com.kai.woof.model.QuizResult

@Composable
fun StartScreen(
    navController: NavHostController,
    vm: StartViewModel,
) {
    val isLoading = vm.uiState.collectAsState().value.isLoading
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Handle quiz result from navigation
    LaunchedEffect(Unit) {
        val quizResult = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<QuizResult>("quiz_result")
        quizResult?.observe(lifecycleOwner) { result ->
            vm.setQuizResult(result)
        }
    }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }, modifier = Modifier.fillMaxSize()) { _ ->
        ErrorSnackBar(vm, snackbarHostState)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                LoadingQuiz(true)
            } else {
                Column {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LottieHome()
                    }

                    StartButton(vm, navController)
                }
            }
        }
    }
}

@Composable
fun ErrorSnackBar(vm: StartViewModel, snackbarHostState: SnackbarHostState) {
    // Collect error events as a one-time effect
    LaunchedEffect(Unit) {
        vm.uiState.collect { uiState ->
            uiState.error?.let { snackbarHostState.showSnackbar(it) }
        }
    }
}

@Composable
fun StartButton(vm: StartViewModel, navController: NavHostController) {
    val uiState = vm.uiState.collectAsState()

    // Observe quiz generation and navigate when quiz is ready
    LaunchedEffect(Unit) {
        vm.uiState.collect { state ->
            state.quiz?.let {
                // Navigate to quiz screen with quiz data
                navController.apply {
                    currentBackStackEntry?.savedStateHandle?.set("quiz", it)
                    navigate("quiz_screen")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Show last quiz result if available
            uiState.value.lastQuizResult?.let { state ->
                QuizResultDisplay(state)
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


@Composable
fun LoadingQuiz(isLoading: Boolean) {
    if (isLoading) {
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