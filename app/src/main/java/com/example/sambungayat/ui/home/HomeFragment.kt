package com.example.sambungayat.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.sambungayat.R
import com.example.sambungayat.databinding.FragmentHomeBinding
import com.example.sambungayat.network.ApiResult
import com.example.sambungayat.network.SessionManager
import com.example.sambungayat.network.repository.UserRepository
import kotlinx.coroutines.launch
import android.widget.LinearLayout
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userRepository = UserRepository()
    private lateinit var sessionManager: SessionManager

    private val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        buildWeeklyDays()
        loadData()
    }

    private fun loadData() {
        val userId = sessionManager.getUserId()

        viewLifecycleOwner.lifecycleScope.launch {
            // Welcome name from session (instant)
            binding.tvWelcomeUser.text = "Assalamu Alaikum, ${sessionManager.getUsername()}!"

            // Statistics
            when (val result = userRepository.getStatistics(userId)) {
                is ApiResult.Success -> {
                    binding.tvTotalScore.text = result.data.totalScore.toString()
                    binding.tvBestStreak.text = result.data.bestStreak.toString()
                }
                else -> {}
            }

            // Progress (current surah)
            when (val result = userRepository.getProgress(userId)) {
                is ApiResult.Success -> {
                    val d = result.data
                    binding.tvContinueSurah.text = "Juz — Surah ${d.currentSurah}, Ayat ${d.currentVerse}"
                    val pct = ((d.currentSurah.toFloat() / 114f) * 100).toInt()
                    binding.progressContinue.progress = pct
                }
                else -> {}
            }
        }
    }

    private fun buildWeeklyDays() {
        val container = binding.layoutWeeklyDays
        container.removeAllViews()

        val today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        val todayIndex = (today + 5) % 7 // Map Sunday=1 → 6, Monday=2 → 0

        days.forEachIndexed { index, day ->
            val tv = TextView(requireContext()).apply {
                text = day
                textSize = 11f
                setTextColor(
                    if (index == todayIndex) Color.WHITE
                    else Color.parseColor("#404944")
                )
                setBackgroundResource(
                    if (index == todayIndex) R.drawable.bg_day_active
                    else R.drawable.bg_day_inactive
                )
                setPadding(12, 16, 12, 16)
                gravity = android.view.Gravity.CENTER
            }

            val params = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
                marginEnd = if (index < days.size - 1) 4 else 0
            }
            tv.layoutParams = params
            container.addView(tv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
