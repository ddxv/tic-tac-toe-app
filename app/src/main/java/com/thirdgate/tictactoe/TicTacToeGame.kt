package com.thirdgate.tictactoe

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.RectangleShape


enum class Player {
    NONE, X, O
}

@Composable
fun TicTacToeGame() {
    var currentPlayer by remember { mutableStateOf(Player.X) }
    var board by remember { mutableStateOf(Array(3) { Array(3) { Player.NONE } }) }
    var winner by remember { mutableStateOf(Player.NONE) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (winner != Player.NONE) {
            Text(
                text = "${winner} Wins!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )
        } else {
            Text(
                text = "$currentPlayer's Turn",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        for (i in board.indices) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (j in board[i].indices) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(
                                width = if (j < board[i].indices.last) 2.dp else 0.dp,
                                color = Color.Black,
                                shape = RectangleShape,
                            )
                            .border(
                                width = if (i < board.indices.last) 2.dp else 0.dp,
                                color = Color.Black,
                                shape = RectangleShape,
                                //top = false, start = false, end = false
                            )
                            .clickable {
                                if (board[i][j] == Player.NONE && winner == Player.NONE) {
                                    board[i][j] = currentPlayer
                                    currentPlayer =
                                        if (currentPlayer == Player.X) Player.O else Player.X
                                    checkWinner(board)?.let { winner = it }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (board[i][j]) {
                            Player.X -> Text("X", fontSize = 24.sp, color = MaterialTheme.colorScheme.error)
                            Player.O -> Text("O", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                            else -> { }
                        }
                    }
//                    if (j < board[i].indices.last) {
//                        // Add vertical Divider, except for the last column
//                        Spacer(
//                            modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.Black)
//                        )
//                    }
                }
            }
        }

        // Display the Reset button only when the game has ended
        if (winner != Player.NONE) {
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    board = Array(3) { Array(3) { Player.NONE } }
                    currentPlayer = Player.X
                    winner = Player.NONE
                }
            ) {
                Text(text = "Reset")
            }
        }
    }
}


fun checkWinner(board: Array<Array<Player>>): Player? {
    // Check rows, columns, diagonals
    for (i in 0..2) {
        if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != Player.NONE) {
            return board[i][0]
        }
        if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != Player.NONE) {
            return board[0][i]
        }
    }
    if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != Player.NONE) {
        return board[0][0]
    }
    if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != Player.NONE) {
        return board[0][2]
    }
    return null
}
