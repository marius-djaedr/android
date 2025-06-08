package com.me.music.service

import android.media.MediaPlayer
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.os.Looper

class PlaybackService(val mediaPlayer: MediaPlayer) : _iPlaybackService{

    override fun currentPercent(): Float {
        return mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
    }

    override fun currentPositionText(): String {
        return formatTime(mediaPlayer.currentPosition)
    }

    override fun durationText(): String {
        return formatTime(mediaPlayer.duration)
    }

    override fun seekTo(pct: Float) {
        val msec = mediaPlayer.duration.toFloat() * pct
        mediaPlayer.seekTo(msec.toInt())
    }

    override fun playPause(isPlaying: Boolean) {
        if(isPlaying){
            mediaPlayer.pause()
        }else{
            mediaPlayer.start()
        }
    }

    // Handler to update SeekBar and current time text every second
    private val handler = Handler(Looper.getMainLooper())
    private var updateProgress: ((Float, String) -> Unit)? = null

    override fun setProgressFunction(func:(Float, String) -> Unit){
        updateProgress = func
        handler.post(updateSeekBar)
    }

    // Runnable task that updates SeekBar and current playback time
    private val updateSeekBar: Runnable = object : Runnable {
        override fun run() {
            // Update SeekBar progress and current time text
            if(updateProgress != null){
                updateProgress?.invoke(currentPercent(), currentPositionText())
            }

            // Repeat this task every 1 second
            handler.postDelayed(this, 1000)
        }
    }


    // Format milliseconds into minutes:seconds format (e.g., 1:05)
    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}