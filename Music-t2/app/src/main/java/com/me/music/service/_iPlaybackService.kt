package com.me.music.service

interface _iPlaybackService {
    fun currentPercent() :Float
    fun currentPositionText() :String
    fun durationText() :String

    fun seekTo(pct:Float)
    fun playPause(isPlaying:Boolean)

    fun setProgressFunction(func:(Float, String) -> Unit)
}