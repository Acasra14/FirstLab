package com.example.firstlab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firstlab.ui.theme.FirstLabTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val SCORE_KEY = "score"
        const val LEVEL_KEY = "level"
        const val USERNAME_KEY = "username"
        const val REACHED_LEVEL_10 = "reached_level_10"
    }

    private var game: Game = Game()
    private var username: String = ""

    private val incrementScoreAndLevel: () -> Game = {
        val increment = if (game.level > 0) Random.nextInt(1, game.level + 1) else 1
        val score = game.score + increment
        val level = score / 10
        game = Game(name = username, score = score, level = level)

        if (level >= 10) {
            goToEndGameActivity(true)
        }

        game
    }

    private val decrementScoreAndLevel: () -> Game = {
        val decrement = game.level * 2
        val score = maxOf(0, game.score - decrement)
        val level = score / 10
        game = Game(name = username, score = score, level = level)
        game
    }

    private val goToEndGameActivity: (Boolean) -> Unit = { reachedLevel10 ->
        val intent = Intent(this, EndGameActivity::class.java)
        intent.putExtra(SCORE_KEY, game.score)
        intent.putExtra(LEVEL_KEY, game.level)
        intent.putExtra(USERNAME_KEY, username)
        intent.putExtra(REACHED_LEVEL_10, reachedLevel10)
        startActivity(intent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        username = intent.getStringExtra(LauncherActivity.USERNAME_KEY) ?: "Player"

        savedInstanceState?.let { instance ->
            game = Game(
                name = username,
                score = instance.getInt(SCORE_KEY, 0),
                level = instance.getInt(LEVEL_KEY, 0)
            )
        }

        enableEdgeToEdge()
        setContent {
            FirstLabTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(text = stringResource(R.string.super_game_counter))
                            }
                        )
                    }
                ) { innerPadding ->
                    GameStateDisplay(
                        game,
                        Modifier.padding(innerPadding),
                        incrementScoreAndLevel,
                        decrementScoreAndLevel,
                        goToEndGameActivity
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: La actividad está a punto de ser visible.")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: La actividad está en primer plano y se puede interactuar con ella.")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: La actividad está en segundo plano.")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: La actividad ya no es visible.")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: La actividad ha sido destruida.")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, game.score)
        outState.putInt(LEVEL_KEY, game.level)
        Log.d(TAG, "onSaveInstanceState: Guardando el estado de la actividad.")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState: Restaurando el estado de la actividad.")
    }
}

@Composable
fun GameStateDisplay(
    game: Game,
    modifier: Modifier = Modifier,
    onIncButtonClick: () -> Game,
    onDecButtonClick: () -> Game,
    onEndGameButtonClick: (Boolean) -> Unit
) {
    var gameState by remember { mutableStateOf(game) }
    val context = LocalContext.current

    val backgroundColor = when (gameState.level) {
        in 0..2 -> colorResource(R.color.level_0_2)
        in 3..6 -> colorResource(R.color.level_3_6)
        in 7..9 -> colorResource(R.color.level_7_9)
        else -> colorResource(R.color.level_7_9)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Greeting(gameState.name)
        }

        if (gameState.level == 5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.good_progress),
                    color = Color.Green,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(backgroundColor)
                    .padding(8.dp)
            ) {
                ShowVariables(stringResource(R.string.score), gameState.score)
                Spacer(Modifier.height(8.dp))
                ShowVariables(stringResource(R.string.level), gameState.level)
            }

            Spacer(Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StandardButton(stringResource(R.string.increase_score)) {
                    val result = onIncButtonClick()
                    gameState = game.copy(score = result.score, level = result.level)
                }

                Spacer(Modifier.height(8.dp))

                StandardButton(stringResource(R.string.decrease_score)) {
                    val result = onDecButtonClick()
                    gameState = game.copy(score = result.score, level = result.level)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            StandardButton(stringResource(R.string.end_game_button)) {
                onEndGameButtonClick(false)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.hello, name),
        modifier = modifier
    )
}

@Composable
fun ShowVariables(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$label -> $value",
        modifier = modifier
    )
}

@Composable
fun StandardButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(
            text = label,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FirstLabTheme {
        Greeting("Android")
    }
}