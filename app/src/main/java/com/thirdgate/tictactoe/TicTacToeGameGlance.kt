package com.thirdgate.tictactoe

import android.content.Context
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.Dimension

import kotlinx.coroutines.launch




@Composable
fun TicTacToeGameGlance() {
    var currentPlayer by remember { mutableStateOf(Player.X) }
    var board by remember { mutableStateOf(Array(3) { Array(3) { Player.NONE } }) }
    var winner by remember { mutableStateOf(Player.NONE) }

    val coroutineScope = rememberCoroutineScope()

    val lineEdgePadding = 20.dp

    Log.i("Widget", "WidgetGameStart")

    GlanceTheme {
    LazyColumn(
        modifier = GlanceModifier.padding(5.dp),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        if (winner != Player.NONE) {
//            var myText = "$winner Wins!"
//            if (winner == Player.DRAW) {
//                myText = "Draw!"
//            }
//            Text(
//                text = myText,
//                style = TextStyle(
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                ),
//            )
//        } else {
//            Text(
//                text = "$currentPlayer's Turn",
//                style = TextStyle(
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                )
//            )
//        }
        itemsIndexed(board) { r, row ->
            TicTacToeRow(r, row, board, lineEdgePadding)
//            Row(
//                modifier = GlanceModifier.height(30.dp)
//            ) {
//                row.forEachIndexed { c, cell ->
//
//            }
            if (r < board[r].indices.last) {
                Spacer(modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .padding(horizontal = lineEdgePadding).background(Color.Blue))
            }
        }


        // Added this for the reset button
        item {
            if (winner != Player.NONE) {
                GameOverView(winner) {
                    board = Array(3) { Array(3) { Player.NONE } }
                    currentPlayer = Player.X
                    winner = Player.NONE
                    actionRunCallback<RefreshAction>()
                }
            }
        }




//        // Display the Reset button only when the game has ended
//        if (winner != Player.NONE) {
//            Spacer(modifier = GlanceModifier.height(20.dp))
//            Button(
//                text="Reset",
//                onClick= {
//                    board = Array(3) { Array(3) { Player.NONE } }
//                    currentPlayer = Player.X
//                    winner = Player.NONE
//                    actionRunCallback<RefreshAction>()
//                }
//            )
//        }
    }
}
}

@Composable
fun GameOverView(winner: Player, onReset: () -> Unit) {
    Spacer(modifier = GlanceModifier.height(20.dp))
    Button(
        text = "Reset",
        onClick = onReset
    )
}


@Composable
fun TicTacToeRow(r: Int, row: Array<Player>, board: Array<Array<Player>>, lineEdgePadding: Dp) {
    Row(modifier = GlanceModifier.height(30.dp)) {
        row.forEachIndexed { c, cell ->
            // ... your existing code
            //Log.i("Widget", "Row:r=$r")
            //for (c in board[r].indices) {
            //Log.i("Widget","Row:r=$r,Col:c=$c")
            Box(
                modifier = GlanceModifier
                    //.defaultWeight()
                    //.background(MaterialTheme.colorScheme.background).width(30.dp)
                    .background(Color.Gray).width(20.dp).height(20.dp)
                    .clickable {
                        //Log.i("Game", "Clicked: box=$r,$c:${board[r][c]}, currentPlayer=$currentPlayer")
//                                if (board[r][c] == Player.NONE && winner == Player.NONE) {
//                                    board[r][c] = currentPlayer
//                                    currentPlayer =
//                                        if (currentPlayer == Player.X) Player.O else Player.X
//                                    checkWinner(board)?.let { winner = it }
//                                    Log.i("Game", "Clicked: box=$r,$c:${board[r][c]}, set new currentPlayer=$currentPlayer")
//                                    if (winner == Player.NONE && currentPlayer == Player.O) {
//                                        Log.i("Game", "Clicked: box=$r,$c:${board[r][c]}, launch coroutine for find best move")
//                                        coroutineScope.launch {
//                                            val bestMove = findBestMove(board)
//                                            Log.i("Game", "Clicked: box=$r,$c:${board[r][c]}, currentPlayer=$currentPlayer bestMove=$bestMove")
//                                            board[bestMove.first][bestMove.second] = currentPlayer
//                                        }
//                                        currentPlayer = Player.X
//                                        checkWinner(board)?.let { winner = it }
//                                    }
//                                }
                    },
                contentAlignment = Alignment.Center
            ) {
                //Log.i("Widget", "Test:$r,$c")
                //Text("b:$r,$c")
                when (board[r][c]) {
                    Player.X -> Text(
                        "X",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = GlanceTheme.colors.error
                        )
                    )
                    Player.O -> {
                        Text(
                            "O",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = GlanceTheme.colors.primary
                            )
                        )
                    }
                    else -> {}
                }
            }

            if (c < board[r].indices.last) {
                var topPadding = 0.dp
                var bottomPadding = 0.dp
                if (r == board[r].indices.start) {
                    topPadding = lineEdgePadding
                }
                if (r == board[r].indices.last) {
                    bottomPadding = lineEdgePadding
                }
                // Spacer is with each Box, not each column
                Spacer(
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .padding(top = topPadding, bottom = bottomPadding)
                        .background(Color.Red)
                )
            }
        }

        }
//    if (r < board.lastIndex) {
//        Spacer(modifier = GlanceModifier
//            .fillMaxWidth()
//            .height(2.dp)
//            .padding(horizontal = lineEdgePadding).background(Color.Blue))
//    }
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        MyWidget().update(context, glanceId)
    }
}

