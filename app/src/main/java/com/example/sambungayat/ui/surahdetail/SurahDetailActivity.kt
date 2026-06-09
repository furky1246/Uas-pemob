package com.example.sambungayat.ui.surahdetail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sambungayat.databinding.ActivitySurahDetailBinding
import com.example.sambungayat.network.ApiResult
import com.example.sambungayat.network.repository.QuranRepository
import com.example.sambungayat.ui.game.GameActivity
import kotlinx.coroutines.launch

class SurahDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurahDetailBinding
    private val repository = QuranRepository()
    private var adapter: VerseDetailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurahDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chapterId   = intent.getIntExtra("chapter_id", 1)
        val chapterName = intent.getStringExtra("chapter_name") ?: ""

        binding.tvSurahName.text = chapterName
        binding.btnBack.setOnClickListener { finish() }

        binding.rvVerses.layoutManager = LinearLayoutManager(this)

        loadSurahDetail(chapterId, chapterName)
    }

    private fun loadSurahDetail(chapterId: Int, chapterName: String) {
        binding.loadingBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            when (val result = repository.getSurahDetail(chapterId)) {
                is ApiResult.Success -> {
                    binding.loadingBar.visibility = View.GONE
                    val data = result.data

                    binding.tvSurahName.text  = data.surah.name
                    binding.tvVerseCount.text = "${data.surah.verseCount} Verses"

                    val pct = 0 // Progress hafalan bisa dikembangkan nanti
                    binding.progressSurah.progress  = pct
                    binding.tvProgressPercent.text  = "$pct% Completed"

                    adapter = VerseDetailAdapter(data.verses)
                    binding.rvVerses.adapter = adapter

                    binding.btnStartGame.setOnClickListener {
                        val intent = Intent(this@SurahDetailActivity, GameActivity::class.java).apply {
                            putExtra(GameActivity.EXTRA_CHAPTER_ID,   data.surah.id)
                            putExtra(GameActivity.EXTRA_CHAPTER_NAME, data.surah.name)
                            putExtra(GameActivity.EXTRA_VERSE_COUNT,  data.surah.verseCount)
                        }
                        startActivity(intent)
                    }
                }
                is ApiResult.Error -> {
                    binding.loadingBar.visibility = View.GONE
                    Toast.makeText(this@SurahDetailActivity, result.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.release()
    }
}
