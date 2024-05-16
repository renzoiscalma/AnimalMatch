package com.animalmatch

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.animalmatch.ui.theme.AnimalMatchTheme

/**
 * Images obtained from https://unsplash.com/
 * For license details please see here.
 *
 * https://unsplash.com/license
 *
 * Sounds obtained are from https://pixabay.com/
 * Monkey Sound Effect by u_zpj3vbdres from Pixabay
 * Horse and Cow Sound Effect by UNIVERSFIELD from Pixabay
 * Dog, Cat Sound Effect from Pixabay
 *
 */
class MainActivity : ComponentActivity() {
    private var animals: Array<Animal> = generateAnimals()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimalMatchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GuessTheAnimal(
                        animals = animals,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun GuessTheAnimal(
    animals: Array<Animal>,
    modifier: Modifier = Modifier
) {
    var randInt by remember { mutableIntStateOf((0..4).random()) }
    var answer by remember { mutableStateOf("") }
    var answers by remember {
        mutableStateOf(generateAnswerList(
            animals,
            animals[randInt]
        ))
    }
    Column(
        modifier = modifier.fillMaxWidth(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Guess the Animal!",
            modifier = modifier
        )
        Image(
            painter = painterResource(animals[randInt].imageId),
            contentDescription = animals[randInt].name,
            Modifier.fillMaxHeight(0.5f)
        )
        GuessButtons(
            answers = answers,
            onAnswerChange = { newVal -> answer = newVal }
        )

        if (answer === animals[randInt].name) {
            val mediaPlayer = MediaPlayer.create(LocalContext.current, animals[randInt].soundId)
            mediaPlayer.start()
            Log.d("PLAYER DEBUG", "Hello")
            Text(
                text = "Correct!"
            )
            Button(onClick = {
                var tempInt = (0..4).random()
                while (tempInt == randInt) {
                    tempInt = (0..4).random()
                }
                randInt = tempInt
                answers = generateAnswerList(animals, animals[randInt])
                answer = ""
            }) {
                Text(stringResource(R.string.roll))
            }
        }

        if (answer !== "" && answer !== animals[randInt].name) {
            Text(
                text = "Wrong Answer!"
            )
        }
    }
}

@Composable
fun GuessButtons(
    answers: Array<Animal?>,
    onAnswerChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (btnAnswer in answers) {
            Button(onClick = {
                onAnswerChange(btnAnswer!!.name)
            }) {
                Text(btnAnswer!!.name)
            }
        }
    }
}

fun generateAnimals(): Array<Animal> {
    return arrayOf(
        Animal("Cat", R.drawable.cat, R.raw.cat_meow),
        Animal("Dog", R.drawable.dog, R.raw.dog_bark),
        Animal("Monkey", R.drawable.monkey, R.raw.monkey_sound),
        Animal("Horse", R.drawable.horse, R.raw.horse_neigh),
        Animal("Cow", R.drawable.cow, R.raw.cow_moo)
    )
}

fun generateAnswerList(
    animals: Array<Animal>,
    answer: Animal,
): Array<Animal?> {
    val answers: Array<Animal?> = Array(3) { null }
    val wrongAnswer1 = generateWrongAnswer(animals, arrayOf(answer))
    val wrongAnswer2 = generateWrongAnswer(animals, arrayOf(answer, wrongAnswer1))
    for (i: Animal in arrayOf(answer, wrongAnswer1, wrongAnswer2)) {
        var randPlacement = (0..2).random()
        while (answers[randPlacement] !== null) {
            randPlacement = (0..2).random()
        }
        answers[randPlacement] = i
    }
    return answers;
}

fun generateWrongAnswer(animals: Array<Animal>, invalidAnimals: Array<Animal>): Animal {
    var wrongAnswer: Animal? = null
    while (wrongAnswer == null || invalidAnimals.contains(wrongAnswer)) {
        wrongAnswer = animals[(0..4).random()]
    }
    return wrongAnswer
}

