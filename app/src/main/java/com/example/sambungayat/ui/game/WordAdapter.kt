package com.example.sambungayat.ui.game

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sambungayat.databinding.ItemWordBinding

class WordAdapter(
    private val words: MutableList<String>,
    private val onDragStarted: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    inner class WordViewHolder(val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.binding.tvWord.text = words[position]
        holder.binding.root.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                onDragStarted(holder)
            }
            false
        }
    }

    override fun getItemCount(): Int = words.size

    fun moveItem(fromIndex: Int, toIndex: Int) {
        val word = words.removeAt(fromIndex)
        words.add(toIndex, word)
        notifyItemMoved(fromIndex, toIndex)
    }

    fun getWords(): List<String> = words.toList()
}
