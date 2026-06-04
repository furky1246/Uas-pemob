package com.example.sambungayat.gamelogic

class ScoreManager {

    companion object {
        const val POINTS_PER_CORRECT = 10
    }

    private var score: Int = 0

    fun onCorrectAnswer() {
        score += POINTS_PER_CORRECT
    }

    fun onWrongAnswer() {
        // skor tidak berubah jika salah
    }

    fun getScore(): Int = score

    fun reset() {
        score = 0
    }
}
