package com.example.novel_summary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novel_summary.R
import com.example.novel_summary.data.model.Bookmark
import java.text.SimpleDateFormat
import java.util.*

class BookmarkAdapter(
    private val onItemClick: (Bookmark) -> Unit,
    private val onItemLongClick: (Bookmark) -> Boolean
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    private val bookmarkList = mutableListOf<Bookmark>()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

    class BookmarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.tvBookmarkTitle)
        val urlTextView: TextView = view.findViewById(R.id.tvBookmarkUrl)
        val timestampTextView: TextView = view.findViewById(R.id.tvBookmarkTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bookmark = bookmarkList[position]

        holder.titleTextView.text = bookmark.title.ifEmpty { "Untitled"
        }
        holder.urlTextView.text = bookmark.url
        holder.timestampTextView.text = dateFormat.format(Date(bookmark.timestamp))

        holder.itemView.setOnClickListener { onItemClick(bookmark) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(bookmark)
            true
        }
    }

    override fun getItemCount() = bookmarkList.size

    fun submitList(bookmarks: List<Bookmark>) {
        bookmarkList.clear()
        bookmarkList.addAll(bookmarks)
        notifyDataSetChanged()
    }

    fun clearList() {
        bookmarkList.clear()
        notifyDataSetChanged()
    }

    // Add this method to access items by position
    fun getItem(position: Int): Bookmark {
        return bookmarkList[position]
    }
}