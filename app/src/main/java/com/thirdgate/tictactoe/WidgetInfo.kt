package com.thirdgate.tictactoe

import kotlinx.serialization.Serializable

@Serializable
data class WidgetInfo(
    val games: Int,
    val wins: Int,
    val losses: Int
)


