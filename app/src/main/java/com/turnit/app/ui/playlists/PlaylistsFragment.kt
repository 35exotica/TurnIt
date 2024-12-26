package com.turnit.app.ui.playlists

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.turnit.app.Playlists
import com.turnit.app.R

class PlaylistsFragment : Fragment() {

    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var playlistAdapter: PlaylistAdapter
    private val playlists = mutableListOf<Playlists>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_playlists, container, false)

        playlistRecyclerView = root.findViewById(R.id.playlistRecyclerView)
        val createPlaylistFAB: FloatingActionButton = root.findViewById(R.id.createPlaylistFAB)

        // Инициализация адаптера
        playlistAdapter = PlaylistAdapter(
            playlists,
            onPlaylistClick = { playlist ->
                // Обработчик клика на плейлист
            },
            onDeleteClick = { playlist ->
                // Обработчик удаления плейлиста
                deletePlaylist(playlist)
            }
        )

        playlistRecyclerView.layoutManager = LinearLayoutManager(context)
        playlistRecyclerView.adapter = playlistAdapter

        // Загружаем плейлисты
        loadPlaylists()

        // Обработчик нажатия на кнопку создания плейлиста
        createPlaylistFAB.setOnClickListener {
            showCreatePlaylistDialog()
        }

        return root
    }

    private fun showCreatePlaylistDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Создать новый плейлист")

        // Создаем View для диалога
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_playlist, null)
        builder.setView(dialogView)

        val nameEditText: EditText = dialogView.findViewById(R.id.nameEditText)
        val descriptionEditText: EditText = dialogView.findViewById(R.id.descriptionEditText)

        builder.setPositiveButton("Создать") { dialog, _ ->
            val name = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            if (name.isNotEmpty() && description.isNotEmpty()) {
                createPlaylist(name, description)
            } else {
                Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun createPlaylist(name: String, description: String) {
        // Получаем текущего пользователя
        val currentUser = Backendless.UserService.CurrentUser()
        val ownerId = currentUser?.objectId ?: "unknown"

        val newPlaylist = Playlists(name = name, description = description, ownerId = ownerId)

        Backendless.Data.of(Playlists::class.java).save(newPlaylist, object : AsyncCallback<Playlists> {
            override fun handleResponse(response: Playlists) {
                playlists.add(response)
                playlistAdapter.notifyDataSetChanged()
                Log.d("Backendless", "Playlist saved with objectId: ${response.objectId}")
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.e("Backendless", "Error saving playlist: ${fault.message}")
            }
        })
    }

    private fun loadPlaylists() {
        // Получаем текущего пользователя
        val currentUser = Backendless.UserService.CurrentUser()
        val ownerId = currentUser?.objectId ?: "unknown"

        val dataQuery = DataQueryBuilder.create()
        dataQuery.whereClause = "ownerId = '$ownerId'"

        Backendless.Data.of(Playlists::class.java).find(dataQuery, object : AsyncCallback<List<Playlists>> {
            override fun handleResponse(response: List<Playlists>) {
                playlists.clear()
                playlists.addAll(response)
                playlistAdapter.notifyDataSetChanged()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.e("Backendless", "Error loading playlists: ${fault.message}")
            }
        })
    }

    private fun deletePlaylist(playlist: Playlists) {
        val objectId = playlist.objectId
        if (objectId != null) {
            Backendless.Data.of(Playlists::class.java).remove("objectId = '$objectId'", object : AsyncCallback<Int> {
                override fun handleResponse(response: Int?) {
                    playlists.remove(playlist)
                    playlistAdapter.notifyDataSetChanged()
                    Log.d("Backendless", "Playlist deleted")
                }

                override fun handleFault(fault: BackendlessFault) {
                    Log.e("Backendless", "Error deleting playlist: ${fault.message}")
                }
            })
        } else {
            Log.e("Backendless", "Playlist objectId is null")
        }
    }
}
