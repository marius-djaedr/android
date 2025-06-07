package com.me.music.service

import android.media.MediaPlayer
import java.util.concurrent.TimeUnit

class _mPlaybackService : _iPlaybackService{
    private var currentPct = 0.4f
    private val duration = 10*60*1000

    override fun currentPercent(): Float {
        return currentPct
    }

    override fun currentPositionText(): String {
        val msec = duration.toFloat() * currentPct
        return formatTime(msec.toInt())
    }

    override fun durationText(): String {
        return formatTime(duration)
    }


    override fun seekTo(pct: Float) {
        currentPct = pct
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    // Format milliseconds into minutes:seconds format (e.g., 1:05)
    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}