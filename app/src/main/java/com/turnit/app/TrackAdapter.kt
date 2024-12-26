package com.turnit.app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.backendless.files.BackendlessFile

class TrackAdapter(
    private var tracks: List<Tracks>,
    private val onTrackClick: (Tracks, Int) -> Unit // Лямбда-функция для передачи индекса
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    // ViewHolder для элементов списка
    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackName: TextView = itemView.findViewById(R.id.tvTrackName)
        val trackArtist: TextView = itemView.findViewById(R.id.tvTrackArtist)
        val trackImage: ImageView = itemView.findViewById(R.id.tvTrackIMG)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]

        // Устанавливаем текстовые данные
        holder.trackName.text = track.name ?: "Без названия"
        holder.trackArtist.text = track.creator ?: "Неизвестный исполнитель"

        // Загружаем изображение обложки
        val coverImage = track.coverImage
        coverImage?.let {
            val imageUrl = coverImage.fileURL // Получаем URL изображения из BackendlessFile
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.trackImage)
        }

        // Устанавливаем обработчик кликов
        holder.itemView.setOnClickListener {
            // Передаем выбранный трек и его индекс через лямбда-функцию
            onTrackClick(track, position)
        }
    }



    // Метод для обновления списка треков
    fun updateTracks(newTracks: List<Tracks>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}
