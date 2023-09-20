package com.thirdgate.tictactoe

import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


enum class Player {
    NONE, X, O, DRAW
}

@Composable
fun TicTacToeGame() {
    var currentPlayer by remember { mutableStateOf(Player.X) }
    var board by remember { mutableStateOf(Array(3) { Array(3) { Player.NONE } }) }
    var winner by remember { mutableStateOf(Player.NONE) }

    val coroutineScope = rememberCoroutineScope()

    val lineEdgePadding = 20.dp

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        if (winner != Player.NONE) {
            var myText = "$winner Wins!"
            if (winner == Player.DRAW) {
            myText = "Draw!"
            }
            Text(
                text = myText,
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
                            .background(MaterialTheme.colorScheme.background)
                            .clip(RoundedCornerShape(30.dp))
                            .clickable {
                                if (board[r][c] == Player.NONE && winner == Player.NONE) {
                                    board[r][c] = currentPlayer
                                    currentPlayer =
                                        if (currentPlayer == Player.X) Player.O else Player.X
                                    checkWinner(board)?.let { winner = it }
                                    Log.i("Game","board=${board[r][c]}")
                                    if (winner == Player.NONE && currentPlayer == Player.O) {
                                        coroutineScope.launch {
                                            val bestMove = findBestMove(board)
                                            board[bestMove.first][bestMove.second] = currentPlayer
                                            currentPlayer = Player.X
                                            checkWinner(board)?.let { winner = it }
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        //Text("Test:$r,$c")
                        when (board[r][c]) {
                            Player.X -> Text("X", fontSize=99.sp, color = MaterialTheme.colorScheme.error)
                            Player.O -> {
                                Text("O", fontSize = 99.sp, color = MaterialTheme.colorScheme.primary)}
                            else -> { }
                        }
                    }

                    if (c < board[r].indices.last) {
                       var topPadding = 0.dp
                       var bottomPadding = 0.dp
                        if (r == board[r].indices.start) {
                        topPadding= lineEdgePadding
                        }
                        if (r == board[r].indices.last)
                        {
                            bottomPadding= lineEdgePadding
                        }
//                        Divider(color = Color.Black,
//                            modifier = Modifier
//                                .fillMaxHeight()
//                                .width(2.dp)
//                                .padding(top = topPadding, bottom = bottomPadding)
//                        )
                        Spacer(
                            //conte = Color.Black,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(2.dp)
                                .padding(top = topPadding, bottom = bottomPadding).background(Color.Black)
                        )
                    }
                }
            }
            if (r < board[r].indices.last) {
                Divider(color = Color.Black, modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .padding(horizontal = lineEdgePadding))
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
    // Check rows, columns
    for (i in 0..2) {
        if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != Player.NONE) {
            return board[i][0]
        }
        if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != Player.NONE) {
            return board[0][i]
        }
    }
    // diagonals
    if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != Player.NONE) {
        return board[0][0]
    }
    if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != Player.NONE) {
        return board[0][2]
    }
    // check draw
    if (board.all { row -> row.all { it != Player.NONE } }) {
            return Player.DRAW // Indicate draw by returning Player.NONE (or you can introduce another enum value for Draw)
        }
    return null
}

fun evaluate(board: Array<Array<Player>>): Int {
    for (row in 0..2) {
        if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
            when (board[row][0]) {
                Player.X -> return +10
                Player.O -> return -10
                else -> {}
            }
        }
    }
    for (col in 0..2) {
        if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
            when (board[0][col]) {
                Player.X -> return +10
                Player.O -> return -10
                else -> {}
            }
        }
    }
    if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
        when (board[0][0]) {
            Player.X -> return +10
            Player.O -> return -10
            else -> {}
        }
    }
    if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
        when (board[0][2]) {
            Player.X -> return +10
            Player.O -> return -10
            else -> {}
        }
    }
    return 0
}

fun areMovesLeft(board: Array<Array<Player>>): Boolean {
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j] == Player.NONE) {
                return true
            }
        }
    }
    return false
}

fun minimax(board: Array<Array<Player>>, depth: Int, isMax: Boolean): Int {
    val score = evaluate(board)
    if (score == 10) return score
    if (score == -10) return score
    if (!areMovesLeft(board)) return 0
    if (isMax) {
        var best = Int.MIN_VALUE
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == Player.NONE) {
                    board[i][j] = Player.X
                    best = maxOf(best, minimax(board, depth + 1, !isMax))
                    board[i][j] = Player.NONE
                }
            }
        }
        return best
    } else {
        var best = Int.MAX_VALUE
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == Player.NONE) {
                    board[i][j] = Player.O
                    best = minOf(best, minimax(board, depth + 1, !isMax))
                    board[i][j] = Player.NONE
                }
            }
        }
        return best
    }
}

//suspend fun findBestMove(board: Array<Array<Player>>): Pair<Int, Int> {
fun findBestMove(board: Array<Array<Player>>): Pair<Int, Int> {
    Log.i("Game", "Find best move")
    var bestVal = Int.MAX_VALUE
    var bestMove = Pair(-1, -1)
    for (i in 0..2) {
        for (j in 0..2) {
            if (board[i][j] == Player.NONE) {
                board[i][j] = Player.O
                val moveVal = minimax(board, 0, true)
                board[i][j] = Player.NONE
                if (moveVal < bestVal) {
                    bestMove = Pair(i, j)
                    bestVal = moveVal
                }
            }
        }
    }
    //delay(200)
    return bestMove
}
