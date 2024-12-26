package com.turnit.app.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.turnit.app.databinding.FragmentSearchBinding
import com.turnit.app.Tracks
import com.turnit.app.TrackAdapter
import com.turnit.app.MainActivity

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: TrackAdapter
    private val trackList = mutableListOf<Tracks>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val searchView = binding.searchView
        val hipHopButton = binding.hipHopButton
        val backToHashtagsButton = binding.backToHashtags
        val noResultsText = binding.noResultsText
        val recyclerViewTracks = binding.recyclerViewTracks

        val genreButtons = listOf(
            binding.hipHopButton to "#HipHop&Rap",
            binding.popButton to "#Pop",
            binding.rnbButton to "#Rnb",
            binding.partyButton to "#Party",
            binding.chillButton to "#Chill",
            binding.workoutButton to "#Workout",
            binding.technoButton to "#Techno",
            binding.houseButton to "#House",
            binding.feelGoodButton to "#FeelGood",
            binding.studyButton to "#Study",
            binding.folkButton to "#Folk",
            binding.indieButton to "#Indie",
            binding.soulButton to "#Soul",
            binding.countryButton to "#Country",
            binding.latinButton to "#Latin",
            binding.rockButton to "#Rock",
            binding.CoolButton to "#Cool"
        )

        genreButtons.forEach { (button, genre) ->
            button.setOnClickListener {
                val currentText = searchView.query.toString()
                val newText = if (currentText.contains(genre)) {
                    currentText
                } else {
                    "$currentText $genre"
                }
                searchView.setQuery(newText, false)
            }
        }


        // Инициализация RecyclerView и адаптера
        trackAdapter = TrackAdapter(trackList) { selectedTrack, position ->
            // Передаем выбранный трек и его индекс в MainActivity
            val coverImageUrl = selectedTrack.coverImage?.fileURL
            (activity as? MainActivity)?.playTrack(selectedTrack, coverImageUrl)
        }



        recyclerViewTracks.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewTracks.adapter = trackAdapter

        backToHashtagsButton.setOnClickListener {
            searchView.setQuery("", false)
            backToHashtagsButton.visibility = View.GONE
            noResultsText.visibility = View.GONE
            recyclerViewTracks.visibility = View.GONE

            hipHopButton.visibility = View.VISIBLE
            binding.popButton.visibility = View.VISIBLE
            binding.rnbButton.visibility = View.VISIBLE
            binding.partyButton.visibility = View.VISIBLE
            binding.chillButton.visibility = View.VISIBLE
            binding.workoutButton.visibility = View.VISIBLE
            binding.technoButton.visibility = View.VISIBLE
            binding.houseButton.visibility = View.VISIBLE
            binding.feelGoodButton.visibility = View.VISIBLE
            binding.studyButton.visibility = View.VISIBLE
            binding.folkButton.visibility = View.VISIBLE
            binding.indieButton.visibility = View.VISIBLE
            binding.soulButton.visibility = View.VISIBLE
            binding.countryButton.visibility = View.VISIBLE
            binding.latinButton.visibility = View.VISIBLE
            binding.rockButton.visibility = View.VISIBLE
            binding.CoolButton.visibility = View.VISIBLE
            binding.noneButton.visibility = View.VISIBLE

        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                hipHopButton.visibility = View.GONE
                binding.popButton.visibility = View.GONE
                binding.rnbButton.visibility = View.GONE
                binding.partyButton.visibility = View.GONE
                binding.chillButton.visibility = View.GONE
                binding.workoutButton.visibility = View.GONE
                binding.technoButton.visibility = View.GONE
                binding.houseButton.visibility = View.GONE
                binding.feelGoodButton.visibility = View.GONE
                binding.studyButton.visibility = View.GONE
                binding.folkButton.visibility = View.GONE
                binding.indieButton.visibility = View.GONE
                binding.soulButton.visibility = View.GONE
                binding.countryButton.visibility = View.GONE
                binding.latinButton.visibility = View.GONE
                binding.rockButton.visibility = View.GONE
                binding.CoolButton.visibility = View.GONE
                binding.noneButton.visibility = View.GONE

                backToHashtagsButton.visibility = View.VISIBLE
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return root
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            // Если строка поиска пуста, скрываем RecyclerView и текст "Нет результатов"
            binding.recyclerViewTracks.visibility = View.GONE
            binding.noResultsText.visibility = View.GONE
            return
        }

        val queryParts = query.split(" ").filter { it.isNotEmpty() }
        val whereClauses = mutableListOf<String>()

        queryParts.forEach { part ->
            when {
                part.startsWith("#") -> {
                    val hashtag = part.substring(1)
                    whereClauses.add("hashtags LIKE '%$hashtag%'")
                }
                part.startsWith("@") -> {
                    val author = part.substring(1)
                    whereClauses.add("creator LIKE '%$author%'")
                }
                else -> {
                    whereClauses.add("name LIKE '%$part%'")
                }
            }
        }

        // Собираем условия в одну строку
        val whereClause = whereClauses.joinToString(" AND ")

        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        queryBuilder.setPageSize(100)  // Устанавливаем лимит на 100 элементов
        println("WhereClause: $whereClause")

        Backendless.Data.of(Tracks::class.java).find(queryBuilder, object : AsyncCallback<List<Tracks>> {
            override fun handleResponse(foundTracks: List<Tracks>) {
                if (foundTracks.isEmpty()) {
                    binding.recyclerViewTracks.visibility = View.GONE
                    binding.noResultsText.visibility = View.VISIBLE
                } else {
                    binding.recyclerViewTracks.visibility = View.VISIBLE
                    binding.noResultsText.visibility = View.GONE
                    trackAdapter.updateTracks(foundTracks)
                    // Передаем список треков в MainActivity
                    (activity as? MainActivity)?.setTracks(foundTracks)
                }
            }

            override fun handleFault(fault: BackendlessFault) {
                println("Error: ${fault.message}")
            }
        })
    }
}

