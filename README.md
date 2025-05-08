# Dice Game

A simple dice game application for Android where you compete against the computer to reach a target score.

![Game Screenshot](screenshots/game_screenshot.png)

## Game Description

This dice game is a turn-based game where:
- Both the player and computer roll 5 dice each turn
- Player can choose which dice to keep and which to reroll (up to 3 rolls per turn)
- After each turn, the total sum of dice is added to the player's score
- The first to reach the target score (default 101) wins
- In case of a tie, a sudden death mode begins
- Multiple games can be played in a session with a win counter

## Features

- Interactive dice that can be selected for keeping/rerolling
- Computer AI that makes strategic decisions
- Adjustable target score
- Win counter to track game statistics
- Smooth UI with visual feedback
- Tie-breaker (sudden death) mode
- Portrait and landscape orientation support

## Project Structure

The project follows standard Android architecture with Jetpack Compose:

```
app/src/main/java/com/example/diceassignment/
├── MainActivity.kt       # Main menu screen
├── GameActivity.kt       # Main game screen and logic
└── ui/theme/
    ├── Color.kt          # Color definitions
    ├── Theme.kt          # Theme settings and custom components
    └── Type.kt           # Typography settings
```

## How to Run the Project

1. Clone the repository or download the project files
2. Open the project in Android Studio (2022.3.1 or newer recommended)
3. Make sure you have the Android SDK installed (minimum API level: 24)
4. Connect an Android device or set up an emulator
5. Click the "Run" button (green triangle) in Android Studio
6. The app will build and install on your device/emulator

## How to Play

1. Launch the app and click "New Game" on the main screen
2. On your turn:
   - Click the "Throw" button to roll the dice
   - Select dice you want to keep (they will be highlighted with a green border)
   - Click "Throw" again to reroll the unselected dice (up to 3 rolls total)
   - Click "Score" to end your turn and add your points
3. The computer will automatically make its moves
4. The first player to reach the target score wins
5. You can change the target score by clicking on it at the top of the screen

## Screenshots

![Main Menu](screenshots/main_menu.png)

![Game in Progress](screenshots/game_progress.png)

## Author

Created by Yurii Sytnichenko (w2064930)
