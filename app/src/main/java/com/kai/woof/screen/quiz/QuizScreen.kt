package com.kai.woof.screen.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.kai.woof.model.BreedVariant
import com.kai.woof.model.Question
import com.kai.woof.model.Quiz

private val red = Color(0xFFD03D56) // Red
private val green = Color(0xFF41ab5d) // Green
private val buttonDefaultColor = Color(0xFF6650a4) // Default Material3 primary color

@Composable
fun QuizScreen(
    navController: NavHostController,
    quiz: Quiz,
    vm: QuizViewModel,
) {
    LaunchedEffect(quiz) {
        vm.setQuiz(quiz)
    }

    val state = vm.uiState.collectAsState()
    val question = state.value.currentQuestion ?: return
    val quizResult = state.value.quizResult

    // Handle quiz completion
    LaunchedEffect(quizResult) {
        quizResult?.let { result ->
            navController.previousBackStackEntry?.savedStateHandle?.set("quiz_result", quizResult)
            navController.navigateUp()
        }
    }

    Scaffold(Modifier.fillMaxSize()) { innerPadding ->
        QuizView(
            question, 
            vm,
            Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun QuizView(
    question: Question, 
    vm: QuizViewModel,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(question.dogPhoto.imageFile.path)
                .build(),
            contentDescription = "icon",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )

        ChoiceList(question.choiceList, vm)
        Spacer(Modifier.weight(1f))
        PageIndicator(vm)
    }
}

@Composable
private fun ChoiceList(list: List<BreedVariant>, vm: QuizViewModel) {
    val state = vm.uiState.collectAsState()
    val correctBreed = state.value.correctChoice
    val incorrectBreed = state.value.incorrectChoice

    for (i in list) {
        val breed =
            i.breedName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val subBreed =
            i.subBreedName?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val name = if (subBreed != null) "$subBreed $breed" else breed

        Box(modifier = Modifier.padding(vertical = 8.dp)) {
            val correct = correctBreed == i
            val incorrect = incorrectBreed == i
            val backgroundColor by animateColorAsState(
                targetValue = when {
                    correct -> green
                    incorrect -> red
                    else -> buttonDefaultColor
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
            )

            Button(
                onClick = { vm.answer(i) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor
                )
            ) {
                Text(
                    name,
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .padding(4.dp, 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PageIndicator(vm: QuizViewModel) {
    val pageIndicator = vm.uiState.collectAsState().value.scoreList
    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        pageIndicator.map { result ->
            val color = when (result) {
                Result.Incorrect -> red
                Result.Correct -> green
                Result.Current -> Color.DarkGray
                Result.Pending -> Color.Gray
            }
            Box(
                modifier = Modifier
                    .padding(2.5.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(10.dp)
            )
        }
    }
}


