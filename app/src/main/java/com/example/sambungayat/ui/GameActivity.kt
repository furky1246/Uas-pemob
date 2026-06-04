package com.example.sambungayat.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sambungayat.databinding.ActivityGameBinding
import com.example.sambungayat.gamelogic.GameState
import com.example.sambungayat.gamelogic.GameViewModel

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()

    private lateinit var wordAdapter: WordAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    // Data dummy untuk testing — pengganti database
    private val dummySurahId = 1
    private val dummyAyatList = listOf(
        "بِسْمِ اللَّهِ الرَّحْمٰنِ الرَّحِيْمِ",
        "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِيْنَ",
        "الرَّحْمٰنِ الرَّحِيْمِ",
        "مَالِكِ يَوْمِ الدِّيْنِ",
        "اِيَّاكَ نَعْبُدُ وَاِيَّاكَ نَسْتَعِيْنُ",
        "اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيْمَ",
        "صِرَاطَ الَّذِيْنَ اَنْعَمْتَ عَلَيْهِمْ"
    )
    private var currentAyatIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()
        startGame()
    }

    // ------------------------------------------------------------------
    // SETUP
    // ------------------------------------------------------------------

    private fun setupRecyclerView() {
        wordAdapter = WordAdapter(
            words = mutableListOf(),
            onDragStarted = { viewHolder -> itemTouchHelper.startDrag(viewHolder) }
        )

        val callback = DragDropCallback(
            adapter = wordAdapter,
            onMoveFinished = { from, to -> viewModel.onDragAndDrop(from, to) }
        )

        itemTouchHelper = ItemTouchHelper(callback)

        binding.rvWords.apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            adapter = wordAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun setupObservers() {
        viewModel.gameState.observe(this) { state ->
            when (state) {
                is GameState.Idle -> handleIdle(state)
                is GameState.AnswerCorrect -> handleAnswerCorrect(state)
                is GameState.AnswerWrong -> handleAnswerWrong()
                is GameState.SurahCompleted -> handleSurahCompleted(state)
            }
        }
    }

    private fun setupListeners() {
        binding.btnPeriksa.setOnClickListener {
            viewModel.checkAnswer()
        }
    }

    private fun startGame() {
        viewModel.setTotalSurahInApp(114)
        viewModel.startSurah(dummySurahId, dummyAyatList.size)
        loadCurrentAyat()
    }

    // ------------------------------------------------------------------
    // LOAD AYAT
    // ------------------------------------------------------------------

    private fun loadCurrentAyat() {
        if (currentAyatIndex < dummyAyatList.size) {
            viewModel.loadAyat(dummyAyatList[currentAyatIndex])
        }
    }

    // ------------------------------------------------------------------
    // HANDLER STATE
    // ------------------------------------------------------------------

    private fun handleIdle(state: GameState.Idle) {
        binding.tvAyatProgress.text = "Ayat ${state.ayatNumber} dari ${state.totalAyat}"
        binding.tvFeedback.visibility = View.INVISIBLE
        binding.btnPeriksa.isEnabled = true

        // Perbarui adapter dengan kata acak yang baru
        wordAdapter = WordAdapter(
            words = state.shuffledWords.toMutableList(),
            onDragStarted = { viewHolder -> itemTouchHelper.startDrag(viewHolder) }
        )

        val callback = DragDropCallback(
            adapter = wordAdapter,
            onMoveFinished = { from, to -> viewModel.onDragAndDrop(from, to) }
        )

        itemTouchHelper = ItemTouchHelper(callback)
        binding.rvWords.adapter = wordAdapter
        itemTouchHelper.attachToRecyclerView(binding.rvWords)
    }

    private fun handleAnswerCorrect(state: GameState.AnswerCorrect) {
        binding.tvFeedback.visibility = View.VISIBLE
        binding.tvFeedback.text = "✓ Benar!  Skor: ${state.score}  Combo: ${state.combo}  Streak: ${state.currentStreak}"
        binding.btnPeriksa.isEnabled = false

        // Jeda singkat lalu lanjut ke ayat berikutnya
        binding.root.postDelayed({
            currentAyatIndex++
            loadCurrentAyat()
        }, 1200)
    }

    private fun handleAnswerWrong() {
        binding.tvFeedback.visibility = View.VISIBLE
        binding.tvFeedback.text = "✗ Salah. Susun ulang dan coba lagi."
        Toast.makeText(this, "Urutan belum tepat", Toast.LENGTH_SHORT).show()
    }

    private fun handleSurahCompleted(state: GameState.SurahCompleted) {
        binding.tvFeedback.visibility = View.VISIBLE
        binding.btnPeriksa.isEnabled = false

        val pesanUnlock = if (state.unlockedNextSurah != null) {
            "Surah ${state.unlockedNextSurah} terbuka!"
        } else {
            "Semua surah selesai!"
        }

        binding.tvFeedback.text =
            "🎉 Surah Selesai!\n" +
            "Skor Akhir: ${state.finalScore}\n" +
            "Best Streak: ${state.bestStreak}\n" +
            pesanUnlock
    }
}
