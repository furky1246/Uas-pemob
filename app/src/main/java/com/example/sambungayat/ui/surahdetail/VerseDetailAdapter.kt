package com.example.sambungayat.ui.surahdetail

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sambungayat.databinding.ItemVerseDetailBinding
import com.example.sambungayat.network.model.response.VerseDetail

class VerseDetailAdapter(
    private val items: List<VerseDetail>
) : RecyclerView.Adapter<VerseDetailAdapter.VerseViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null

    inner class VerseViewHolder(val binding: ItemVerseDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseViewHolder {
        val binding = ItemVerseDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VerseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerseViewHolder, position: Int) {
        val verse = items[position]
        holder.binding.tvVerseNumber.text = verse.verseNumber.toString()
        holder.binding.tvArabic.text      = verse.arabic ?: ""

        // Tafsir toggle
        if (verse.tafsir.isNullOrEmpty()) {
            holder.binding.btnToggleTafsir.visibility = View.GONE
        } else {
            holder.binding.btnToggleTafsir.visibility = View.VISIBLE
            holder.binding.btnToggleTafsir.setOnClickListener {
                val isVisible = holder.binding.tvTafsir.visibility == View.VISIBLE
                holder.binding.tvTafsir.visibility  = if (isVisible) View.GONE else View.VISIBLE
                holder.binding.btnToggleTafsir.text = if (isVisible) "SEE TAFSIR" else "HIDE TAFSIR"
            }
            holder.binding.tvTafsir.text = verse.tafsir
        }

        // Audio play
        if (verse.audioUrl.isNullOrEmpty()) {
            holder.binding.btnAudio.visibility = View.GONE
        } else {
            holder.binding.btnAudio.visibility = View.VISIBLE
            holder.binding.btnAudio.setOnClickListener {
                playAudio(verse.audioUrl)
            }
        }
    }

    private fun playAudio(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { start() }
        }
    }

    override fun getItemCount() = items.size

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
