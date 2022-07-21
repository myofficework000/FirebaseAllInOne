package com.learning.b38firebasedemo.realtimedb.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.b38firebasedemo.databinding.NotesItemListBinding

class NotesAdapter(private val arrayList: ArrayList<Notes>) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    lateinit var binding: NotesItemListBinding

    override fun getItemCount(): Int = arrayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = NotesItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Notes) {
            val remove = item.date.replace("/", " ")
            val date = remove.substring(0, 6)
            binding.apply {
                txtNoteTitle.text = item.title
                txtNoteDate.text = date
                txtNoteDesc.text = item.description
            }
        }
    }
}