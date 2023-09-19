package com.thirdgate.tictactoe

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
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


enum class Player {
    NONE, X, O
}

@Composable
fun TicTacToeGame() {
    var currentPlayer by remember { mutableStateOf(Player.X) }
    var board by remember { mutableStateOf(Array(3) { Array(3) { Player.NONE } }) }
    var winner by remember { mutableStateOf(Player.NONE) }

    Column(
        modifier = Modifier,
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

        for (r in board.indices) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                for (c in board[r].indices) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                if (board[r][c] == Player.NONE && winner == Player.NONE) {
                                    board[r][c] = currentPlayer
                                    currentPlayer =
                                        if (currentPlayer == Player.X) Player.O else Player.X
                                    checkWinner(board)?.let { winner = it }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        //Text("Test:$r,$c")
                        when (board[r][c]) {
                            Player.X -> Text("X", fontSize=24.sp, color = MaterialTheme.colorScheme.error)
                            Player.O -> Text("O", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                            else -> { }
                        }
                    }

                    if (c < board[r].indices.last) {
                        Divider(color = Color.Black, modifier = Modifier.fillMaxHeight().width(2.dp))
                    }
                }
            }
            if (r < board[r].indices.last) {
                Divider(color = Color.Black, modifier = Modifier.fillMaxWidth().height(2.dp))
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
