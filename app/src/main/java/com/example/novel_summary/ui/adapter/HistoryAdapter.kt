package com.example.novel_summary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novel_summary.R
import com.example.novel_summary.data.model.History
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val onItemClick: (History) -> Unit,
    private val onItemLongClick: (History) -> Boolean
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val historyList = mutableListOf<History>()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.tvHistoryTitle)
        val urlTextView: TextView = view.findViewById(R.id.tvHistoryUrl)
        val timestampTextView: TextView = view.findViewById(R.id.tvHistoryTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]

        holder.titleTextView.text = history.title.ifEmpty { "Untitled" }
        holder.urlTextView.text = history.url
        holder.timestampTextView.text = dateFormat.format(Date(history.timestamp))

        holder.itemView.setOnClickListener { onItemClick(history) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(history)
            true
        }
    }

    override fun getItemCount() = historyList.size

    fun submitList(history: List<History>) {
        historyList.clear()
        historyList.addAll(history)
        notifyDataSetChanged()
    }

    fun clearList() {
        historyList.clear()
        notifyDataSetChanged()
    }

    // Add this method to access items by position
    fun getItem(position: Int): History {
        return historyList[position]
    }
}