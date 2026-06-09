package com.example.sambungayat.ui.game

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sambungayat.databinding.ActivityGameBinding
import com.example.sambungayat.gamelogic.GameState
import com.example.sambungayat.gamelogic.GameViewModel
import com.example.sambungayat.network.ApiResult
import com.example.sambungayat.network.SessionManager
import com.example.sambungayat.network.repository.QuranRepository
import com.example.sambungayat.network.repository.UserRepository
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CHAPTER_ID    = "chapter_id"
        const val EXTRA_CHAPTER_NAME  = "chapter_name"
        const val EXTRA_VERSE_COUNT   = "verse_count"
    }

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()

    private lateinit var wordAdapter: WordAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var sessionManager: SessionManager

    private val quranRepository = QuranRepository()
    private val userRepository  = UserRepository()

    private var chapterId   = 1
    private var chapterName = ""
    private var verseCount  = 0

    // List ayat yang diambil dari API
    private var ayatList: List<String> = emptyList()
    private var currentAyatIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        chapterId   = intent.getIntExtra(EXTRA_CHAPTER_ID, 1)
        chapterName = intent.getStringExtra(EXTRA_CHAPTER_NAME) ?: ""
        verseCount  = intent.getIntExtra(EXTRA_VERSE_COUNT, 0)

        binding.tvSurahName.text = chapterName
        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        setupObservers()
        setupListeners()
        fetchVersesAndStart()
    }

    // ------------------------------------------------------------------
    // FETCH AYAT DARI API
    // ------------------------------------------------------------------

    private fun fetchVersesAndStart() {
        setLoadingState(true)

        lifecycleScope.launch {
            when (val result = quranRepository.getVerses(chapterId)) {
                is ApiResult.Success -> {
                    ayatList = result.data
                        .sortedBy { it.verseNumber }
                        .mapNotNull { it.text }
                        .filter { it.isNotBlank() }

                    if (ayatList.isEmpty()) {
                        Toast.makeText(
                            this@GameActivity,
                            "Tidak ada ayat tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return@launch
                    }

                    setLoadingState(false)
                    viewModel.setTotalSurahInApp(114)
                    viewModel.startSurah(chapterId, ayatList.size)
                    loadCurrentAyat()
                }
                is ApiResult.Error -> {
                    setLoadingState(false)
                    Toast.makeText(this@GameActivity, result.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {}
            }
        }
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
                is GameState.Idle          -> handleIdle(state)
                is GameState.AnswerCorrect -> handleAnswerCorrect(state)
                is GameState.AnswerWrong   -> handleAnswerWrong()
                is GameState.SurahCompleted -> handleSurahCompleted(state)
            }
        }
    }

    private fun setupListeners() {
        binding.btnPeriksa.setOnClickListener { viewModel.checkAnswer() }
    }

    // ------------------------------------------------------------------
    // LOAD AYAT
    // ------------------------------------------------------------------

    private fun loadCurrentAyat() {
        if (currentAyatIndex < ayatList.size) {
            viewModel.loadAyat(ayatList[currentAyatIndex])
        }
    }

    // ------------------------------------------------------------------
    // STATE HANDLERS
    // ------------------------------------------------------------------

    private fun handleIdle(state: GameState.Idle) {
        binding.tvAyatProgress.text = "Ayat ${state.ayatNumber} dari ${state.totalAyat}"
        binding.cardFeedback.visibility = View.INVISIBLE
        binding.btnPeriksa.isEnabled = true

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
        binding.cardFeedback.visibility = View.VISIBLE
        binding.cardFeedback.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
        binding.tvFeedback.text =
            "✓ Benar!   Skor: ${state.score}   Combo: x${state.combo}   Streak: ${state.currentStreak}"
        binding.tvScore.text  = state.score.toString()
        binding.tvCombo.text  = "x${state.combo}"
        binding.tvStreak.text = state.currentStreak.toString()
        binding.btnPeriksa.isEnabled = false

        binding.root.postDelayed({
            currentAyatIndex++
            loadCurrentAyat()
        }, 1200)
    }

    private fun handleAnswerWrong() {
        binding.cardFeedback.visibility = View.VISIBLE
        binding.cardFeedback.setCardBackgroundColor(Color.parseColor("#FFDAD6"))
        binding.tvFeedback.text = "✗ Salah. Susun ulang dan coba lagi."
        binding.tvCombo.text  = "x0"
        binding.tvStreak.text = "0"
    }

    private fun handleSurahCompleted(state: GameState.SurahCompleted) {
        binding.cardFeedback.visibility = View.VISIBLE
        binding.cardFeedback.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
        binding.btnPeriksa.isEnabled = false

        val pesanUnlock = if (state.unlockedNextSurah != null)
            "Surah ${state.unlockedNextSurah} terbuka!"
        else
            "Semua surah selesai!"

        binding.tvFeedback.text =
            "🎉 Surah Selesai!\nSkor: ${state.finalScore}   Best Streak: ${state.bestStreak}\n$pesanUnlock"

        saveProgressToServer(state)
    }

    // ------------------------------------------------------------------
    // SAVE PROGRESS
    // ------------------------------------------------------------------

    private fun saveProgressToServer(state: GameState.SurahCompleted) {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        lifecycleScope.launch {
            userRepository.saveProgress(
                userId               = userId,
                totalScore           = state.finalScore,
                bestStreak           = state.bestStreak,
                highestUnlockedSurah = state.unlockedNextSurah ?: chapterId,
                currentSurah         = state.unlockedNextSurah ?: chapterId,
                currentVerse         = 1
            )
        }
    }

    // ------------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------------

    private fun setLoadingState(loading: Boolean) {
        binding.rvWords.visibility   = if (loading) View.GONE else View.VISIBLE
        binding.btnPeriksa.isEnabled = !loading
    }
}
