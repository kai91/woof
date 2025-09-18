package com.kai.woof.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kai.woof.model.Quiz
import com.kai.woof.screen.quiz.QuizScreen
import com.kai.woof.screen.quiz.QuizViewModel
import com.kai.woof.screen.start.StartScreen
import com.kai.woof.screen.start.StartViewModel
import com.kai.woof.ui.theme.WoofTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WoofTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "start_screen",
                    enterTransition = {
                        EnterTransition.Companion.None
                    },
                    exitTransition = {
                        ExitTransition.Companion.None
                    }
                ) {
                    composable("start_screen") {
                        StartScreen(
                            navController = navController,
                            vm = hiltViewModel<StartViewModel>()
                        )
                    }
                    composable("quiz_screen") { backStackEntry ->
                        val quiz =
                            navController.previousBackStackEntry?.savedStateHandle?.get<Quiz>("quiz")
                        quiz?.let {
                            QuizScreen(
                                navController = navController,
                                quiz = it,
                                vm = hiltViewModel<QuizViewModel>()
                            )
                        }
                    }
                }
            }
        }
    }
}