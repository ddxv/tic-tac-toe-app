package com.thirdgate.tictactoe

import kotlinx.serialization.Serializable

@Serializable
data class WidgetInfo(
    val games: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0
)


