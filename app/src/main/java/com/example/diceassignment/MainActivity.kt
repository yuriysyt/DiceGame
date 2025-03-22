package com.example.diceassignment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diceassignment.ui.theme.*

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
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightBlue.copy(alpha = 0.7f),
                        Color.White
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image with rounded corners and shadow
        Image(
            painter = painterResource(id = R.drawable.title_image_white),
            contentDescription = "Title",
            modifier = Modifier
                .width(if (isPortrait) screenWidth * 0.8f else screenWidth * 0.4f)
                .height(if (isPortrait) screenHeight * 0.3f else screenHeight * 0.5f)
                .clip(RoundedCornerShape(20.dp))  // Rounded corners
                .shadow(10.dp, RoundedCornerShape(20.dp)),  // Nice shadow
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stylish title
        Text(
            text = "Dice Game",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Pretty buttons with spacing
        GameButton(
            onClick = {
                // Start the game activity
                val gameIntent = Intent(context, GameActivity::class.java)
                context.startActivity(gameIntent)
            },
            color = DeepBlue,
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
        ) {
            Text(
                text = "New Game",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GameButton(
            onClick = { openAboutDialog = true },
            color = Amber,
            textColor = Color.Black,
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
        ) {
            Text(
                text = "About",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (openAboutDialog) {
            AlertDialog(
                icon = {
                    Icon(Icons.Default.Info, contentDescription = "About Icon")
                },
                title = {
                    Text(
                        text = "About",
                        fontWeight = FontWeight.Bold,
                        color = DeepBlue
                    )
                },
                text = {
                    Text(
                        text = "Student ID: w2064930\nName: Yurii Sytnichenko\n\nI confirm that I understand what plagiarism is and have read and understood the section on Assessment Offences in the Essential Information for Students. The work that I have submitted is entirely my own. Any work from other authors is duly referenced and acknowledged."
                    )
                },
                onDismissRequest = {
                    openAboutDialog = false
                },
                confirmButton = {
                    GameButton(
                        onClick = { openAboutDialog = false },
                        color = Green
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = null
            )
        }
    }
}

@Composable
fun GameButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = DeepBlue,
    disabledColor: Color = Color.Gray,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = color,
            contentColor = textColor,
            disabledContainerColor = disabledColor.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        ),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun MainScreenPreview() {
    DiceAssignmentTheme {
        MainScreen()
    }
}