package com.kai.woof.screen.quiz

//import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
//import androidx.compose.material3.MaterialShapes
//import androidx.compose.material3.toShape
//import androidx.graphics.shapes.RoundedPolygon
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.IntentCompat
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kai.woof.R
import com.kai.woof.model.BreedVariant
import com.kai.woof.model.Question
import com.kai.woof.model.Quiz
import com.kai.woof.ui.theme.WoofTheme

private const val quizKey = "quiz"

class QuizActivity : ComponentActivity() {

    companion object {
        fun newIntent(context: Context, quiz: Quiz): Intent {
            val intent = Intent(context, QuizActivity::class.java)
            intent.putExtra(quizKey, quiz)
            return intent
        }
    }

    private lateinit var quiz: Quiz
    private val vm: QuizViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IntentCompat.getParcelableExtra(intent, quizKey, Quiz::class.java)?.let { quiz = it }
        vm.setQuiz(quiz)
        enableEdgeToEdge()
        setContent {
            WoofTheme {
                val question = vm.currentQuestion().collectAsState().value ?: return@WoofTheme
                Scaffold(Modifier.fillMaxSize()) { innerPadding ->
                    QuizView(
                        question, Modifier
                            .padding(innerPadding))
                }
            }
        }
    }

    @Composable
    private fun QuizView(question: Question, modifier: Modifier) {
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

            ChoiceList(question.choiceList)
            Spacer(Modifier.weight(1f))
            PageIndicator()
        }
    }

    @Composable
    private fun ChoiceList(list: List<BreedVariant>) {
        val correctBreed = vm.correctChoice().collectAsState()

        for (i in list) {
            val breed = i.breedName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            val subBreed = i.subBreedName?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            val name = if (subBreed != null) "$subBreed $breed" else breed

            Box(modifier = Modifier.padding(vertical = 8.dp)) {
                Button(onClick = {vm.answer(i)}, modifier = Modifier.fillMaxWidth(),
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
    fun CorrectLottieView() {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.correct3))
        LottieAnimation(
            composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        )
    }

    @Composable
    private fun PageIndicator() {
        val pageIndicator = vm.pageIndicator().collectAsState()
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            pageIndicator.value.map { result ->
                val color = when(result) {
                    Result.Incorrect -> Color(0xFFD03D56) // Red
                    Result.Correct -> Color(0xFF41ab5d) // Green
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
}