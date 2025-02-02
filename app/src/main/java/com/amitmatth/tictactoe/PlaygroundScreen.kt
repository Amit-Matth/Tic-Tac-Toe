package com.amitmatth.tictactoe

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(paddingValues: PaddingValues, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isSoundOn by rememberSaveable { mutableStateOf(true) }

    val generalMoveSound: MediaPlayer = MediaPlayer.create(context, R.raw.move_sound)
    val drawSound: MediaPlayer = MediaPlayer.create(context, R.raw.draw_sound)
    val winsMoveSound: MediaPlayer = MediaPlayer.create(context, R.raw.win_sound)

    val preferencesManager = PlayerPreferencesManager(context)
    val savedPlayerXName by preferencesManager.playerXName.collectAsState(initial = "")
    val savedPlayerOName by preferencesManager.playerOName.collectAsState(initial = "")
    val playerXWins by preferencesManager.playerXWins.collectAsState(initial = 0)
    val playerOWins by preferencesManager.playerOWins.collectAsState(initial = 0)
    val drawMatches by preferencesManager.drawMatches.collectAsState(initial = 0)
    val areNamesSet by preferencesManager.areNamesSet.collectAsState(initial = false)

    // Adding a loading state
    var isLoading by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(areNamesSet) {
        // Simulate delay to wait until the data is loaded
        delay(500)
        isLoading = false
    }

    if (isLoading) {
        // Show a loading screen while waiting for the data
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(60.dp))
        }
    } else {

        if (!areNamesSet) {
            // Dialog for setting player names
            var playerXName by rememberSaveable { mutableStateOf("") }
            var playerOName by rememberSaveable { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    "Enter Player Names",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(8.dp),
                    style = TextStyle(brush = Brush.linearGradient(colors = listOf(
                        Color.Blue,
                        Color.Magenta,
                        Color.Green
                    )))
                )
                OutlinedTextField(
                    value = playerXName,
                    onValueChange = { playerXName = it },
                    label = { Text("Player X Name", fontSize = 16.sp) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.player_x),
                            contentDescription = "player_x_icon",
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTrailingIconColor = Color.Magenta,
                        disabledTrailingIconColor = Color.DarkGray,
                        focusedBorderColor = Color.Magenta,
                        unfocusedBorderColor = Color.DarkGray,
                        unfocusedTrailingIconColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        focusedLabelColor = Color.Magenta
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = playerOName,
                    onValueChange = { playerOName = it },
                    label = { Text("Player O Name", fontSize = 16.sp) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.player_o),
                            contentDescription = "player_o_icon",
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTrailingIconColor = Color.Magenta,
                        disabledTrailingIconColor = Color.DarkGray,
                        focusedBorderColor = Color.Magenta,
                        unfocusedBorderColor = Color.DarkGray,
                        unfocusedTrailingIconColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        focusedLabelColor = Color.Magenta
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedButton(
                    onClick = {
                        if (playerOName.isNotEmpty() && playerXName.isNotEmpty()) {
                            scope.launch {
                                preferencesManager.savePlayerNames(playerXName, playerOName)
                            }
                        }
                        else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please enter names")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(11.dp)
                ) {
                    Text("Submit Names")
                }
            }
        } else {
            // Main playground screen
            var currentPlayer by rememberSaveable { mutableStateOf("X") }
            var boardState by rememberSaveable { mutableStateOf(List(9) { null as String? }) }
            var winner by rememberSaveable { mutableStateOf<String?>(null) }
            var isDraw by rememberSaveable { mutableStateOf(false) }
            var timerValue by rememberSaveable { mutableIntStateOf(0) }

            val xImage: Painter = painterResource(id = R.drawable.player_x)
            val oImage: Painter = painterResource(id = R.drawable.player_o)

            fun resetGameState() {
                boardState = List(9) { null }
                currentPlayer = "X"
                winner = null
                isDraw = false
                timerValue = 0
            }

            fun checkGameState() {
                val winningPatterns = listOf(
                    listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
                    listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
                    listOf(0, 4, 8), listOf(2, 4, 6)
                )

                for (pattern in winningPatterns) {
                    val (a, b, c) = pattern
                    if (boardState[a] != null && boardState[a] == boardState[b] && boardState[a] == boardState[c]) {
                        winner = boardState[a]
                        if (isSoundOn) winsMoveSound.start()
                        scope.launch {
                            preferencesManager.updateMatchStats(isXWinner = winner == "X")
                        }
                        timerValue = 3
                        return
                    }
                }

                if (boardState.all { it != null } && winner == null) {
                    isDraw = true
                    if (isSoundOn) drawSound.start()
                    scope.launch {
                        preferencesManager.updateMatchStats(isDraw = true)
                    }
                    timerValue = 3
                }
            }

            LaunchedEffect(timerValue) {
                if (timerValue > 0) {
                    delay(1000)
                    timerValue -= 1
                    if (timerValue == 0) resetGameState()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(onClick = { isSoundOn = !isSoundOn }) {
                            Icon(
                                painter = painterResource(id = if (isSoundOn) R.drawable.baseline_volume_up_24 else R.drawable.baseline_volume_off_24),
                                contentDescription = "Toggle Sound",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        OutlinedButton(onClick = { showDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.score),
                                contentDescription = "Scoreboard",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scoreboard")
                        }
                    }

                    Text(style = TextStyle(brush = Brush.linearGradient(colors = listOf(
                        Color.Blue,
                        Color.Magenta,
                        Color.Green
                    ))),
                        text = "Tic Tac Toe",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Center,
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            for (row in 0..2) {
                                Row {
                                    for (col in 0..2) {
                                        val index = row * 3 + col
                                        val player = boardState[index]

                                        Card(
                                            elevation = CardDefaults.cardElevation(4.dp),
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .size(80.dp)
                                                .clickable {
                                                    if (boardState[index] == null && winner == null && !isDraw) {
                                                        boardState = boardState
                                                            .toMutableList()
                                                            .also { it[index] = currentPlayer }
                                                        checkGameState()
                                                        currentPlayer =
                                                            if (currentPlayer == "X") "O" else "X"
                                                        if (isSoundOn) generalMoveSound.start()
                                                    }
                                                },
                                            colors = CardDefaults.cardColors(containerColor = Color.White)
                                        ) {
                                            if (player != null) {
                                                Icon(
                                                    painter = if (player == "X") xImage else oImage,
                                                    contentDescription = "Player $player",
                                                    modifier = Modifier.fillMaxSize(),
                                                    tint = Color.Magenta
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (timerValue > 0) {
                        Text(style = TextStyle(brush = Brush.linearGradient(colors = listOf(
                            Color.Blue,
                            Color.Magenta,
                            Color.Green
                        ))),
                            text = "Restarts in $timerValue seconds...",
                            fontSize = 16.sp,
                            color = Color.Magenta,
                            textAlign = TextAlign.Center
                        )
                    }

                    OutlinedButton(onClick = {}) {
                        if (winner == null && !isDraw) {
                            val currentTurn =
                                if (currentPlayer == "X") savedPlayerXName else savedPlayerOName
                            Icon(
                                painter = if (currentPlayer == "X") xImage else oImage,
                                contentDescription = "Player Turn",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Player $currentTurn turn")
                        } else if (winner != null) {
                            val winnerName =
                                if (winner == "X") savedPlayerXName else savedPlayerOName
                            Icon(
                                painter = if (winner == "X") xImage else oImage,
                                contentDescription = "Winner",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$winnerName Won!")
                        } else {
                            Text("Match Draws!")
                        }
                    }

                }
            }

            if (showDialog) {
                BasicAlertDialog (
                    onDismissRequest = { showDialog = false }) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Text("Scoreboard", fontSize = 22.sp, color = Color.Magenta,
                            modifier = Modifier.padding(top = 21.dp, start = 21.dp, bottom = 11.dp),
                            style = TextStyle(brush = Brush.linearGradient(colors = listOf(
                                Color.Blue,
                                Color.Magenta,
                                Color.Green
                            )))
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 21.dp)
                                .border(
                                    width = 3.dp,
                                    color = Color.Magenta,
                                    shape = RoundedCornerShape(11.dp)
                                ),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.player_x),
                                    contentDescription = "player_x_score_img",
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(Modifier.width(11.dp))
                                Text(
                                    "Player $savedPlayerXName score: $playerXWins",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    style = TextStyle(brush = Brush.linearGradient(colors = listOf(
                                        Color.Black,
                                        Color.Magenta
                                    )))
                                )
                            }

                            HorizontalDivider(Modifier.height(2.dp), color = Color.Magenta)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.player_o),
                                    contentDescription = "player_o_score_img",
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(Modifier.width(11.dp))
                                Text(
                                    "Player $savedPlayerOName score: $playerOWins",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    style = TextStyle(brush = Brush.linearGradient(colors = listOf(
                                        Color.Black,
                                        Color.Magenta
                                    )))
                                )
                            }

                            HorizontalDivider(Modifier.height(2.dp), color = Color.Magenta)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(R.drawable.player_x),
                                        contentDescription = "player_x_score_img",
                                        modifier = Modifier.size(50.dp)
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.player_o),
                                        contentDescription = "player_0_score_img",
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                                Spacer(Modifier.width(11.dp))
                                Text(
                                    "Draws: $drawMatches",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    style = TextStyle(brush = Brush.linearGradient(colors = listOf(
                                        Color.Black,
                                        Color.Magenta
                                    )))
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                    .padding(top = 11.dp, start = 21.dp, bottom = 21.dp, end = 21.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Magenta),
                                onClick = {
                                    scope.launch {
                                        preferencesManager.resetPlayerStats()
                                    }
                                    showDialog = false
                                }) {
                                Text("Reset Game")
                            }

                            Spacer(Modifier.width(11.dp))

                            OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Magenta),
                                onClick = { showDialog = false }
                            ) {
                                Text("Close")
                            }
                        }
                    }

                }
            }
        }
    }
}
