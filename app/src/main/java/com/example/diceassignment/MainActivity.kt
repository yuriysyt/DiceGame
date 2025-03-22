package com.example.diceassignment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.diceassignment.ui.theme.DiceAssignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceAssignmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var openAboutDialog by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.title_image_white),
            contentDescription = "Title",
            modifier = Modifier
                .width(if (isPortrait) screenWidth else screenWidth * 0.4f)
                .height(if (isPortrait) screenHeight * 0.3f else screenHeight * 0.5f),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "Dice Game",
            modifier = modifier,
            textAlign = TextAlign.Center
        )

        Button(onClick = {
            // Start the game activity
            val gameIntent = Intent(context, GameActivity::class.java)
            context.startActivity(gameIntent)
        }) {
            Text(text = "New Game")
        }

        Button(onClick = { openAboutDialog = true }) {
            Text(text = "About")
        }

        if (openAboutDialog) {
            AlertDialog(
                icon = {
                    Icon(Icons.Default.Info, contentDescription = "About Icon")
                },
                title = {
                    Text(text = "About")
                },
                text = {
                    Text(text = "Student ID: w2064930\nName: Yurii Sytnichenko\n\nI confirm that I understand what plagiarism is and have read and understood the section on Assessment Offences in the Essential Information for Students. The work that I have submitted is entirely my own. Any work from other authors is duly referenced and acknowledged.")
                },
                onDismissRequest = {
                    openAboutDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openAboutDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = null
            )
        }
    }
}


@Composable
fun MainScreenPreview() {
    DiceAssignmentTheme {
        MainScreen()
    }
}