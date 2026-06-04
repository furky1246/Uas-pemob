package com.example.sambungayat.gamelogic

class ValidationManager {

    fun validate(correctOrder: List<String>, userOrder: List<String>): Boolean {
        return correctOrder == userOrder
    }

    fun splitAyat(ayatText: String): List<String> {
        return ayatText.trim().split(" ").filter { it.isNotEmpty() }
    }

    fun shuffle(words: List<String>): List<String> {
        return words.shuffled()
    }
}
