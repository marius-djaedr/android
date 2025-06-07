package com.me.music.service

interface _iPlaybackService {
    fun currentPercent() :Float
    fun currentPositionText() :String
    fun durationText() :String

    fun seekTo(pct:Float)
    fun pause()
    fun play()
    fun stop()
}