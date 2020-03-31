package com.example.takenotes.Controller

import androidx.recyclerview.widget.RecyclerView

open interface ItemTouchHelperListener {
    open fun onItemMove(from_position: Int, to_position: Int)
    open fun onItemSwipe(position: Int)
    open fun onLeftClick(position: Int, viewHolder: RecyclerView.ViewHolder)
    open fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder)
}
