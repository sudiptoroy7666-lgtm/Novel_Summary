package com.example.novel_summary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novel_summary.R
import com.example.novel_summary.data.model.Chapter
import java.text.SimpleDateFormat
import java.util.*

class ChapterAdapter(
    private val onItemClick: (Chapter) -> Unit,
    private val onItemLongClick: (Chapter) -> Boolean
) : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    private val chapterList = mutableListOf<Chapter>()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    class ChapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chapterNameTextView: TextView = view.findViewById(R.id.tvNovelName)
        val summaryTypeTextView: TextView = view.findViewById(R.id.tvNovelVolumeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_novel, parent, false)
        return ChapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = chapterList[position]

        holder.chapterNameTextView.text = chapter.chapterName
        holder.summaryTypeTextView.text = when (chapter.summaryType) {
            "short" -> "Short Summary"
            "detailed" -> "Detailed Summary"
            "very_detailed" -> "Very Detailed Summary"
            else -> "Summary"
        }

        holder.itemView.setOnClickListener { onItemClick(chapter) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(chapter)
            true
        }
    }

    override fun getItemCount() = chapterList.size

    fun submitList(chapters: List<Chapter>) {
        chapterList.clear()
        chapterList.addAll(chapters)
        notifyDataSetChanged()
    }

    fun clearList() {
        chapterList.clear()
        notifyDataSetChanged()
    }

    // Add this method to access items by position
    fun getItem(position: Int): Chapter {
        return chapterList[position]
    }
}