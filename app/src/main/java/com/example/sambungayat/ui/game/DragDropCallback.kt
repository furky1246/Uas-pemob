package com.example.sambungayat.ui.game

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragDropCallback(
    private val adapter: WordAdapter,
    private val onMoveFinished: (fromIndex: Int, toIndex: Int) -> Unit
) : ItemTouchHelper.Callback() {

    private var dragFromIndex: Int = -1

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags  = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val from = viewHolder.adapterPosition
        val to   = target.adapterPosition
        if (dragFromIndex == -1) dragFromIndex = from
        adapter.moveItem(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val toIndex = viewHolder.adapterPosition
        if (dragFromIndex != -1 && dragFromIndex != toIndex) {
            onMoveFinished(dragFromIndex, toIndex)
        }
        dragFromIndex = -1
    }

    override fun isLongPressDragEnabled(): Boolean = false
}
