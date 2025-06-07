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

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun stop() {
        mediaPlayer.stop()
    }


    /** Iterate the progress value */
    override suspend fun loadProgress(updateProgress: (Float) -> Unit) {
        val handler = Handler(Looper.getMainLooper())

        val updateSeekBar: Runnable = object : Runnable {
            override fun run() {
                updateProgress(currentPercent())

                // Repeat this task every 1 second
                handler.postDelayed(this, 1000)
            }
        }

    }


    // Format milliseconds into minutes:seconds format (e.g., 1:05)
    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}