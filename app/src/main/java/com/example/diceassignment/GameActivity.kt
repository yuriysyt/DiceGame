package com.example.diceassignment

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diceassignment.ui.theme.DiceAssignmentTheme
import kotlin.random.Random
import com.example.diceassignment.ui.theme.DeepBlue
import com.example.diceassignment.ui.theme.Green
import com.example.diceassignment.ui.theme.Amber
import com.example.diceassignment.ui.theme.GameButton
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import com.example.diceassignment.ui.theme.DiceSelected
import com.example.diceassignment.ui.theme.DiceUnselected
import com.example.diceassignment.ui.theme.ComputerDice

class GameActivity : ComponentActivity() {
    // my variables for the game
    private var myGamePoints by mutableStateOf(0)
    private var compScore by mutableStateOf(0)
    private var playerWinCount by mutableStateOf(0)
    private var compWinCount by mutableStateOf(0)
    private var winningPoints by mutableStateOf(101) // default score as per spec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // restore stuff if orientation changes etc
        if (savedInstanceState != null) {
            myGamePoints = savedInstanceState.getInt("myGamePoints", 0)
            compScore = savedInstanceState.getInt("compScore", 0)
            playerWinCount = savedInstanceState.getInt("playerWinCount", 0)
            compWinCount = savedInstanceState.getInt("compWinCount", 0)
            winningPoints = savedInstanceState.getInt("winningPoints", 101)
        }

