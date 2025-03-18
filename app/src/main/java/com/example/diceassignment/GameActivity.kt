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

class GameActivity : ComponentActivity() {
    private var humanScore by mutableStateOf(0)
    private var computerScore by mutableStateOf(0)
    private var humanWins by mutableStateOf(0)
    private var computerWins by mutableStateOf(0)
    private var targetScore by mutableStateOf(101)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            humanScore = savedInstanceState.getInt("humanScore", 0)
            computerScore = savedInstanceState.getInt("computerScore", 0)
            humanWins = savedInstanceState.getInt("humanWins", 0)
            computerWins = savedInstanceState.getInt("computerWins", 0)
            targetScore = savedInstanceState.getInt("targetScore", 101)
        }

        setContent {
            DiceAssignmentTheme {
                GameScreen(
                    humanScore = humanScore,
                    computerScore = computerScore,
                    humanWins = humanWins,
                    computerWins = computerWins,
                    targetScore = targetScore,
                    onHumanScoreChanged = { humanScore = it },
                    onComputerScoreChanged = { computerScore = it },
                    onHumanWinsChanged = { humanWins = it },
                    onComputerWinsChanged = { computerWins = it },
                    onTargetScoreChanged = { targetScore = it }
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("humanScore", humanScore)
        outState.putInt("computerScore", computerScore)
        outState.putInt("humanWins", humanWins)
        outState.putInt("computerWins", computerWins)
        outState.putInt("targetScore", targetScore)
    }
}

@Composable
fun GameScreen(
    humanScore: Int,
    computerScore: Int,
    humanWins: Int,
    computerWins: Int,
    targetScore: Int,
    onHumanScoreChanged: (Int) -> Unit,
    onComputerScoreChanged: (Int) -> Unit,
    onHumanWinsChanged: (Int) -> Unit,
    onComputerWinsChanged: (Int) -> Unit,
    onTargetScoreChanged: (Int) -> Unit
) {
    var humanDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var computerDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var humanDiceSelected by remember { mutableStateOf(List(5) { false }) }
    var rollCount by remember { mutableStateOf(0) }
    var gameFinished by remember { mutableStateOf(false) }
    var tieBreaker by remember { mutableStateOf(false) }
    var showWinDialog by remember { mutableStateOf(false) }
    var showTargetDialog by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf("") }
    var humanTurnScore by remember { mutableStateOf(0) }
    var computerTurnScore by remember { mutableStateOf(0) }
    val context = LocalContext.current

    fun scoreRoll(humanRollScore: Int, computerRollScore: Int) {
        if (tieBreaker) {
            // In tie-breaker mode, the higher score wins
            if (humanRollScore > computerRollScore) {
                winner = "Human"
                onHumanWinsChanged(humanWins + 1)
            } else if (computerRollScore > humanRollScore) {
                winner = "Computer"
                onComputerWinsChanged(computerWins + 1)
            } else {
                // Still tied, continue tie-breaker
                rollCount = 0
                humanDiceSelected = List(5) { false }
                humanDice = List(5) { Random.nextInt(1, 7) }
                computerDice = List(5) { Random.nextInt(1, 7) }
                return
            }
            showWinDialog = true
            gameFinished = true
            return
        }

        // Update scores
        val newHumanScore = humanScore + humanRollScore
        val newComputerScore = computerScore + computerRollScore

        onHumanScoreChanged(newHumanScore)
        onComputerScoreChanged(newComputerScore)

        // Check if a player has won
        if (newHumanScore >= targetScore || newComputerScore >= targetScore) {
            if (newHumanScore >= targetScore && newComputerScore >= targetScore) {
                // Both reached target - check who has higher score
                if (newHumanScore > newComputerScore) {
                    winner = "Human"
                    onHumanWinsChanged(humanWins + 1)
                } else if (newComputerScore > newHumanScore) {
                    winner = "Computer"
                    onComputerWinsChanged(computerWins + 1)
                } else {
                    // Tie - need tiebreaker
                    tieBreaker = true
                    rollCount = 0
                    humanDiceSelected = List(5) { false }
                    return
                }
            } else if (newHumanScore >= targetScore) {
                winner = "Human"
                onHumanWinsChanged(humanWins + 1)
            } else {
                winner = "Computer"
                onComputerWinsChanged(computerWins + 1)
            }
            showWinDialog = true
            gameFinished = true
        }

        // Reset for next turn
        rollCount = 0
        humanDiceSelected = List(5) { false }
        humanTurnScore = 0
        computerTurnScore = 0
    }

    // Function to get dice image resource
    fun getDiceImageResource(value: Int): Int {
        return when (value) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            else -> R.drawable.dice_1
        }
    }
    // Function to roll dice
    fun rollDice() {
        // Roll human dice (only the unselected ones in rerolls)
        humanDice = humanDice.mapIndexed { index, value ->
            if (rollCount == 0 || !humanDiceSelected[index]) {
                Random.nextInt(1, 7)
            } else {
                value
            }
        }

        // Calculate human turn score
        humanTurnScore = humanDice.sum()

        fun advancedComputerStrategy(
            currentDice: List<Int>,
            computerScore: Int,
            humanScore: Int,
            targetScore: Int,
            rollCount: Int
        ): Pair<Boolean, List<Boolean>> {
            // If this is the last roll, we can't reroll
            if (rollCount >= 3) {
                return Pair(false, List(5) { true })
            }

            val currentSum = currentDice.sum()
            val pointsToTarget = targetScore - computerScore
            val scoreGap = computerScore - humanScore

            // If we can win with current roll, take it
            if (computerScore + currentSum >= targetScore) {
                return Pair(false, List(5) { true })
            }

            // If current roll is very good (above 22), keep it unless we're far behind
            if (currentSum > 22 && (scoreGap > -15 || rollCount == 2)) {
                return Pair(false, List(5) { true })
            }

            // If we're in the final stretch and close to target
            if (pointsToTarget < 30 && currentSum > pointsToTarget + 5) {
                return Pair(false, List(5) { true })
            }

            // Decide which dice to keep based on value
            val diceToKeep = currentDice.map { value ->
                when {
                    value >= 5 -> true // Always keep high value dice
                    value >= 4 -> scoreGap > -10 // Keep 4s unless far behind
                    value >= 3 -> scoreGap > 0 // Keep 3s only if ahead
                    else -> false // Reroll low values
                }
            }

            // If we're keeping all dice, just score
            if (diceToKeep.all { it }) {
                return Pair(false, diceToKeep)
            }

            // Take more risks in late game if behind
            if (computerScore > 70 && humanScore > computerScore + 10) {
                return Pair(true, currentDice.map { it >= 4 })
            }

            // Be conservative in late game if ahead
            if (computerScore > 70 && computerScore > humanScore + 10 && currentSum > 15) {
                return Pair(false, List(5) { true })
            }

            return Pair(true, diceToKeep)
        }

        // Roll computer dice and implement computer strategy
        val computerStrategy = if (rollCount == 0) {
            // First roll - always roll all dice
            computerDice = List(5) { Random.nextInt(1, 7) }
            computerTurnScore = computerDice.sum()
            true // Always roll at least once
        } else {
            // Use advanced strategy instead of random
            val (shouldReroll, diceToKeep) = advancedComputerStrategy(
                computerDice,
                computerScore,
                humanScore,
                targetScore,
                rollCount
            )

            if (shouldReroll) {
                // Reroll dice according to strategy
                computerDice = computerDice.mapIndexed { index, value ->
                    if (diceToKeep[index]) {
                        value
                    } else {
                        Random.nextInt(1, 7)
                    }
                }
                computerTurnScore = computerDice.sum()
            }
            shouldReroll
        }

        rollCount++

        // Auto-score after third roll
        if (rollCount >= 3) {
            scoreRoll(humanTurnScore, computerTurnScore)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(1.dp)
    ) {


        // Scores and game info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Wins counter
            Text(
                text = "H:$humanWins/C:$computerWins",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Target score
            Text(
                text = "Target: $targetScore",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    if (!gameFinished) {
                        showTargetDialog = true
                    }
                }
            )

            // Scores
            Text(
                text = "H:$humanScore/C:$computerScore",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Human dice
        Text(
            text = "Your Dice (Current Roll: ${humanDice.sum()})",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.height(120.dp)
        ) {
            items(humanDice.indices.toList()) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(
                            width = if (humanDiceSelected[index]) 3.dp else 1.dp,
                            color = if (humanDiceSelected[index]) Color.Green else Color.Gray
                        )
                        .clickable(enabled = rollCount > 0 && rollCount < 3 && !gameFinished) {
                            // Toggle selection
                            val newSelected = humanDiceSelected.toMutableList()
                            newSelected[index] = !newSelected[index]
                            humanDiceSelected = newSelected
                        }
                ) {
                    Image(
                        painter = painterResource(id = getDiceImageResource(humanDice[index])),
                        contentDescription = "Dice ${humanDice[index]}",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        // Computer dice
        Text(
            text = "Computer Dice (Current Roll: ${computerDice.sum()})",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.height(120.dp)
        ) {
            items(computerDice.indices.toList()) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(width = 1.dp, color = Color.Gray)
                ) {
                    Image(
                        painter = painterResource(id = getDiceImageResource(computerDice[index])),
                        contentDescription = "Dice ${computerDice[index]}",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { rollDice() },
                enabled = rollCount < 3 && !gameFinished
            ) {
                Text(text = "Throw")
            }

            Button(
                onClick = { scoreRoll(humanDice.sum(), computerDice.sum()) },
                enabled = rollCount > 0 && rollCount < 3 && !gameFinished
            ) {
                Text(text = "Score")
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        // Roll count
        Text(
            text = "Roll: $rollCount/3",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )

        // Win dialog
        if (showWinDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        text = if (winner == "Human") "You Win!" else "You Lose",
                        color = if (winner == "Human") Color.Green else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text =  "Final Score:\n"+
                                "Human $humanScore - Computer $computerScore\n\n" +
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
                            showWinDialog = false
                            gameFinished = false
                            onHumanScoreChanged(0)
                            onComputerScoreChanged(0)
                        }) {
                        Text("OK")
                    }
                }
            )
        }

        // Target score dialog
        if (showTargetDialog) {
            val newTargetScore = remember { mutableStateOf(targetScore.toString()) }

            AlertDialog(
                onDismissRequest = { showTargetDialog = false },
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
                            showTargetDialog = false
                        }
                    ) {
                        Text("Set")
                    }
                },
                dismissButton = {
                    Button(onClick = { showTargetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Game state text (for debugging)
        if (tieBreaker) {
            Text(
                text = "Tie-breaker mode! Roll once to determine the winner.",
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        // Initial roll when the screen is first composed
        LaunchedEffect(Unit) {
            if (humanDice.isEmpty()) {
                humanDice = List(5) { Random.nextInt(1, 7) }
                computerDice = List(5) { Random.nextInt(1, 7) }
            }
        }
    }
}