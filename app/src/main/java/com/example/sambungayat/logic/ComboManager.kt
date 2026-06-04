package com.example.sambungayat.gamelogic

class ComboManager {

    private var combo: Int = 0

    fun onCorrectAnswer() {
        combo++
    }

    fun onWrongAnswer() {
        combo = 0
    }

    fun getCombo(): Int = combo

    fun reset() {
        combo = 0
    }
}
