package com.example.sambungayat.gamelogic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val validationManager = ValidationManager()
    private val scoreManager = ScoreManager()
    private val comboManager = ComboManager()
    private val streakManager = StreakManager()
    private val surahProgressManager = SurahProgressManager()

    private val gameManager = GameManager(
        validationManager,
        scoreManager,
        comboManager,
        streakManager,
        surahProgressManager
    )

    // State utama yang diobservasi oleh Activity/Fragment
    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    // Menyimpan urutan kata user yang aktif dimanipulasi saat drag and drop
    private val _userOrder = MutableLiveData<MutableList<String>>()
    val userOrder: LiveData<MutableList<String>> = _userOrder

    // ------------------------------------------------------------------
    // SETUP
    // ------------------------------------------------------------------

    fun setTotalSurahInApp(total: Int) {
        gameManager.setTotalSurahInApp(total)
    }

    // ------------------------------------------------------------------
    // MEMULAI SURAH
    // ------------------------------------------------------------------

    fun startSurah(surahId: Int, totalAyat: Int) {
        gameManager.startSurah(surahId, totalAyat)
    }

    // ------------------------------------------------------------------
    // MEMUAT AYAT
    // ------------------------------------------------------------------

    fun loadAyat(ayatText: String) {
        val idleState = gameManager.loadAyat(ayatText)
        _userOrder.value = idleState.shuffledWords.toMutableList()
        _gameState.value = idleState
    }

    // ------------------------------------------------------------------
    // DRAG AND DROP
    // ------------------------------------------------------------------

    fun onDragAndDrop(fromIndex: Int, toIndex: Int) {
        val currentList = _userOrder.value ?: return
        gameManager.onDragAndDrop(currentList, fromIndex, toIndex)
        // Trigger observer dengan list yang sama (sudah dimodifikasi in-place)
        _userOrder.value = currentList
    }

    // ------------------------------------------------------------------
    // VALIDASI JAWABAN
    // ------------------------------------------------------------------

    fun checkAnswer() {
        val currentUserOrder = _userOrder.value ?: return
        val result = gameManager.checkAnswer(currentUserOrder)
        _gameState.value = result
    }

    // ------------------------------------------------------------------
    // NAVIGASI AYAT BERIKUTNYA
    // ------------------------------------------------------------------

    // Dipanggil Activity setelah menampilkan feedback AnswerCorrect,
    // untuk memuat ayat selanjutnya dalam surah yang sama.
    fun loadNextAyat(nextAyatText: String) {
        loadAyat(nextAyatText)
    }

    // ------------------------------------------------------------------
    // QUERY STATUS
    // ------------------------------------------------------------------

    fun isSurahUnlocked(surahId: Int): Boolean = gameManager.isSurahUnlocked(surahId)

    fun getUnlockedSurahList(): List<Int> = gameManager.getUnlockedSurahList()

    fun getCurrentScore(): Int = gameManager.getCurrentScore()

    fun getCurrentCombo(): Int = gameManager.getCurrentCombo()
}
