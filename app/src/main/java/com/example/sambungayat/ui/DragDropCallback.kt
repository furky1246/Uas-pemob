package com.example.sambungayat.ui

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragDropCallback(
    private val adapter: WordAdapter,
    private val onMoveFinished: (fromIndex: Int, toIndex: Int) -> Unit
) : ItemTouchHelper.Callback() {

    // Simpan posisi asal sebelum drag dimulai
    private var dragFromIndex: Int = -1

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // Izinkan drag ke atas dan bawah, tidak ada swipe
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition

        // Catat posisi awal drag (hanya sekali, saat pertama berpindah)
        if (dragFromIndex == -1) dragFromIndex = from

        adapter.moveItem(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Tidak ada aksi swipe
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        // Drag selesai — kirim posisi akhir ke ViewModel
        val toIndex = viewHolder.adapterPosition
        if (dragFromIndex != -1 && dragFromIndex != toIndex) {
            onMoveFinished(dragFromIndex, toIndex)
        }

        // Reset tracker
        dragFromIndex = -1
    }

    override fun isLongPressDragEnabled(): Boolean = false
}
