package com.example.sambungayat.ui.quran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sambungayat.databinding.ItemJuzCardBinding
import com.example.sambungayat.network.model.response.JuzResponse

class JuzAdapter(
    private val items: List<JuzResponse>,
    private val onClick: (JuzResponse) -> Unit
) : RecyclerView.Adapter<JuzAdapter.JuzViewHolder>() {

    inner class JuzViewHolder(val binding: ItemJuzCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JuzViewHolder {
        val binding = ItemJuzCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return JuzViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JuzViewHolder, position: Int) {
        val juz = items[position]
        holder.binding.tvJuzNumber.text = "JUZ ${juz.number}"
        holder.binding.tvJuzName.text = juz.name
        holder.binding.root.setOnClickListener { onClick(juz) }
    }

    override fun getItemCount() = items.size
}
