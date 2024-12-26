package com.turnit.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.turnit.app.databinding.ActivityMainBinding
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.bumptech.glide.Glide
import com.turnit.app.TrackAdapter.TrackViewHolder

class MainActivity : AppCompatActivity() {
    private var currentTrackIndex = -1 // Индекс текущего трека
    private lateinit var binding: ActivityMainBinding
    private var exoPlayer: ExoPlayer? = null
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())

    private var isFirstTrackClick = true // Флаг для отслеживания первого нажатия
    private var isShuffleEnabled = false
    private var isRepeatEnabled = false
    private val tracks = mutableListOf<Tracks>() // Список треков, который будет передаваться

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Инициализация binding после setContentView
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка навигации
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        // Слушатели кнопок miniPlayer и expandedPlayer
        binding.miniPlayer.setOnClickListener {
            showExpandedPlayer()
        }

        binding.expandDownBTN.setOnClickListener {
            showMiniPlayer()
        }

        // Настройка кнопок управления
        binding.playTogle.setOnClickListener {
            togglePlayback()
        }

        binding.playTogleExpand.setOnClickListener {
            togglePlayback()
        }

        binding.skipFwdBTNexpand.setOnClickListener {
            playNextTrack()
        }

        binding.skipBackBTNexpand.setOnClickListener {
            playPreviousTrack()
        }

        binding.shuffleBTNexpand.setOnClickListener {
            toggleShuffleMode()
        }

        binding.repeatBTNexpand.setOnClickListener {
            toggleRepeatMode()
        }

        // Обновление прогресса SeekBar
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    exoPlayer?.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        // Извлекаем URL изображения из Intent
        val coverImageUrl = intent.getStringExtra("image_url")

        // Если URL изображения не пустой, загружаем его с помощью Glide в ImageView
        coverImageUrl?.let {
            Glide.with(this)
                .load(it) // Загружаем изображение из URL
                .into(binding.tvTrackIMGexpand) // Указываем ImageView, в который загружается изображение
        }

    }


    private fun showExpandedPlayer() {
        binding.miniPlayer.visibility = View.GONE
        binding.expandedPlayer.visibility = View.VISIBLE
        updateExpandedPlayerUI()
    }

    private fun showMiniPlayer() {
        binding.expandedPlayer.visibility = View.GONE
        binding.miniPlayer.visibility = View.VISIBLE
    }

    // Метод playTrack должен принимать track и coverImageUrl (тип String?)
    // Метод playTrack должен принимать track и coverImageUrl (тип String?)
    fun playTrack(track: Tracks, coverImageUrl: String?) {
        currentTrackIndex = tracks.indexOf(track)
        exoPlayer?.release()

        // Если это первый клик по треку, показываем miniPlayer
        if (isFirstTrackClick) {
            binding.miniPlayer.visibility = View.VISIBLE
            isFirstTrackClick = false // После первого клика флаг сбрасывается
        }

        exoPlayer = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri(track.music?.fileURL ?: return)
            setMediaItem(mediaItem)
            prepare()
            play()

            // Добавляем слушатель для отслеживания окончания трека
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        playNextTrack()
                    }
                }
            })
        }

        // Сброс иконки повторения в выключенное состояние
        binding.repeatBTNexpand.setImageResource(R.drawable.ic_repeat)

        isPlaying = true
        updatePlayButton()
        updateTrackInfo(track)
        startProgressUpdater()

        // Загружаем обложку трека, если она есть
        coverImageUrl?.let {
            Glide.with(this)
                .load(it)
                .into(binding.tvTrackIMGexpand)
        }
    }





    fun setTracks(newTracks: List<Tracks>) {
        tracks.clear()
        tracks.addAll(newTracks)
    }

    // Переключение на следующий трек

    fun playNextTrack() {
        binding.repeatBTNexpand.setImageResource(R.drawable.ic_repeat)
        if (currentTrackIndex + 1 < tracks.size) {
            val nextTrack = tracks[currentTrackIndex + 1]
            playTrack(nextTrack, nextTrack.coverImage?.fileURL)
        } else {
            // Если треков больше нет, останавливаем плеер
            exoPlayer?.stop()
            isPlaying = false
            updatePlayButton()
        }
    }


    private fun updateRepeatButtonUI() {
        val repeatIcon = if (isRepeatEnabled) {
            R.drawable.ic_repeat_pressed // Иконка включённого повтора
        } else {
            R.drawable.ic_repeat // Иконка выключенного повтора
        }
        binding.repeatBTNexpand.setImageResource(repeatIcon)
    }


    private fun toggleRepeatMode() {
        isRepeatEnabled = !isRepeatEnabled
        exoPlayer?.repeatMode = if (isRepeatEnabled) {
            ExoPlayer.REPEAT_MODE_ONE
        } else {
            ExoPlayer.REPEAT_MODE_OFF
        }
        updateRepeatButtonUI()
    }


    private fun toggleShuffleMode() {
        isShuffleEnabled = !isShuffleEnabled
        exoPlayer?.shuffleModeEnabled = isShuffleEnabled // Включаем/выключаем shuffle в ExoPlayer
        updateShuffleButtonUI()
    }


    private fun updateShuffleButtonUI() {
        val shuffleIcon = if (isShuffleEnabled) {
            R.drawable.ic_shuffle_pressed // Иконка включённого перемешивания
        } else {
            R.drawable.ic_shuffle // Иконка выключенного перемешивания
        }
        binding.shuffleBTNexpand.setImageResource(shuffleIcon)
    }


    // Переключение на предыдущий трек
    fun playPreviousTrack() {
        binding.repeatBTNexpand.setImageResource(R.drawable.ic_repeat)
        if (currentTrackIndex - 1 >= 0) {
            val previousTrack = tracks[currentTrackIndex - 1]
            val coverImageUrl = previousTrack.coverImage?.fileURL // Получаем URL обложки
            playTrack(previousTrack, coverImageUrl)
        }
    }


    private fun togglePlayback() {
        exoPlayer?.let {
            if (isPlaying) {
                it.pause()
            } else {
                it.play()
            }
            isPlaying = !isPlaying
            updatePlayButton()
        }
    }

    private fun updatePlayButton() {
        val playIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        binding.playTogle.setImageResource(playIcon)
        binding.playTogleExpand.setImageResource(playIcon)
    }

    private fun updateTrackInfo(track: Tracks) {
        binding.tvTrackNamePL.text = track.name
        binding.tvTrackArtistPL.text = track.creator

        binding.tvTrackNamePLexpand.text = track.name
        binding.tvTrackArtistPLexpand.text = track.creator
    }

    private fun updateExpandedPlayerUI() {
        // Получаем продолжительность трека
        val duration = exoPlayer?.duration ?: 0
        binding.seekBar.max = duration.toInt()
        binding.totalTime.text = formatTime(duration)
    }


    //тут
    private fun startProgressUpdater() {
        handler.post(object : Runnable {
            override fun run() {
                exoPlayer?.let {
                    val duration = exoPlayer?.duration ?: 0
                    val currentPosition = it.currentPosition
                    binding.seekBar.progress = currentPosition.toInt()
                    binding.currentTime.text = formatTime(currentPosition)
                    binding.seekBar.max = duration.toInt()
                    binding.totalTime.text = formatTime(duration)
                }
                handler.postDelayed(this, 1)
            }
        })
    }


    private fun formatTime(ms: Long): String {
        if (ms < 0) return "0:00"

        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60

        return String.format("%d:%02d", minutes, seconds)
    }


    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }
}