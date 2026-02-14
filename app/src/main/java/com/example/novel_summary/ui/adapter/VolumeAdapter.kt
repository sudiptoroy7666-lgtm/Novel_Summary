package com.example.novel_summary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novel_summary.R
import com.example.novel_summary.data.model.Volume

class VolumeAdapter(
    private val onItemClick: (Volume) -> Unit,
    private val onItemLongClick: (Volume) -> Boolean
) : RecyclerView.Adapter<VolumeAdapter.VolumeViewHolder>() {

    private val volumeList = mutableListOf<Volume>()

    class VolumeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val volumeNameTextView: TextView = view.findViewById(R.id.tvNovelName)
        val chapterCountTextView: TextView = view.findViewById(R.id.tvNovelVolumeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolumeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_novel, parent, false)
        return VolumeViewHolder(view)
    }

    override fun onBindViewHolder(holder: VolumeViewHolder, position: Int) {
        val volume = volumeList[position]

        holder.volumeNameTextView.text = volume.volumeName
        holder.chapterCountTextView.text = "Loading chapters..."

        holder.itemView.setOnClickListener { onItemClick(volume) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(volume)
            true
        }
    }

    override fun getItemCount() = volumeList.size

    fun submitList(volumes: List<Volume>) {
        volumeList.clear()
        volumeList.addAll(volumes)
        notifyDataSetChanged()
    }

    fun clearList() {
        volumeList.clear()
        notifyDataSetChanged()
    }

    // Add this method to access items by position
    fun getItem(position: Int): Volume {
        return volumeList[position]
    }
}