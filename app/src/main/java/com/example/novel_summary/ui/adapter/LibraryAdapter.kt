package com.example.novel_summary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novel_summary.R
import com.example.novel_summary.data.model.Novel

class LibraryAdapter(
    private val onItemClick: (Novel) -> Unit,
    private val onItemLongClick: (Novel) -> Boolean
) : RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    private val novelList = mutableListOf<Novel>()

    class LibraryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val novelNameTextView: TextView = view.findViewById(R.id.tvNovelName)
        val volumeCountTextView: TextView = view.findViewById(R.id.tvNovelVolumeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_novel, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val novel = novelList[position]

        holder.novelNameTextView.text = novel.name
        holder.volumeCountTextView.text = "Loading volumes..."

        holder.itemView.setOnClickListener { onItemClick(novel) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(novel)
            true
        }
    }

    override fun getItemCount() = novelList.size

    fun submitList(novels: List<Novel>) {
        novelList.clear()
        novelList.addAll(novels)
        notifyDataSetChanged()
    }

    fun clearList() {
        novelList.clear()
        notifyDataSetChanged()
    }

    // Add this method to access items by position
    fun getItem(position: Int): Novel {
        return novelList[position]
    }
}