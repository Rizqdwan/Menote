package com.example.noteapp.ui.notif

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.data.local.entities.NotifEntity
import com.example.noteapp.databinding.ItemNoteNotifBinding

class AdapterNotif : ListAdapter<NotifEntity, AdapterNotif.NotifViewHolder>(diffCallback) {

    inner class NotifViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemNoteNotifBinding.bind(view)


        fun bind(notif: NotifEntity) {
            var selectedColor: String
            with(binding) {
                // Bind data from NotifEntity to your views
                tvAction.text = notif.action
                tvMessage.text = notif.massage
                when (notif.action) {
                    "DELETE" -> {
                        selectedColor = "#8c2014"
                        tvAction.setTextColor(Color.parseColor(selectedColor))
                    }
                    "UPDATE" -> {
                        selectedColor = "#FFF78A"
                        tvAction.setTextColor(Color.parseColor(selectedColor))
                    }
                    "CREATE" -> {
                        selectedColor = "#527853"
                        tvAction.setTextColor(Color.parseColor(selectedColor))
                    }
                }
                tvDateTime.text = notif.dataTime
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterNotif.NotifViewHolder {
        val binding = ItemNoteNotifBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<NotifEntity>() {
            override fun areItemsTheSame(oldItem: NotifEntity, newItem: NotifEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NotifEntity, newItem: NotifEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

}