package com.example.sambungayat.ui.quran

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sambungayat.databinding.FragmentQuranBinding
import com.example.sambungayat.network.ApiResult
import com.example.sambungayat.network.repository.QuranRepository
import kotlinx.coroutines.launch

class QuranFragment : Fragment() {

    private var _binding: FragmentQuranBinding? = null
    private val binding get() = _binding!!
    private val quranRepository = QuranRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvJuz.layoutManager = GridLayoutManager(requireContext(), 2)
        loadJuz()
    }

    private fun loadJuz() {
        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = quranRepository.getJuz()) {
                is ApiResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val adapter = JuzAdapter(result.data) { juz ->
                        val intent = Intent(requireContext(), ChapterListActivity::class.java)
                        intent.putExtra("juz_id", juz.id)
                        intent.putExtra("juz_number", juz.number)
                        intent.putExtra("juz_name", juz.name)
                        startActivity(intent)
                    }
                    binding.rvJuz.adapter = adapter
                }
                is ApiResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
