package com.thirdgate.tictactoe

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle



@Composable
fun TicTacToeGameGlance() {
    val boardSize = 3
    var currentPlayer = remember { mutableStateOf(Player.X) }
    var board = remember { mutableStateOf(Array(boardSize) { Array(boardSize) { Player.NONE } }) }
    var winner:MutableState<Player> = remember { mutableStateOf(Player.NONE) }
    val lineEdgePadding = 20.dp

    val widgetSize = LocalSize.current

    val boxSize = widgetSize.height.value

    Log.i("Widget", "WidgetGameStart boxSize=$boxSize")

    GlanceTheme {
        GameStatusText(winner, currentPlayer.value)
        LazyColumn(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(board.value) { r, row ->
                Column() {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.Bottom
                    ) {
                        row.forEachIndexed { c, cell ->
                            Log.i("Widget", "forEach Cell create box=$r,$c")
                            Box(
                                modifier = GlanceModifier
                                    .defaultWeight()
                                    .height(50.dp)
                                    .background(GlanceTheme.colors.background)
                                    .clickable {
                                        Log.i("Game", "Tap: box:$r,$c=${board.value[r][c]} start")
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
                                                //coroutineScope.launch {
                                                val bestMove = findBestMove(board.value)
                                                Log.i(
                                                    "Game",
                                                    "Tap: box:$r,$c=${board.value[r][c]}, currentPlayer=${currentPlayer.value} bestMove=$bestMove"
                                                )
                                                board.value[bestMove.first][bestMove.second] =
                                                    currentPlayer.value
                                                currentPlayer.value = Player.X
                                                //}
                                                checkWinner(board.value)?.let { winner.value = it }
                                            }
                                        }
                                        Log.i(
                                            "Game",
                                            "Tap: box:$r,$c=${board.value[r][c]}, currentPlayer=${currentPlayer.value} finish"
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Log.i("Widget", "Inside box logic:$r,$c")
                                //Text("b:$r,$c")
                                when (board.value[r][c]) {
                                    Player.X -> Text(
                                        "X",
                                        style = TextStyle(
                                            fontSize = 30.sp,
                                            color = GlanceTheme.colors.primary
                                        )
                                    )

                                    Player.O -> {
                                        Text(
                                            "O",
                                            style = TextStyle(
                                                fontSize = 30.sp,
                                                color = GlanceTheme.colors.error
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
                                        .width(2.dp)
                                        .padding(top = topPadding, bottom = bottomPadding)
                                        .background(GlanceTheme.colors.primary)
                                )
                            }
                        }
                    }
                    if (r < board.value[r].indices.last) {
                        Log.i("Widget", "Placing Spacer horizontal")
                        Spacer(
                            modifier = GlanceModifier.fillMaxWidth().height(2.dp)
                                .background(GlanceTheme.colors.primary).padding(horizontal = 50.dp)
                        )
                    }
                }
            }
            item {
                if (winner.value != Player.NONE) {
                    GameOverView(winner.value) {
                        board.value = Array(boardSize) { Array(boardSize) { Player.NONE } }
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
    Row(horizontalAlignment = Alignment.CenterHorizontally, modifier = GlanceModifier.fillMaxWidth()) {
        Text(
            text = displayText,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
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