        setContent {
            DiceAssignmentTheme {
                GameScreen(
                    myGamePoints = myGamePoints,
                    compScore = compScore,
                    playerWinCount = playerWinCount,
                    compWinCount = compWinCount,
                    winningPoints = winningPoints,
                    onHumanScoreChanged = { myGamePoints = it },
                    onComputerScoreChanged = { compScore = it },
                    onHumanWinsChanged = { playerWinCount = it },
                    onComputerWinsChanged = { compWinCount = it },
                    onTargetScoreChanged = { winningPoints = it }
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("myGamePoints", myGamePoints)
        outState.putInt("compScore", compScore)
        outState.putInt("playerWinCount", playerWinCount)
        outState.putInt("compWinCount", compWinCount)
        outState.putInt("winningPoints", winningPoints)
    }
}

@Composable
fun GameScreen(
    myGamePoints: Int,
    compScore: Int,
    playerWinCount: Int,
    compWinCount: Int,
    winningPoints: Int,
    onHumanScoreChanged: (Int) -> Unit,
    onComputerScoreChanged: (Int) -> Unit,
    onHumanWinsChanged: (Int) -> Unit,
    onComputerWinsChanged: (Int) -> Unit,
    onTargetScoreChanged: (Int) -> Unit
) {
    // dice and game state variables
    var myDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var botDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var keptDice by remember { mutableStateOf(List(5) { false }) }
    var throwNumber by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var suddenDeath by remember { mutableStateOf(false) }
    var suddedisplayWinPopup by remember { mutableStateOf(false) }
    var suddchangeScorePopup by remember { mutableStateOf(false) }
    var gameWinner by remember { mutableStateOf("") }
    var currentPlayerRoll by remember { mutableStateOf(0) }
    var currentCompRoll by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // this is where we calculate the scores
    fun calculateScores(humanRollScore: Int, computerRollScore: Int) {
        if (suddenDeath) {
            // if we're in suddenDeath mode, just see who got higher roll
            if (humanRollScore > computerRollScore) {
                gameWinner = "Human"
                onHumanWinsChanged(playerWinCount + 1)
            } else if (computerRollScore > humanRollScore) {
                gameWinner = "Computer"
                onComputerWinsChanged(compWinCount + 1)
            } else {
                // still tied, so keep going
                throwNumber = 0
                keptDice = List(5) { false }
                myDice = List(5) { Random.nextInt(1, 7) }
                botDice = List(5) { Random.nextInt(1, 7) }
                return
            }
            suddedisplayWinPopup = true
            gameOver = true
            return
        }

        // Update scores normally if not in suddenDeath
        val newHumanScore = myGamePoints + humanRollScore
        val newComputerScore = compScore + computerRollScore

        onHumanScoreChanged(newHumanScore)
        onComputerScoreChanged(newComputerScore)

        // check if game is over
        if (newHumanScore >= winningPoints || newComputerScore >= winningPoints) {
            if (newHumanScore >= winningPoints && newComputerScore >= winningPoints) {
                // both players reached target
                if (newHumanScore > newComputerScore) {
                    gameWinner = "Human"
                    onHumanWinsChanged(playerWinCount + 1)
                } else if (newComputerScore > newHumanScore) {
                    gameWinner = "Computer"
                    onComputerWinsChanged(compWinCount + 1)
                } else {
                    // it's a tie. Go to suddenDeath
                    suddenDeath = true
                    throwNumber = 0
                    keptDice = List(5) { false }
                    return
                }
            } else if (newHumanScore >= winningPoints) {
                gameWinner = "Human"
                onHumanWinsChanged(playerWinCount + 1)
            } else {
                gameWinner = "Computer"
                onComputerWinsChanged(compWinCount + 1)
            }
            suddedisplayWinPopup = true
            gameOver = true
        }

        // Reset for next turn
        throwNumber = 0
        keptDice = List(5) { false }
        currentPlayerRoll = 0
        currentCompRoll = 0
    }

    // get the right dice image
    fun getDiceImageResource(value: Int): Int {
        return when (value) {
            1 -> R.drawable.human_dice_1
            2 -> R.drawable.human_dice_2
            3 -> R.drawable.human_dice_3
            4 -> R.drawable.human_dice_4
            5 -> R.drawable.human_dice_5
            6 -> R.drawable.human_dice_6
            else -> R.drawable.human_dice_1  // just in case but shouldn't happen
        }
    }

    fun getCompDiceImageResource(value: Int): Int {
        return when (value) {
            1 -> R.drawable.comp_dice_2
            2 -> R.drawable.comp_dice_3
            3 -> R.drawable.comp_dice_5
            4 -> R.drawable.comp_dice_4
            5 -> R.drawable.comp_dice_2
            6 -> R.drawable.comp_dice_6
            else -> R.drawable.comp_dice_2  // just in case but shouldn't happen
        }
    }

    // this is the main function for rolling dice
    fun throwDice() {
        // Roll human dice (only the unselected ones in rerolls)
        myDice = myDice.mapIndexed { index, value ->
            if (throwNumber == 0 || !keptDice[index]) {
                Random.nextInt(1, 7)
            } else {
                value
            }
        }

        // Calculate human turn score
        currentPlayerRoll = myDice.sum()

        // Simpler computer strategy - more like what a student would do
        fun simpleComputerStrategy(
            dice: List<Int>,
            compScore: Int,
            myGamePoints: Int
        ): Pair<Boolean, List<Boolean>> {
            // Check if this is the last roll - can't reroll if at max rolls
            if (throwNumber >= 3) {
                return Pair(false, List(5) { true })
            }

            // Calculate current dice sum
            val diceSum = dice.sum()

            // If we get more than 20, probably good enough to keep
            if (diceSum > 20) {
                return Pair(false, List(5) { true })
            }

            // If we're behind by more than 15 points, take more risks
            val isBehind = compScore < myGamePoints - 15

            // If we're close to target score, be more careful
            val closeToTarget = winningPoints - compScore < 20

            // Keep high value dice (5 and 6), reroll low dice
            val keepDice = dice.map { value ->
                when {
                    value >= 5 -> true  // always keep high dice
                    value <= 2 -> false  // always reroll low dice
                    else -> !isBehind   // keep middle dice only if not behind
                }
            }

            // If we're keeping all dice, just score
            if (keepDice.all { it }) {
                return Pair(false, keepDice)
            }

            // Make reroll decision based on basic factors
            val shouldReroll = diceSum < 18 || isBehind

            return Pair(shouldReroll, keepDice)
        }

        // Computer dice roll strategy
        if (throwNumber == 0) {
            // First roll - computer always rolls all dice
            botDice = List(5) { Random.nextInt(1, 7) }
            currentCompRoll = botDice.sum()
        } else {
            // Use strategy for rerolls
            val (shouldReroll, diceToKeep) = simpleComputerStrategy(
                botDice,
                compScore,
                myGamePoints
            )

            if (shouldReroll) {
                // Reroll dice according to strategy
                botDice = botDice.mapIndexed { index, value ->
                    if (diceToKeep[index]) {
                        value
                    } else {
                        Random.nextInt(1, 7)
                    }
                }
                currentCompRoll = botDice.sum()
            }
        }

        throwNumber++

        // Auto-score after third roll
        if (throwNumber >= 3) {
            calculateScores(currentPlayerRoll, currentCompRoll)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE6F2FF),
                        Color(0xFFF5F5F5)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        // Top scores section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Win counter
            Text(
                text = "H:$playerWinCount/C:$compWinCount",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Target score that player can click to change
            Text(
                text = "Target: $winningPoints",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    if (!gameOver) {
                        suddchangeScorePopup = true
                    }
                }
            )

            // Current scores
            Text(
                text = "H:$myGamePoints/C:$compScore",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        // Human dice section
        Text(
            text = "Your Dice (Current Roll: ${myDice.sum()})",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        // Human dice section
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.height(90.dp)
        ) {
            items(myDice.indices.toList()) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(
                            width = if (keptDice[index]) 3.dp else 2.dp,
                            color = if (keptDice[index]) DiceSelected else DiceUnselected,
                            shape = RoundedCornerShape(12.dp)  // Rounded corners
                        )
                        .shadow(
                            elevation = if (keptDice[index]) 8.dp else 4.dp,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = throwNumber > 0 && throwNumber < 3 && !gameOver) {
                            // Toggle selection for rerolls
                            val newSelected = keptDice.toMutableList()
                            newSelected[index] = !newSelected[index]
                            keptDice = newSelected
                        }
                ) {
                    Image(
                        painter = painterResource(id = getDiceImageResource(myDice[index])),
                        contentDescription = "Dice ${myDice[index]}",
                        modifier = Modifier
                            .size(60.dp)
                            .padding(4.dp)
                    )
                }
            }
        }


        Text(
            text = "Computer Dice (Current Roll: ${botDice.sum()})",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        // Computer dice section
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.height(120.dp)
        ) {
            items(botDice.indices.toList()) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(
                            width = 2.dp,
                            color = ComputerDice,
                            shape = RoundedCornerShape(12.dp)  // Rounded corners
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = getCompDiceImageResource(botDice[index])),
                        contentDescription = "Dice ${botDice[index]}",
                        modifier = Modifier
                            .size(60.dp)
                            .padding(4.dp)
                    )
                }
            }
        }

        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GameButton(
                onClick = { throwDice() },
                enabled = throwNumber < 3 && !gameOver,
                color = DeepBlue
            ) {
                Text(
                    text = "Throw",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            GameButton(
                onClick = { calculateScores(myDice.sum(), botDice.sum()) },
                enabled = throwNumber > 0 && throwNumber < 3 && !gameOver,
                color = Green
            ) {
                Text(
                    text = "Score",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            GameButton(
                onClick = {
                    val mainActivity = Intent(context, MainActivity::class.java)
                    context.startActivity(mainActivity)
                },
                color = Amber
            ) {
                Text(
                    text = "Back",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        // Roll count display
        Text(
            text = "Roll: $throwNumber/3",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )

        // Win dialog
        if (suddedisplayWinPopup) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        text = if (gameWinner == "Human") "You Win!" else "You Lose",
                        color = if (gameWinner == "Human") Color.Green else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text =  "Final Score:\n"+
                                "Human $myGamePoints - Computer $compScore\n\n" +
                                "Press OK to continue\n" +
                                "Press CANCEL to leave"
                    )
                },
                dismissButton = {
                    Button(
                        onClick = {
                            val gameIntent = Intent(context, MainActivity::class.java)
                            context.startActivity(gameIntent)
                        }) {
                        Text("Cancel")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            suddedisplayWinPopup = false
                            gameOver = false
                            onHumanScoreChanged(0)
                            onComputerScoreChanged(0)
                        }) {
                        Text("OK")
                    }
                }
            )
        }

        // Dialog for changing target score
        if (suddchangeScorePopup) {
            val newTargetScore = remember { mutableStateOf(winningPoints.toString()) }

            AlertDialog(
                onDismissRequest = { suddchangeScorePopup = false },
                title = { Text("Set Target Score") },
                text = {
                    Column {
                        Text("Enter target score:")
                        TextField(
                            value = newTargetScore.value,
                            onValueChange = { newTargetScore.value = it },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val parsedScore = newTargetScore.value.toIntOrNull()
                            if (parsedScore != null && parsedScore > 0) {
                                onTargetScoreChanged(parsedScore)
                            }
                            suddchangeScorePopup = false
                        }
                    ) {
                        Text("Set")
                    }
                },
                dismissButton = {
                    Button(onClick = { suddchangeScorePopup = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // SuddenDeath notice
        if (suddenDeath) {
            Text(
                text = "Tie-breaker mode! Roll once to determine the gameWinner.",
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        // Initialize dice on first launch
        LaunchedEffect(Unit) {
            if (myDice.isEmpty()) {
                myDice = List(5) { Random.nextInt(1, 7) }
                botDice = List(5) { Random.nextInt(1, 7) }
            }
        }
    }
}