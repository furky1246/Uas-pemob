package com.example.sambungayat.ui.quran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sambungayat.databinding.ItemChapterCardBinding
import com.example.sambungayat.network.model.response.ChapterResponse

class ChapterAdapter(
    private val items: List<ChapterResponse>,
    private val onClick: (ChapterResponse) -> Unit
) : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    inner class ChapterViewHolder(val binding: ItemChapterCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = ItemChapterCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = items[position]
        holder.binding.tvChapterNumber.text = chapter.id.toString()
        holder.binding.tvChapterName.text   = chapter.name
        holder.binding.tvVerseCount.text    = "${chapter.verseCount} Verses"
        holder.binding.root.setOnClickListener { onClick(chapter) }
    }

    override fun getItemCount() = items.size
}
