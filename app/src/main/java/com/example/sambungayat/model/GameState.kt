package com.example.sambungayat.gamelogic

sealed class GameState {

    data class Idle(
        val shuffledWords: List<String>,
        val ayatNumber: Int,
        val totalAyat: Int
    ) : GameState()

    data class AnswerCorrect(
        val score: Int,
        val combo: Int,
        val currentStreak: Int,
        val bestStreak: Int
    ) : GameState()

    object AnswerWrong : GameState()

    data class SurahCompleted(
        val surahId: Int,
        val finalScore: Int,
        val bestStreak: Int,
        val unlockedNextSurah: Int?
    ) : GameState()
}
