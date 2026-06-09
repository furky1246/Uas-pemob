package com.example.sambungayat.ui.quran

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sambungayat.databinding.ActivityChapterListBinding
import com.example.sambungayat.network.ApiResult
import com.example.sambungayat.network.repository.QuranRepository
import com.example.sambungayat.ui.surahdetail.SurahDetailActivity
import kotlinx.coroutines.launch

class ChapterListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChapterListBinding
    private val repository = QuranRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChapterListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val juzId     = intent.getIntExtra("juz_id", 1)
        val juzNumber = intent.getIntExtra("juz_number", 1)
        val juzName   = intent.getStringExtra("juz_name") ?: ""

        binding.tvJuzLabel.text = "JUZ $juzNumber"
        binding.tvJuzTitle.text = juzName
        binding.btnBack.setOnClickListener { finish() }

        binding.rvChapters.layoutManager = LinearLayoutManager(this)
        loadChapters(juzId)
    }

    private fun loadChapters(juzId: Int) {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            when (val result = repository.getChaptersByJuz(juzId)) {
                is ApiResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvChapters.adapter = ChapterAdapter(result.data) { chapter ->
                        val intent = Intent(this@ChapterListActivity, SurahDetailActivity::class.java)
                        intent.putExtra("chapter_id",   chapter.id)
                        intent.putExtra("chapter_name", chapter.name)
                        startActivity(intent)
                    }
                }
                is ApiResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@ChapterListActivity, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}
