package com.turnit.app.ui.playlists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.turnit.app.R
import com.turnit.app.Playlists

class PlaylistAdapter(
    private val playlists: List<Playlists>,
    private val onPlaylistClick: (Playlists) -> Unit,
    private val onDeleteClick: (Playlists) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.playlistNameText)
        val description: TextView = itemView.findViewById(R.id.playlistDescriptionText)
        val deleteButton: View = itemView.findViewById(R.id.deleteButton)

        init {
            itemView.setOnClickListener {
                onPlaylistClick(playlists[adapterPosition])
            }

            deleteButton.setOnClickListener {
                onDeleteClick(playlists[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.name.text = playlist.name
        holder.description.text = playlist.description
    }

    override fun getItemCount(): Int = playlists.size
}
