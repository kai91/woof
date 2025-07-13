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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.IntentCompat
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IntentCompat.getParcelableExtra(intent, quizKey, Quiz::class.java)?.let { quiz = it }
        enableEdgeToEdge()
        setContent {
            WoofTheme {
                var index by remember { mutableIntStateOf(0) }

                var question = quiz.questionList[index]
                Scaffold(Modifier.fillMaxSize()) { innerPadding ->
                    QuizView(
                        question, Modifier
                            .padding(innerPadding)
                            .clickable {
                                index++
                            })
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

        }
    }

    @Composable
    private fun ChoiceList(list: List<BreedVariant>) {
        for (i in list) {
            Box(modifier = Modifier.padding(vertical = 8.dp)) {
                Button(onClick = {}) {
                    Text(
                        i.breedName,
                        fontSize = 32.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.fillMaxWidth()
                            .padding(4.dp),
                    )
                }
            }
        }
    }
}