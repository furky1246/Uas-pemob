package com.example.sambungayat.gamelogic

class SurahProgressManager {

    private val unlockedSurahList: MutableList<Int> = mutableListOf(1)

    private var currentSurahId: Int = 0
    private var totalAyat: Int = 0
    private var ayatSelesai: Int = 0

    fun startSurah(surahId: Int, totalAyat: Int) {
        currentSurahId = surahId
        this.totalAyat = totalAyat
        ayatSelesai = 0
    }

    fun onAyatCompleted() {
        ayatSelesai++
    }

    fun isSurahCompleted(): Boolean = ayatSelesai == totalAyat

    fun getCurrentAyatNumber(): Int = ayatSelesai + 1

    fun getTotalAyat(): Int = totalAyat

    fun getCurrentSurahId(): Int = currentSurahId

    fun unlockNextSurah(totalSurahInApp: Int): Int? {
        val nextSurahId = currentSurahId + 1
        if (nextSurahId > totalSurahInApp) return null
        if (!unlockedSurahList.contains(nextSurahId)) {
            unlockedSurahList.add(nextSurahId)
        }
        return nextSurahId
    }

    fun isSurahUnlocked(surahId: Int): Boolean = unlockedSurahList.contains(surahId)

    fun getUnlockedSurahList(): List<Int> = unlockedSurahList.toList()
}
