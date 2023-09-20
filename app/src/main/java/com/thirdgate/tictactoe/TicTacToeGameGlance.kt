package com.thirdgate.tictactoe

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle

import kotlinx.coroutines.launch


@Composable
fun TicTacToeGameGlance() {
    var currentPlayer = remember { mutableStateOf(Player.X) }
    var board = remember { mutableStateOf(Array(3) { Array(3) { Player.NONE } }) }
    var winner:MutableState<Player> = remember { mutableStateOf(Player.NONE) }
    val coroutineScope = rememberCoroutineScope()

    val lineEdgePadding = 20.dp

    Log.i("Widget", "WidgetGameStart")

    GlanceTheme {
        GameStatusText(winner, currentPlayer.value)
        //Column() {}
        LazyColumn(
            modifier = GlanceModifier.padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(board.value) { r, row ->
                Row(modifier=GlanceModifier.fillMaxWidth()) {
                    row.forEachIndexed { c, cell ->
                        Log.i("Widget", "forEach Cell create box=$r,$c")
                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                //.background(Color.Gray).width(50.dp).height(50.dp)
                                .background(Color.Gray)
                                .clickable {
                                    Log.i("Game", "Clicked: box=$r,$c:${board.value[r][c]}")
                                    if (board.value[r][c] == Player.NONE && winner.value == Player.NONE) {
                                        board.value[r][c] = currentPlayer.value
                                        currentPlayer.value =
                                            if (currentPlayer.value == Player.X) Player.O else Player.X
                                        checkWinner(board.value)?.let { winner.value = it }
                                        Log.i(
                                            "Game",
                                            "Tap: box:$r,$c=${board.value[r][c]}, set new currentPlayer=${currentPlayer.value}"
                                        )
                                        if (winner.value == Player.NONE && currentPlayer.value == Player.O) {
                                            coroutineScope.launch {
                                                val bestMove = findBestMove(board.value)
                                                Log.i(
                                                    "Game",
                                                    "Tap: box:$r,$c=${board.value[r][c]}, currentPlayer=${currentPlayer.value} bestMove=$bestMove"
                                                )
                                                board.value[bestMove.first][bestMove.second] =
                                                    currentPlayer.value
                                                currentPlayer.value = Player.X
                                            }
                                            checkWinner(board.value)?.let { winner.value = it }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Log.i("Widget", "Inside box logic:$r,$c")
                            Text("b:$r,$c")
                            when (board.value[r][c]) {
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
                        if (c < board.value[r].indices.last) {
                            var topPadding = 0.dp
                            var bottomPadding = 0.dp
                            if (r == board.value[r].indices.start) {
                                topPadding = lineEdgePadding
                            }
                            if (r == board.value[r].indices.last) {
                                bottomPadding = lineEdgePadding
                            }
                            // Spacer is with each Box, not each column
                            Log.i("Widget", "Placing Spacer Red")
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
                if (r < board.value[r].indices.last) {
                    Log.i("Widget", "Placing Spacer Blue")
                    Spacer(
                        modifier = GlanceModifier.fillMaxWidth().height(2.dp)
                            .padding(horizontal = lineEdgePadding).background(Color.Blue)
                    )
                }
            }
            item {
                if (winner.value != Player.NONE) {
                    GameOverView(winner.value) {
                        board.value = Array(3) { Array(3) { Player.NONE } }
                        currentPlayer.value = Player.X
                        winner.value = Player.NONE
                        actionRunCallback<RefreshAction>()
                    }
                }
            }
        }
    }
}

@Composable
fun GameStatusText(winner: State<Player>, currentPlayer: Player) {
    val displayText = when (winner.value) {
        Player.NONE -> "$currentPlayer's Turn"
        Player.DRAW -> "Draw!"
        else -> "${winner.value} Wins!"
    }

    val fontSize = if (winner.value == Player.NONE) 24.sp else 20.sp

    Text(
        text = displayText,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    )
}


@Composable
fun GameOverView(winner: Player, onReset: () -> Unit) {
    Spacer(modifier = GlanceModifier.height(20.dp))
    Button(
        text = "Reset",
        onClick = onReset
    )
}


//@Composable
//fun TicTacToeRow(
//    r: Int,
//    row: Array<Player>,
//    board: Array<Array<Player>>,
//    lineEdgePadding: Dp,
//    winner: MutableState<Player>,
//    currentPlayer: MutableState<Player>,
//    coroutineScope: CoroutineScope
//) {
//        row.forEachIndexed { c, cell ->
//            Log.i("Widget", "forEach Cell create Box:$r,$c")
//            Box(
//                modifier = GlanceModifier
//                    //.defaultWeight()
//                    .background(Color.Gray).width(50.dp).height(50.dp)
//                    .clickable {
//                        Log.i("Game", "Tap: box:$r,$c=${board[r][c]}")
//                        if (board[r][c] == Player.NONE && winner.value == Player.NONE) {
//                            board[r][c] = currentPlayer.value
//                            currentPlayer.value =
//                                if (currentPlayer.value == Player.X) Player.O else Player.X
//                            checkWinner(board)?.let { winner.value = it }
//                            Log.i(
//                                "Game",
//                                "Tap: box:$r,$c=${board[r][c]}, set new currentPlayer=${currentPlayer.value}"
//                            )
//                            if (winner.value == Player.NONE && currentPlayer.value == Player.O) {
////                                Log.i(
////                                    "Game",
////                                    "Tap: box=$r,$c:${board[r][c]}, launch coroutine for find best move"
////                                )
//                                //coroutineScope.launch {
//                                val bestMove = findBestMove(board)
//                                Log.i(
//                                    "Game",
//                                    "Tap: box:$r,$c=${board[r][c]}, currentPlayer=${currentPlayer.value} bestMove=$bestMove"
//                                )
//                                board[bestMove.first][bestMove.second] = currentPlayer.value
//                                //}
//                                currentPlayer.value = Player.X
//                                checkWinner(board)?.let { winner.value = it }
//                            }
//                        }
//                    },
//                contentAlignment = Alignment.Center
//            ) {
//                Log.i("Widget", "Inside box logic?:$r,$c")
//                Text("b:$r,$c")
//                when (board[r][c]) {
//                    Player.X -> Text(
//                        "X",
//                        style = TextStyle(
//                            fontSize = 20.sp,
//                            color = GlanceTheme.colors.error
//                        )
//                    )
//                    Player.O -> {
//                        Text(
//                            "O",
//                            style = TextStyle(
//                                fontSize = 20.sp,
//                                color = GlanceTheme.colors.primary
//                            )
//                        )
//                    }
//                    else -> {}
//                }
//            }
//            if (c < board[r].indices.last) {
//                var topPadding = 0.dp
//                var bottomPadding = 0.dp
//                if (r == board[r].indices.start) {
//                    topPadding = lineEdgePadding
//                }
//                if (r == board[r].indices.last) {
//                    bottomPadding = lineEdgePadding
//                }
//                // Spacer is with each Box, not each column
//                Log.i("Widget", "Placing Spacer Red")
//                Spacer(
//                    modifier = GlanceModifier
//                        .fillMaxHeight()
//                        .width(2.dp)
//                        .padding(top = topPadding, bottom = bottomPadding)
//                        .background(Color.Red)
//                )
//            }
//        }
//        if (r < board.lastIndex) {
//            Spacer(modifier = GlanceModifier
//                .fillMaxWidth()
//                .height(2.dp)
//                .padding(horizontal = lineEdgePadding).background(Color.Magenta))
//        }
//}



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

